package com.yiliao.chat.constant;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.tencent.cos.xml.common.Region;
import com.yiliao.chat.BuildConfig;
import com.yiliao.chat.util.FileUtil;

/*
 * Copyright (C) 2018
 * 版权所有
 *
 * 功能描述：常量工具类
 * 作者：
 * 创建时间：2018/6/19
 *
 * 修改人：
 * 修改描述：
 * 修改日期
 */
public class Constant {

    //-------------- 第三方 start---------------

    //祥云实名认证
    public static final String XY_KEY = "";
    public static final String XY_SECRET = "";

    //拓幻
    public static final String TI_KEY = "";
    //-------------- 第三方 end---------------

    //标题extra标志
    public static final String TITLE = "title";
    public static final String DYNAMIC_ID = "dynamic_id";
    public static final String ACTOR_ID = "actor_id";//主播的id
    public static final String FROM_WHERE = "from_where";//从哪里进入视频播放页面
    public static final String COVER_URL = "cover_url";//视频封面图url
    public static final String VIDEO_URL = "video_url";//短视频地址
    public static final String ROOM_ID = "room_id";//房间编号
    public static final String PASS_USER_ID = "pass_user_id";//视频聊天对方id
    public static final String FROM_TYPE = "from_type";//是用户发起聊天,还是主播接通聊天
    public static final int FROM_USER = 0;//是用户发起聊天
    public static final int FROM_ACTOR = 1;//还是主播接通聊天
    public static final int FROM_ACTOR_INVITE = 2;//是主播邀请用户
    public static final int FROM_USER_JOIN = 3;//是用户收到主要邀请加入房间
    public static final int FROM_ALBUM = 2;//从我的相册
    public static final int FROM_GIRL = 3;//从首页
    public static final int FROM_ACTOR_VIDEO = 4;//从主播视频
    public static final int FROM_ACTIVE = 5;//从动态视频
    public static final String PHONE_MODIFY = "phone_modify";//修改手机号
    public static final String IMAGE_URL = "image_url";//图片地址
    public static final String USER_HEAD_URL = "user_head_url";//用户头像图片地址
    public static final String MINE_HEAD_URL = "mine_head_url";//自己头像图片地址
    public static final String MINE_ID = "mine_id";//自己ID
    public static final String NICK_NAME = "nick_name";//自己nick
    public static final String RED_BEAN = "red_bean";//红包bean
    public static final String USER_HAVE_MONEY = "user_have_money";//用户是否有足够的钱
    public static final String FILE_ID = "file_id";//文件id
    public static final String RECOMMEND_TYPE = "recommend_type";//推荐 试看 新人
    public static final String YEAR = "year";//年
    public static final String MONTH = "month";//月
    public static final String POST_IMAGE_PATH = "post_image_path";//动态图片本地地址
    public static final String POST_VIDEO_URL = "post_video_url";//动态发布 本地视频地址
    public static final String POST_FILE_URI = "post_file_uri";//动态发布 本地文件地址uri
    public static final String PASS_TYPE = "pass_type";//动态发布类型
    public static final String POST_FROM = "post_from";//动态发布从哪里点击进来
    public static final int TYPE_IMAGE = 0x11;//动态发布类型 图片
    public static final int TYPE_VIDEO = 0x12;//动态发布类型  视频
    public static final int REQUEST_ALBUM_IMAGE_AND_VIDEO = 0x117;//动态发布请求matisee相册,用于从activeFragment进,返回到MainActivity
    public static final String ACTIVE_ID = "active_id";//动态id
    public static final String COMMENT_NUMBER = "comment_number";//动态评论数量
    public static final String CLICK_POSITION = "click_position";//动态图片点击那个位置
    public static final String ACTIVE_FILE_BEAN = "active_file_bean";//动态图片文件bean
    public static final String CHOOSED_POSITION = "choose_position";//选择的位置
    public static final String QUICK_ANCHOR_BEAN = "quick_anchor_bean";//速配主播bean
    public static final String JOIN_TYPE = "join_type";//加入类型
    public static final String CHAT_ROOM_ID = "chat_room_id";//聊天室ID
    public static final String USER_PHONE = "user_phone";//用户手机号
    public static final String USER_PHONE_UPDATE = "user_phone_update";//手机号更新
    public static final String ACCOUNT_OUT = "account_out";//账号被挤掉

