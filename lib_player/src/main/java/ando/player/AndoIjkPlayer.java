package ando.player;

import android.content.Context;

import com.dueeeke.videoplayer.ijk.IjkPlayer;

public class AndoIjkPlayer extends IjkPlayer {

    private Context mContext;

    public AndoIjkPlayer(Context context) {
        super(context);
        this.mContext = context;
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