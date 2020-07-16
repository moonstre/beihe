package com.yiliao.chat.socket;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yiliao.chat.activity.ScrollLoginActivity;
import com.yiliao.chat.activity.WaitActorActivity;
import com.yiliao.chat.base.AppManager;
import com.yiliao.chat.base.BaseResponse;
import com.yiliao.chat.bean.ChatUserInfo;
import com.yiliao.chat.constant.ChatApi;
import com.yiliao.chat.constant.Constant;
import com.yiliao.chat.helper.SharedPreferenceHelper;
import com.yiliao.chat.net.AjaxCallback;
import com.yiliao.chat.net.NetCode;
import com.yiliao.chat.socket.domain.Mid;
import com.yiliao.chat.socket.domain.SocketResponse;
import com.yiliao.chat.socket.domain.UserLoginReq;
import com.yiliao.chat.util.LogUtil;
import com.yiliao.chat.util.ParamUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.lang.ref.WeakReference;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.im.android.api.JMessageClient;

/**
 * 连接的管理类
 * Created by lyf on 2017/5/20.
 */

public class ConnectManager {

    public static final String BROADCAST_ACTION = "com.yiliao.chat.socket";
    public static final String MESSAGE = "message";

    private ConnectConfig mConfig;//配置文件
    private WeakReference<Context> mContext;
    private NioSocketConnector mConnection;
    private IoSession mSession;
    private InetSocketAddress mAddress;

    //心跳包内容
    private static final String HEARTBEATREQUEST = "0x11";
    private static final String HEARTBEATRESPONSE = "01010";

    private ConnectService mConnectService;
    private boolean mMineConnect = false;

    ConnectManager(ConnectConfig mConfig, ConnectService connectService) {
        mConnectService = connectService;
        this.mConfig = mConfig;
        this.mContext = new WeakReference<>(mConfig.getContext());
        init();
    }

    private void init() {
        mAddress = new InetSocketAddress(mConfig.getIp(), mConfig.getPort());
        //创建连接对象
        mConnection = new NioSocketConnector();
        //设置连接地址
        mConnection.setDefaultRemoteAddress(mAddress);
        mConnection.getSessionConfig().setReadBufferSize(mConfig.getReadBufferSize());
        //设置过滤
        mConnection.getFilterChain().addLast("logger", new LoggingFilter());
        mConnection.getFilterChain().addLast("codec", new ProtocolCodecFilter(new ByteArrayCodecFactory(Charset.forName("UTF-8"))));//自定义解编码器
        //设置连接监听
        mConnection.setHandler(new DefaultHandler(mContext.get()));
    }

    private class DefaultHandler extends IoHandlerAdapter {

        private Context context;

        DefaultHandler(Context context) {
            this.context = context;
        }

        /**
         * 连接成功时回调的方法
         */
        @Override
        public void sessionOpened(IoSession session) throws Exception {
            super.sessionOpened(session);
            Log.i("aaa","sessionOpened");
            //向服务器注册当前用户
            ChatUserInfo chatUserInfo;
            if (AppManager.getInstance().getUserInfo() != null) {
                chatUserInfo = AppManager.getInstance().getUserInfo();
            } else {
                chatUserInfo = SharedPreferenceHelper.getAccountInfo(context);
            }
            if (chatUserInfo != null && chatUserInfo.t_id > 0) {
                UserLoginReq ui = new UserLoginReq();
                ui.setUserId(chatUserInfo.t_id);
                ui.setT_is_vip(chatUserInfo.t_is_vip);
                ui.setT_role(chatUserInfo.t_role);
                if (ui.getT_sex() != 2) {
                    ui.setT_sex(chatUserInfo.t_sex);
                }



                ui.setT_token(chatUserInfo.t_token);
                ui.setMid(30001);
                session.write(JSONObject.toJSONString(ui));

            }
            //当与服务器连接成功时,将我们的session保存到我们的session manager类中,从而可以发送消息到服务器
        }

        @Override
        public void sessionClosed(IoSession session) throws Exception {
            mSession = null;
            LogUtil.i("sessionClosed 关闭了");
            super.sessionClosed(session);
        }

