package ando.player;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dueeeke.videoplayer.player.AbstractPlayer;
import com.dueeeke.videoplayer.player.VideoView;

/**
 * Title: BaseIjkVideoView
 * <p>
 * Description: 自定义播放器
 * </p>
 */
public abstract class BaseIjkVideoView<P extends AbstractPlayer> extends VideoView<P> {

    private boolean isLive;//直播 or 录播

    public BaseIjkVideoView(@NonNull Context context) {
        super(context);
    }

    public BaseIjkVideoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseIjkVideoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public boolean isLive() {
        return isLive;
    }

    public void setLive(boolean live) {
        isLive = live;
    }
}