    //关闭login页面广播
    public static final String FINISH_LOGIN_PAGE = "com.yiliao.chat.close";
    //关闭充值页面
    public static final String FINISH_CHARGE_PAGE = "com.yiliao.chat.chargeclose";
    //被封号广播
    public static final String BEEN_CLOSE = "been_close";//红包bean
    public static final String BEEN_CLOSE_DES = "been_close_des";//被封号描述
    public static final String BEEN_CLOSE_LOGIN_PAGE = "com.yiliao.chat.beenclose";
    //微信授权返回信息广播
    public static final String WECHAT_WITHDRAW_ACCOUNT = "com.yiliao.chat.account";
    public static final String WECHAT_NICK_INFO = "wechat_nick_info";
    public static final String WECHAT_HEAD_URL = "wechat_head_url";
    public static final String WECHAT_OPEN_ID = "wechat_open_id";
    //新人分享微信群成功广播关闭分享页面
    public static final String QUN_SHARE_QUN_CLOSE = "com.yiliao.chat.qunclose";

    //h5 url
    public static final String URL = "url";
    //权限申请
    public static final int REQUEST_PERMISSION_CODE = 0x01;//权限申请
    //图片选择
    public static final int REQUEST_CODE_CHOOSE = 0x02;
    //视频选择
    public static final int REQUEST_CODE_CHOOSE_VIDEO = 0x05;
    //u crop 裁剪请求码 封面图
    public static final int UCROP_REQUEST_CODE_COVER = 12;
    //头像选择
    public static final int REQUEST_CODE_CHOOSE_HEAD_IMG = 0x03;
    //u crop 裁剪请求码 头像
    public static final int UCROP_REQUEST_CODE_HEAD = 15;
    //拍照
    public static final int REQUEST_CODE_SHOOT_PICTURE = 11;
    //压缩后的文件目录
    public static final String AFTER_COMPRESS_DIR = FileUtil.YCHAT_DIR + "compress/";
    //缩放后的文件目录,认证页面
    public static final String VERIFY_AFTER_RESIZE_DIR = FileUtil.YCHAT_DIR + "verify/";
    //裁剪后的文件目录,个人中心头像
    public static final String HEAD_AFTER_SHEAR_DIR = FileUtil.YCHAT_DIR + "head/";
    //编辑资料裁剪后封面的文件
    public static final String COVER_AFTER_SHEAR_DIR = FileUtil.YCHAT_DIR + "cover/";
    //举报的目录
    public static final String REPORT_DIR = FileUtil.YCHAT_DIR + "report/";
    //更新存放apk的目录
    public static final String UPDATE_DIR = FileUtil.YCHAT_DIR + "update/";
    public static final String UPDATE_APK_NAME = "chatNew.apk";
    //二维码
    public static final String ER_CODE = FileUtil.YCHAT_DIR + "code/";
    //动态视频
    public static final String ACTIVE_VIDEO_DIR = FileUtil.YCHAT_DIR + "video/";
    //动态图片
    public static final String ACTIVE_IMAGE_DIR = FileUtil.YCHAT_DIR + "image/";
    public static final String BUNDLE_FACE_BEAUTIFICATION = "face_beautification.bundle";
    public static final String BUNDLE_LIGHT_MAKEUP = "light_makeup.bundle";


    /**
     * 获取版本号
     */
    public static String getVersion(Context context) {
            try {
                PackageManager manager = context.getPackageManager();
                PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
                return info.versionName;
            } catch (Exception e) {
                e.printStackTrace();
            }

        return null;
    }
    public static boolean showMainVideo() {
        switch (BuildConfig.CHANNEL) {
            case "meirenyu":
                return false;
            default:
                return true;
        }
    }
    public static boolean showMainRecommendUI() {
        switch (BuildConfig.CHANNEL) {
            case "meirenyu":
                return false;
            default:
                return true;
        }
    }

