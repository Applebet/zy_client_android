package ando.player;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dueeeke.videoplayer.player.PlayerFactory;
import com.dueeeke.videoplayer.util.PlayerUtils;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * Title: IjkVideoView
 * <p>
 * Description: 自定义播放器 -> 视频
 * </p>
 *
 * @author javakam
 * @date 2020-03-29
 */
public class IjkVideoView extends BaseIjkVideoView<AndoIjkPlayer> {

    //小窗位置控制
    private int tinyCenterRightOffset;
    private int tinyBottomMargin;

    public IjkVideoView(@NonNull Context context) {
        super(context);
    }

    public IjkVideoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public IjkVideoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    {
        setPlayerFactory(new PlayerFactory<AndoIjkPlayer>() {
            @Override
            public AndoIjkPlayer createPlayer(Context context) {
                return new AndoIjkPlayer(context);
            }
        });

    }

    public void setNormalBottom(int centerRightOffset, int bottomMargin) {
        this.tinyCenterRightOffset = centerRightOffset;
        this.tinyBottomMargin = bottomMargin;
    }

    @Override
    protected void setOptions() {
        super.setOptions();
        Log.w("123","setOptions 生效了");

        //https://zhuanlan.zhihu.com/p/47060105
        //https://www.cnblogs.com/renhui/p/6420140.html
        if (isLive()) {//如果是直播设置 秒开配置，其余正常配置
            mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 0);
            mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "http-detect-range-support", 0);
            mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 8);
            mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "analyzemaxduration", 100L);
            mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "probesize", 10240L);
            mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "flush_packets", 1L);
            mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "packet-buffering", 0L);
            mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "analyzeduration", 1);
            mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "infbuf", 1);  // 无限读
        } else {
            mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "max_cached_duration", 0);
            mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "infbuf", 0);
            mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "packet-buffering", 1);
            // 设置缓冲区为 300 KB
            mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "max-buffer-size", 300 * 1024);
            // 视频的话，设置 150 帧即开始播放
            mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "min-frames", 150);
            mMediaPlayer.setSpeed(1F);
        }

        //启用硬解码 1启用 0关闭 硬解码容易造成黑屏无声（硬件兼容问题）
        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1);
        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", 1);
        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-handle-resolution-change", 1);
        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-sync", 1);
        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-all-videos", 1);
        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-hevc", 1); //打开h265硬解

        //设置丢帧 音视同步
        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 5);
        //变速不变调 0 or 1
        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "soundtouch", 1);
        //拖动seek问题稍微修复 但是不可避免seek不准确的问题 1开启;0关闭
        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "enable-accurate-seek", 1);
        //加载本地 m3u8 问题修复
        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "protocol_whitelist", "rtmp,crypto,file,http,https,tcp,tls,udp");
        //为嵌入式系统打开声音库 0关闭
        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "opensles", 0);
        //断网重连
        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "reconnect", 1);
        // url切换400（http与https域名共用）
        // 清空DNS,有时因为在APP里面要播放多种类型的视频(如:MP4,直播,直播平台保存的视频,和其他http视频), 有时会造成因为DNS的问题而报10000问题的
        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "dns_cache_clear", 1);
    }

    @Override
    public void startTinyScreen() {
        //super.startTinyScreen();

        //重写小窗创建
        if (mIsTinyScreen) {
            return;
        }
        ViewGroup contentView = getContentView();
        if (contentView == null) {
            return;
        }
        this.removeView(mPlayerContainer);
        int width = mTinyScreenSize[0];
        if (width <= 0) {
            width = PlayerUtils.getScreenWidth(getContext(), false) / 2;
        }

        int height = mTinyScreenSize[1];
        if (height <= 0) {
            height = width * 9 / 16;
        }

        LayoutParams params = new LayoutParams(width - tinyCenterRightOffset, height);
        params.gravity = Gravity.BOTTOM | Gravity.END;

        //params.bottomMargin = ResUtils.getDimensionPixelSize(R.dimen.dp_55);
        params.bottomMargin = tinyBottomMargin;
        //mPlayerContainer.setOnTouchListener(null);
        mPlayerContainer.requestFocusFromTouch();

        contentView.addView(mPlayerContainer, params);
        mIsTinyScreen = true;
        setPlayerState(PLAYER_TINY_SCREEN);

    }

    /* @Override
    public void onPrepared() {
        //super.onPrepared();
        setPlayState(STATE_PREPARED);
    }


    @Override
    public void skipPositionWhenPlay(int position) {
        //super.skipPositionWhenPlay(position);
        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "seek-at-start", position);
    }*/

}