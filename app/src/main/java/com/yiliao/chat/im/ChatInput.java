package com.yiliao.chat.im;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yiliao.chat.R;
import com.yiliao.chat.adapter.ImChatFacePagerAdapter;
import com.yiliao.chat.constant.Constant;

import java.util.ArrayList;
import java.util.List;

/**
 * 聊天界面输入控件
 */
public class ChatInput extends RelativeLayout implements TextWatcher, View.OnClickListener {

    private ImageView btnVideo;
    private ImageView btnAdd, btnVoice, btnEmotion;
    private EditText editText;
    private boolean isSendVisible, isHoldVoiceBtn;
    private InputMode inputMode = InputMode.NONE;
    private ChatView chatView;
    private LinearLayout morePanel;
    private TextView voicePanel;

    //发送
    private TextView send;

    private LinearLayout emoticonPanel;
    private ViewPager viewPager;
    private RadioGroup radioGroup;
    private ImChatFacePagerAdapter imChatFacePagerAdapter;
    private final int REQUEST_CODE_ASK_PERMISSIONS = 100;

    public ChatInput(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.chat_input, this);
        initView();
    }

    private void initView() {
        btnAdd = findViewById(R.id.btn_add);
        if (Constant.hideChatPicture()) {
            btnAdd.setVisibility(GONE);
        }
        btnAdd.setOnClickListener(this);
        btnVoice = findViewById(R.id.btn_voice);
        btnVoice.setOnClickListener(this);
        btnEmotion = findViewById(R.id.btnEmoticon);
        btnEmotion.setOnClickListener(this);
        btnVideo = findViewById(R.id.btnVideo);
        btnVideo.setOnClickListener(this);
        morePanel = findViewById(R.id.morePanel);
        send = findViewById(R.id.send);
        send.setOnClickListener(this);
        LinearLayout BtnImage = findViewById(R.id.btn_photo);
        BtnImage.setOnClickListener(this);
        LinearLayout BtnPhoto = findViewById(R.id.btn_image);
        BtnPhoto.setOnClickListener(this);
//        LinearLayout btnVideo = findViewById(R.id.btn_video);
//        btnVideo.setOnClickListener(this);
        LinearLayout btnFile = findViewById(R.id.btn_file);
        btnFile.setOnClickListener(this);
        voicePanel = findViewById(R.id.voice_panel);
        voicePanel.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        isHoldVoiceBtn = true;
                        updateVoiceView();
                        break;
                    case MotionEvent.ACTION_UP:
                        isHoldVoiceBtn = false;
                        updateVoiceView();
                        break;
                }
                return true;
            }
        });
        editText = findViewById(R.id.input);
        editText.addTextChangedListener(this);
        editText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    updateView(InputMode.TEXT);
                }
            }
        });
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    chatView.sendText();
                    return true;
                }
                return false;
            }
        });


        editText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                updateView(InputMode.TEXT);
            }
        });
        isSendVisible = editText.getText().length() != 0;
        emoticonPanel = findViewById(R.id.emoticonPanel);
        viewPager = findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(10);
        radioGroup = findViewById(R.id.radio_group);
        imChatFacePagerAdapter = new ImChatFacePagerAdapter(getContext(), new ImChatFacePagerAdapter.OnFaceClickListener() {
            @Override
            public void onFaceClick(String str, int faceImageRes) {
                if (editText != null) {
                    Editable editable = editText.getText();
                    editable.insert(editText.getSelectionStart(), str);
                }
            }

            @Override
            public void onFaceDeleteClick() {
                int keyCode = KeyEvent.KEYCODE_DEL;
                KeyEvent keyEventDown = new KeyEvent(KeyEvent.ACTION_DOWN, keyCode);
                KeyEvent keyEventUp = new KeyEvent(KeyEvent.ACTION_UP, keyCode);
                editText.onKeyDown(keyCode, keyEventDown);
                editText.onKeyUp(keyCode, keyEventUp);
            }
        });
        viewPager.setAdapter(imChatFacePagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                ((RadioButton) radioGroup.getChildAt(position)).setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        LayoutInflater inflater = LayoutInflater.from(getContext());
        for (int i = 0, pageCount = imChatFacePagerAdapter.getCount(); i < pageCount; i++) {
            RadioButton radioButton = (RadioButton) inflater.inflate(R.layout.view_chat_indicator, radioGroup, false);
            radioButton.setId(i + 10000);
            if (i == 0) {
                radioButton.setChecked(true);
            }
            radioGroup.addView(radioButton);
        }
    }

    private void updateView(InputMode mode) {
        if (mode == inputMode) return;
        leavingCurrentState();
        switch (inputMode = mode) {
            case MORE:
                morePanel.setVisibility(VISIBLE);
                break;
            case TEXT:
                if (editText.requestFocus()) {
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
                    }
                }
                break;
            case VOICE:
                voicePanel.setVisibility(VISIBLE);
                btnVoice.setVisibility(GONE);
                break;
            case EMOTICON:
                emoticonPanel.setVisibility(VISIBLE);
                break;
        }
    }

    private void leavingCurrentState() {
        switch (inputMode) {
            case TEXT:
                View view = ((Activity) getContext()).getCurrentFocus();
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                editText.clearFocus();
                break;
            case MORE:
                morePanel.setVisibility(GONE);
                break;
            case VOICE:
                voicePanel.setVisibility(GONE);
                btnVoice.setVisibility(VISIBLE);
                break;
            case EMOTICON:
                emoticonPanel.setVisibility(GONE);
        }
    }


    private void updateVoiceView() {
        if (isHoldVoiceBtn) {
            voicePanel.setText(getResources().getString(R.string.chat_release_send));
            voicePanel.setBackground(getResources().getDrawable(R.drawable.btn_voice_pressed));
            //chatView.startSendVoice();
        } else {
            voicePanel.setText(getResources().getString(R.string.chat_press_talk));
            voicePanel.setBackground(getResources().getDrawable(R.drawable.btn_voice_normal));
            //chatView.endSendVoice();
        }
    }

    /**
     * 关联聊天界面逻辑
     */
    public void setChatView(ChatView chatView) {
        this.chatView = chatView;
    }

    /**
     * This method is called to notify you that, within <code>s</code>,
     * the <code>count</code> characters beginning at <code>start</code>
     * are about to be replaced by new text with length <code>after</code>.
     * It is an error to attempt to make changes to <code>s</code> from
     * this callback.
     */
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    /**
     * This method is called to notify you that, within <code>s</code>,
     * the <code>count</code> characters beginning at <code>start</code>
     * have just replaced old text that had length <code>before</code>.
     * It is an error to attempt to make changes to <code>s</code> from
     * this callback.
     */
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        isSendVisible = s != null && s.length() > 0;
        if (isSendVisible) {
            chatView.sending();
        }
    }

    /**
     * This method is called to notify you that, somewhere within
     * <code>s</code>, the text has been changed.
     * It is legitimate to make further changes to <code>s</code> from
     * this callback, but be careful not to get yourself into an infinite
     * loop, because any changes you make will cause this method to be
     * called again recursively.
     * (You are not told where the change took place because other
     * afterTextChanged() methods may already have made other changes
     * and invalidated the offsets.  But if you need to know here,
     * you can use {@link Spannable#setSpan} in {@link #onTextChanged}
     * to mark your place and then look up from here where the span
     * ended up.
     */
    @Override
    public void afterTextChanged(Editable s) {
        if (TextUtils.isEmpty(s.toString())) {
            send.setVisibility(View.GONE);
            if (!Constant.hideChatPicture()) {
                btnAdd.setVisibility(VISIBLE);
            }
        } else {
            send.setVisibility(View.VISIBLE);
            if (!Constant.hideChatPicture()) {
                btnAdd.setVisibility(GONE);
            }
        }
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        Activity activity = (Activity) getContext();
        int id = v.getId();
        if (id == R.id.btn_add) {
            updateView(inputMode == InputMode.MORE ? InputMode.TEXT : InputMode.MORE);
        }
        if (id == R.id.btn_photo) {
            if (activity != null && requestCamera(activity)) {
                chatView.sendPhoto();
            }
        }
        if (id == R.id.btn_image) {
            if (activity != null && requestStorage(activity)) {
                chatView.sendImage();
            }
        }
        if (id == R.id.btn_voice) {
            /*if (activity != null && requestAudio(activity)) {
                updateView(InputMode.VOICE);
            }*/
            //updateView(inputMode == InputMode.EMOTICON ? InputMode.TEXT : InputMode.EMOTICON);
            chatView.sendRed();
        }
//        if (id == R.id.btn_video) {
//            if (getContext() instanceof FragmentActivity) {
//                FragmentActivity fragmentActivity = (FragmentActivity) getContext();
//                if (requestVideo(fragmentActivity)) {
//                    //VideoInputDialog.show(fragmentActivity.getSupportFragmentManager());
//                }
//            }
//        }
        if (id == R.id.btnEmoticon) {
            updateView(inputMode == InputMode.EMOTICON ? InputMode.TEXT : InputMode.EMOTICON);
        }
        if (id == R.id.btnVideo) {
            chatView.sendVideo();
        }
        if (id == R.id.btn_file) {
            chatView.sendRed();
        }

        if (id == R.id.send) {
            chatView.sendText();
        }

    }


    /**
     * 获取输入框文字
     */
    public Editable getText() {
        return editText.getText();
    }

    /**
     * 设置输入框文字
     */
    public void setText(String text) {
        editText.setText(text);
    }

    /**
     * 获取EditText
     */
    public EditText getEditText() {
        return editText;
    }

    /**
     * 设置输入模式
     */
    public void setInputMode(InputMode mode) {
        updateView(mode);
    }


    public enum InputMode {
        TEXT,
        VOICE,
        EMOTICON,
        MORE,
        VIDEO,
        NONE,
    }

    private boolean requestVideo(Activity activity) {
        if (afterM()) {
            final List<String> permissionsList = new ArrayList<>();
            if ((activity.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED))
                permissionsList.add(Manifest.permission.CAMERA);
            if ((activity.checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED))
                permissionsList.add(Manifest.permission.RECORD_AUDIO);
            if (permissionsList.size() != 0) {
                activity.requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                        REQUEST_CODE_ASK_PERMISSIONS);
                return false;
            }
            int hasPermission = activity.checkSelfPermission(Manifest.permission.CAMERA);
            if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                activity.requestPermissions(new String[]{Manifest.permission.CAMERA},
                        REQUEST_CODE_ASK_PERMISSIONS);
                return false;
            }
        }
        return true;
    }

    private boolean requestCamera(Activity activity) {
        if (afterM()) {
            int hasPermission = activity.checkSelfPermission(Manifest.permission.CAMERA);
            if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                activity.requestPermissions(new String[]{Manifest.permission.CAMERA},
                        REQUEST_CODE_ASK_PERMISSIONS);
                return false;
            }
        }
        return true;
    }

    private boolean requestAudio(Activity activity) {
        if (afterM()) {
            int hasPermission = activity.checkSelfPermission(Manifest.permission.RECORD_AUDIO);
            if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                activity.requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO},
                        REQUEST_CODE_ASK_PERMISSIONS);
                return false;
            }
        }
        return true;
    }

    private boolean requestStorage(Activity activity) {
        if (afterM()) {
            int hasPermission = activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                activity.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE_ASK_PERMISSIONS);
                return false;
            }
        }
        return true;
    }

    private boolean afterM() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

}