    public static boolean hideHomeNear() {
        switch (BuildConfig.CHANNEL) {
            default:
                return false;
        }
    }

    public static boolean hideHomeVideo() {
        switch (BuildConfig.CHANNEL) {

            case "meirenyu":
                return true;

            default:
                return false;
        }
    }

    public static boolean hideHomeNearAndNew(){
        switch (BuildConfig.CHANNEL){

            default:
                return false;
        }
    }
    public static boolean showSearch(){
        switch (BuildConfig.CHANNEL){

            default:
                return false;
        }
    }


    public static boolean Recommendrefresh(){
        switch (BuildConfig.CHANNEL){

            default:
                return false;
        }
    }

    public static boolean hideHomeActivity() {
        switch (BuildConfig.CHANNEL) {

            default:
                return true;
        }
    }



    public static String getTencentCloudRegion() {
        switch (BuildConfig.tencentCloudRegion) {
            case "Shanghai":
                return Region.AP_Shanghai.getRegion();
        }
        return "";
    }

    public static boolean showExtremeCharge() {
        switch (BuildConfig.FLAVOR) {

            default:
                return false;
        }
    }

    public static boolean showOnLineInRank() {
        switch (BuildConfig.CHANNEL) {
            case "meirenyu":
                return true;

            default:
                return false;
        }
    }

    public static boolean hideClickInRank() {
        switch (BuildConfig.CHANNEL) {
            case "meirenyu":
                return false;

            default:
                return false;
        }
    }

    public static boolean hideChatOnTelephone() {
        switch (BuildConfig.CHANNEL) {
            case "meirenyu":
                return true;

            default:
                return false;
        }
    }

    public static boolean hideActiveFragment() {
        switch (BuildConfig.CHANNEL) {
            case "meirenyu":
                return true;

            default:
                return false;
        }
    }

    public static boolean showTudiMoney() {
        switch (BuildConfig.CHANNEL) {
            case "meirenyu":
                return true;
            default:
                return false;
        }
    }


    public static boolean showHomeForFenDie() {
        switch (BuildConfig.CHANNEL) {
            case "meirenyu":
                return true;
            default:
                return false;
        }
    }

    public static boolean hideChatPicture() {
        switch (BuildConfig.CHANNEL) {
            case "meirenyu":
                return true;

            default:
                return false;
        }
    }

    public static boolean showGradeOnMine() {
        switch (BuildConfig.CHANNEL) {
            case "meirenyu":
                return true;

            default:
                return false;
        }
    }

    public static boolean hideVipOnMine() {
        switch (BuildConfig.CHANNEL) {
            case "meirenyu":
                return true;
            default:
                return false;
        }
    }


    public static boolean hideAgeOnMine() {
        switch (BuildConfig.CHANNEL) {
            case "meirenyu":
                return true;
            default:
                return false;
        }
    }


    public static boolean hideTusun() {
        switch (BuildConfig.CHANNEL) {
            case "meirenyu":
                return true;
            default:
                return false;
        }
    }

    public static boolean hideCompanyOnMine() {
        switch (BuildConfig.CHANNEL) {
            case "meirenyu":
                return true;
            default:
                return false;
        }
    }
    public static boolean showGoldOnChat() {
        switch (BuildConfig.CHANNEL) {
            case "meirenyu":
                return true;
            default:
                return false;
        }
    }

    public static boolean hideMineActive() {
        switch (BuildConfig.CHANNEL) {
            case "meirenyu":
                return true;

            default:
                return false;
        }
    }
    public static boolean hideMineInviteCode() {
        switch (BuildConfig.CHANNEL) {
            case "meirenyu":
                return true;
            default:
                return false;
        }
    }

    public static boolean hideActorInfoVideo() {
        switch (BuildConfig.CHANNEL) {
            case "meirenyu":
                return true;
            default:
                return false;
        }
    }


