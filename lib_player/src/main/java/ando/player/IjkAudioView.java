package ando.player;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dueeeke.videoplayer.player.PlayerFactory;

/**
 * Title: IjkAudioView
 * <p>
 * Description: 自定义播放器 -> 音频
 * </p>
 *
 * @author javakam
 * @date 2020-03-29
 */
public class IjkAudioView extends BaseIjkVideoView<BaseIjkPlayer> {

    public IjkAudioView(@NonNull Context context) {
        super(context);
    }

    public IjkAudioView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public IjkAudioView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    {
        setPlayerFactory(new PlayerFactory<BaseIjkPlayer>() {
            @Override
            public BaseIjkPlayer createPlayer(Context context) {
                return new BaseIjkPlayer(context);
            }
        });

    }

    @Override
    public void replay(boolean resetPosition) {
        //super.replay(resetPosition);
        if (resetPosition) {
            mCurrentPosition = 0;
        }
        //addDisplay();
        startPrepare(true);
        if (mPlayerContainer != null) {
            mPlayerContainer.setKeepScreenOn(true);
        }
    }


}