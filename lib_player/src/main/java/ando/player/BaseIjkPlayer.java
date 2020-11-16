package ando.player;

import android.content.Context;

import com.dueeeke.videoplayer.ijk.IjkPlayer;

public class BaseIjkPlayer extends IjkPlayer {

    public BaseIjkPlayer(Context context) {
        super(context);
    }

    public void setOption(int category, String name, String value) {
        if (mMediaPlayer != null) {
            mMediaPlayer.setOption(category, name, value);
        }
    }

    public void setOption(int category, String name, long value) {
        if (mMediaPlayer != null) {
            mMediaPlayer.setOption(category, name, value);
        }
    }

}