    public static boolean hideActorInfoClose() {
        switch (BuildConfig.CHANNEL) {
            case "meirenyu":
                return true;
            default:
                return false;
        }
    }

    public static boolean showSexOnMain() {
        switch (BuildConfig.CHANNEL) {
            case "meirenyu":
                return true;
            default:
                return false;
        }
    }

    public static boolean hideVipCharge() {
        switch (BuildConfig.CHANNEL) {
            case "meirenyu":
                return true;
            default:
                return false;
        }
    }


    public static boolean goToActorInfoFromHome() {
        switch (BuildConfig.CHANNEL) {
            case "meirenyu":
                return true;
            default:
                return false;
        }
    }


    public static boolean screenScreenshots() {
        switch (BuildConfig.CHANNEL) {
            case "meirenyu":
                return true;

            default:
                return false;
        }
    }

    public static boolean displaylive() {
        switch (BuildConfig.CHANNEL) {

            default:
                return false;
        }
    }


    public static boolean changeBgColor() {
        switch (BuildConfig.CHANNEL) {

            default:
                return false;
        }
    }


    public static boolean helpandues() {
        switch (BuildConfig.CHANNEL) {
            case "meirenyu":
                return true;

            default:
                return false;
        }
    }


    public static boolean displayMine() {
        switch (BuildConfig.CHANNEL) {

            default:
                return false;
        }
    }


    public static boolean isNewUser() {
        switch (BuildConfig.CHANNEL) {
            case "meirenyu":
                return true;

            default:
                return false;
        }
    }


    public static boolean displayInviteCode() {
        switch (BuildConfig.CHANNEL) {
            case "meirenyu":
                return true;


            default:
                return false;
        }
    }

    public static boolean displayHeadImage() {
        switch (BuildConfig.CHANNEL) {
            case "meirenyu":
                return true;

            default:
                return false;
        }
    }


    public static boolean hideFans(){
        switch (BuildConfig.CHANNEL) {

            default:
                return false;
        }
    }


    public static boolean hideApplyVerifyName() {
        switch (BuildConfig.CHANNEL) {
            case "meirenyu":
                return true;
            default:
                return false;
        }
    }


    public static boolean hideApplyActivity() {
        switch (BuildConfig.CHANNEL) {

            default:
                return false;
        }
    }

    public static boolean hideApplyMyFriends() {
        switch (BuildConfig.CHANNEL) {

            default:
                return false;
        }
    }


    public static boolean hideActivity(){
        switch (BuildConfig.CHANNEL){

                default:
                    return false;
        }
    }


    public static boolean hideMainVideo(){
        switch (BuildConfig.CHANNEL){

            default:
                return false;
        }
    }


    public static boolean hideMainTurntable(){
        switch (BuildConfig.CHANNEL){
            default:
                return false;
        }
    }

    public static boolean hideNearby(){
        switch (BuildConfig.CHANNEL){
            default:
                return false;
        }
    }


    public static boolean TypefaceColor(){
        switch (BuildConfig.CHANNEL){

            default:
                return false;
        }
    }


    public static boolean hideFour(){
        switch (BuildConfig.CHANNEL){

            default:
                return false;
        }
    }


    public static boolean showZBRecommend(){
        switch (BuildConfig.CHANNEL){

                default:
                    return false;
        }
    }

    public static boolean showGoddess(){
        switch (BuildConfig.CHANNEL){
            default:
                return false;
        }
    }


    public static boolean addType(){
        switch (BuildConfig.CHANNEL){
            case "meirenyu":
                return true;
            default:
                return false;
        }
    }
    /*
    *
    * 是否添加openinstall
    * */
    public static boolean showOpeninstall(){
        switch (BuildConfig.CHANNEL){
            case "meirenyu":
                return true;
            default:
                return false;
        }
    }

   
    public static boolean showExtension(){
        switch (BuildConfig.CHANNEL){
           
            default:
                return false;
        }
    }
}
