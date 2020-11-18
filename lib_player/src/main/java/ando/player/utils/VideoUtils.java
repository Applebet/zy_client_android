package ando.player.utils;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.dueeeke.videoplayer.player.VideoView;
import com.dueeeke.videoplayer.player.VideoViewConfig;
import com.dueeeke.videoplayer.player.VideoViewManager;

import java.lang.reflect.Field;

public final class VideoUtils {

    private VideoUtils() {
    }

    /**
     * 获取当前的播放核心
     */
    public static Object getCurrentPlayerFactory() {
        VideoViewConfig config = VideoViewManager.getConfig();
        Object playerFactory = null;
        try {
            Field mPlayerFactoryField = config.getClass().getDeclaredField("mPlayerFactory");
            mPlayerFactoryField.setAccessible(true);
            playerFactory = mPlayerFactoryField.get(config);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return playerFactory;
    }

    /**
     * 将View从父控件中移除
     */
    public static void removeViewFormParent(View v) {
        if (v == null) {
            return;
        }
        ViewParent parent = v.getParent();
        if (parent instanceof ViewGroup) {
            ((ViewGroup) parent).removeView(v);
        }
    }

    /**
     * Returns a string containing player state debugging information.
     */
    public static String dumpPlayState(int state) {
        String playStateString;
        switch (state) {
            default:
            case VideoView.STATE_IDLE:
                playStateString = "idle";
                break;
            case VideoView.STATE_PREPARING:
                playStateString = "preparing";
                break;
            case VideoView.STATE_PREPARED:
                playStateString = "prepared";
                break;
            case VideoView.STATE_PLAYING:
                playStateString = "playing";
                break;
            case VideoView.STATE_PAUSED:
                playStateString = "pause";
                break;
            case VideoView.STATE_BUFFERING:
                playStateString = "buffering";
                break;
            case VideoView.STATE_BUFFERED:
                playStateString = "buffered";
                break;
            case VideoView.STATE_PLAYBACK_COMPLETED:
                playStateString = "playback completed";
                break;
            case VideoView.STATE_ERROR:
                playStateString = "error";
                break;
        }
        return String.format("playState: %s", playStateString);
    }

    /**
     * Returns a string containing player state debugging information.
     */
    public static String dumpPlayerState(int state) {
        String playerStateString;
        switch (state) {
            default:
            case VideoView.PLAYER_NORMAL:
                playerStateString = "normal";
                break;
            case VideoView.PLAYER_FULL_SCREEN:
                playerStateString = "full screen";
                break;
            case VideoView.PLAYER_TINY_SCREEN:
                playerStateString = "tiny screen";
                break;
        }
        return String.format("playerState: %s", playerStateString);
    }

}