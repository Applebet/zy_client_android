package ando.player.utils;

import android.view.View;
import android.view.animation.Animation;

import androidx.annotation.NonNull;

import com.dueeeke.videoplayer.controller.ControlWrapper;
import com.dueeeke.videoplayer.controller.IControlComponent;
import com.dueeeke.videoplayer.util.L;

public class PlayerMonitor implements IControlComponent {

    private ControlWrapper mControlWrapper;

    @Override
    public void attach(@NonNull ControlWrapper controlWrapper) {
        mControlWrapper = controlWrapper;
    }

    @Override
    public View getView() {
        return null;
    }

    @Override
    public void onVisibilityChanged(boolean isVisible, Animation anim) {
        L.d("onVisibilityChanged: " + isVisible);
    }

    @Override
    public void onPlayStateChanged(int playState) {
        L.d("onPlayStateChanged: " + VideoUtils.dumpPlayState(playState));
    }

    @Override
    public void onPlayerStateChanged(int playerState) {
        L.d("onPlayerStateChanged: " + VideoUtils.dumpPlayerState(playerState));
    }

    @Override
    public void setProgress(int duration, int position) {
        L.d("setProgress: duration: " + duration + " position: " + position + " buffered percent: " + mControlWrapper.getBufferedPercentage());
        L.d("network speed: " + mControlWrapper.getTcpSpeed());
    }

    @Override
    public void onLockStateChanged(boolean isLocked) {
        L.d("onLockStateChanged: " + isLocked);
    }
}