        /**
         * 接收到消息时回调的方法
         */
        @Override
        public void messageReceived(IoSession session, Object message) throws Exception {
            if (context != null) {
                mMineConnect = true;
                String content = message.toString().trim();
                LogUtil.i("接收到的消息: " + content);
                if (content.equals(HEARTBEATREQUEST)) {//心跳消息
                    try {
                        session.write(HEARTBEATRESPONSE);
                        if (mConnectService != null) {
                            Log.i("aaa","sendChangeMessage");
                            mConnectService.sendChangeMessage();
                        }
                    } catch (Exception e) {

                        e.printStackTrace();
                    }
                    return;
                }
                Log.i("aaa","messageReceived"+content);
                //将接收到的消息利用广播发送出去
                Intent intent = new Intent(BROADCAST_ACTION);
                intent.putExtra(MESSAGE, content);
                context.sendBroadcast(intent);
                openPage(context, content);
            }
        }
    }

    /**
     * 跳转页面
     */
    private void openPage(Context context, String message) {
        try {
            SocketResponse response = JSON.parseObject(message, SocketResponse.class);
            if (response != null) {

                if (response.mid == Mid.CHAT_LINK || response.mid == Mid.USER_GET_INVITE) { //用户来视频了 用户收到邀请
                    int roomId = response.roomId;
                    int userId = response.connectUserId;
                    int satisy = response.satisfy;
                    Intent videoIntent = new Intent(context, WaitActorActivity.class);
                    videoIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    videoIntent.putExtra(Constant.ROOM_ID, roomId);
                    videoIntent.putExtra(Constant.PASS_USER_ID, userId);
                    if (response.mid == Mid.USER_GET_INVITE) {
                        videoIntent.putExtra(Constant.USER_HAVE_MONEY, satisy);
                    }
                    try {
                        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                                videoIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        pendingIntent.send();
                    } catch (PendingIntent.CanceledException e) {
                        e.printStackTrace();
                    }
                } else if (response.mid == Mid.VIDEO_CHAT_START_HINT) {//接通视频的时候,下发提示消息,存在内存中,
                } else if (response.mid == Mid.BIG_GIFT) {

                } else if (response.mid == Mid.EXIT) {
                    exit(context);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 与服务器连接的方法
     */
    boolean connect() {
        try {
            ConnectFuture mConnectFuture = mConnection.connect();
            mConnectFuture.awaitUninterruptibly();
            mSession = mConnectFuture.getSession();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return mSession != null;
    }

    /**
     * 断开连接的方法
     */
    void disConnect() {
        mConnection.dispose();
        mConnection = null;
        mSession = null;
        mAddress = null;
        mContext = null;
        mMineConnect = false;
    }

    boolean isSocketConnect() {
        return mSession != null && mSession.isConnected() && mMineConnect;
    }

    void setMineConnect() {
        mMineConnect = false;
    }


    /**
     * 退出
     */
    private void exit(final Context context) {
        try {

            //调用接口退出
            logoutMyService(String.valueOf(SharedPreferenceHelper.getAccountInfo(context).t_id));

            AppManager.getInstance().setUserInfo(null);
            ChatUserInfo chatUserInfo = new ChatUserInfo();
            chatUserInfo.t_sex = -1;
            chatUserInfo.t_id = 0;
            SharedPreferenceHelper.saveAccountInfo(context, chatUserInfo);

            //关闭service
            finishJob(context);

            //IM登出
            JMessageClient.logout();

            //极光
            JPushInterface.stopPush(context);

            Window window = ((Activity) context).getWindow();
            if (window != null) {
                window.getDecorView().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        Intent intent = new Intent(context, ScrollLoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra(Constant.ACCOUNT_OUT, true);
                        context.startActivity(intent);

                    }
                }, 700);
            } else {

                Intent intent = new Intent(context, ScrollLoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra(Constant.ACCOUNT_OUT, true);
                context.startActivity(intent);

            }
        } catch (Exception e) {

            e.printStackTrace();

            Intent intent = new Intent(context, ScrollLoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra(Constant.ACCOUNT_OUT, true);
            context.startActivity(intent);

        }
    }

    private void finishJob(Context context) {
        try {
            Intent connect = new Intent(context, ConnectService.class);
            context.stopService(connect);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Intent job = new Intent(context, WakeupService.class);
                context.stopService(job);

                JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
                if (jobScheduler != null) {
                    List<JobInfo> allPendingJobs = jobScheduler.getAllPendingJobs();
                    if (allPendingJobs.size() > 0) {
                        jobScheduler.cancel(1);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 调用接口退出
     */
    private void logoutMyService(String userId) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userId", userId);
        OkHttpUtils.post().url(ChatApi.LOGOUT)
                .addParams("param", ParamUtil.getParam(paramMap))
                .build().execute(new AjaxCallback<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response, int id) {
                if (response != null && response.m_istatus == NetCode.SUCCESS) {
                    LogUtil.i("登出服务器成功");
                }
            }
        });
    }

}
