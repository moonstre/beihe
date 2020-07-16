package com.yiliao.chat.ttt;


import com.yiliao.chat.activity.MainActivity;

import java.util.ArrayList;

public class RemoteManager {

    private ArrayList<AudioRemoteWindow> mRemoteWindowList = new ArrayList();

    public RemoteManager(MainActivity mainActivity) {
    }

    public void add(EnterUserInfo userInfo) {
        for (int i = 0; i < mRemoteWindowList.size(); i ++) {
            AudioRemoteWindow audioRemoteWindow = mRemoteWindowList.get(i);
            if (audioRemoteWindow.mId == -1) {
                audioRemoteWindow.show(userInfo);
                return;
            }
        }
    }

    public void remove(long id) {
        for (int i = 0; i < mRemoteWindowList.size(); i ++) {
            AudioRemoteWindow audioRemoteWindow = mRemoteWindowList.get(i);
            if (audioRemoteWindow.mId == id) {
                audioRemoteWindow.hide();
                return;
            }
        }
    }

    public void muteAudio(long id, boolean mute) {
        for (int i = 0; i < mRemoteWindowList.size(); i ++) {
            AudioRemoteWindow audioRemoteWindow = mRemoteWindowList.get(i);
            if (audioRemoteWindow.mId == id) {
                audioRemoteWindow.mute(mute);
                return;
            }
        }
    }

    public void updateAudioBitrate(long id, String bitrate) {
        for (int i = 0; i < mRemoteWindowList.size(); i ++) {
            AudioRemoteWindow audioRemoteWindow = mRemoteWindowList.get(i);
            if (audioRemoteWindow.mId == id) {
                audioRemoteWindow.updateAudioBitrate(bitrate);
                return;
            }
        }
    }

    public void updateVideoBitrate(long id, String bitrate) {
        for (int i = 0; i < mRemoteWindowList.size(); i ++) {
            AudioRemoteWindow audioRemoteWindow = mRemoteWindowList.get(i);
            if (audioRemoteWindow.mId == id) {
                audioRemoteWindow.updateVideoBitrate(bitrate);
                return;
            }
        }
    }

    public void updateSpeakState(long id, int volumeLevel) {
        for (int i = 0; i < mRemoteWindowList.size(); i ++) {
            AudioRemoteWindow audioRemoteWindow = mRemoteWindowList.get(i);
            if (audioRemoteWindow.mId == id) {
                audioRemoteWindow.updateSpeakState(volumeLevel);
                return;
            }
        }
    }
}
