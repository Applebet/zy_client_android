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
 */
public class IjkVideoView extends BaseIjkVideoView<BaseIjkPlayer> {

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
        setPlayerFactory(new PlayerFactory<BaseIjkPlayer>() {
            @Override
            public BaseIjkPlayer createPlayer(Context context) {
                return new BaseIjkPlayer(context);
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
        Log.w("123", "setOptions 生效了");
        initIjkOptions();
    }

    private void initIjkOptions() {

        //https://github.com/CarGuo/GSYVideoPlayer/blob/b0efc902dc554787092c97d98f1e15f414ea2dcb/doc/QUESTION.md
        //https://zhuanlan.zhihu.com/p/47060105
        //https://www.cnblogs.com/renhui/p/6420140.html
        if (isLive()) {//如果是直播设置 秒开配置，其余正常配置
            mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "http-detect-range-support", 0);
            mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "analyzeduration", 1);
            //rtsp 问题 https://ffmpeg.org/ffmpeg-protocols.html#rtsp >>>>>>>>>>>
            mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "rtsp_transport", "tcp");//tcp传输数据
            mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "rtsp_flags", "prefer_tcp");
            mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "allowed_media_types", "video");//根据媒体类型来配置
            mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "timeout", 30000);
            mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "buffer_size", 2048L);

            //input buffer:don't limit the input buffer size (useful with realtime streams)
            mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "infbuf", 1);//无限读, 是否限制输入缓存数 30
            mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "analyzemaxduration", 100L);
            //播放前的探测Size，默认是1M, 改小一点会出画面更快  1024 * 10
            mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "probesize", 10240L);//10240 20480
            mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "flush_packets", 1L);
            //关闭播放器缓冲，这个必须关闭，否则会出现播放一段时间后，一直卡主，控制台打印 FFP_MSG_BUFFERING_START
            //pause output until enough packets have been read after stalling
            mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "packet-buffering", 0L);
            mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "fast", 1L);//不额外优化
            //automatically start playing on prepared : on 1 ; off 0
            mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 1);
            mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 48L);//默认值48

            //max buffer size should be pre-read：默认为15*1024*1024
            mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "max-buffer-size", 60 * 1024 * 1024);//最大缓存数
            mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "min-frames", 60);//默认最小帧数2
            //设置seekTo能够快速seek到指定位置并播放
            //解决m3u8文件拖动问题 比如:一个3个多少小时的音频文件，开始播放几秒中，
            //然后拖动到2小时左右的时间，要loading 10分钟 fastseek , nobuffer
            mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "fflags", "fastseek");
            //分析码流时长 默认 1024*1000
            mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "analyzedmaxduration", 100L);
            ///rtsp 问题 <<<<<<<<<<
        } else {
            mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "infbuf", 0);
            mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "max_cached_duration", 0);//最大缓存时长
            mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "packet-buffering", 1L);
            // 设置缓冲区为 100 KB
            mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "max-buffer-size", 100 * 1024);
            // 视频的话，设置 100 帧即开始播放
            mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "min-frames", 100);
            mMediaPlayer.setSpeed(1F);
        }

        //开启硬解 1、打开，0、关闭
        int isEnable = 0;
        //硬解码：1、打开，0、关闭  -> 硬解码容易造成黑屏无声（硬件兼容问题）
        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", isEnable);
        //mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "videotoolbox", 1);
        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", isEnable);
        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-handle-resolution-change", isEnable);
        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-sync", isEnable);
        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-all-videos", isEnable);
        //打开h265硬解
        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-hevc", 0);

        //设置丢帧 音视同步, 太卡可以尝试丢帧  drop frames when cpu is too slow：0-120
        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 1L);
        //变速不变调 0 or 1
        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "soundtouch", 1);
        //拖动seek问题稍微修复 但是不可避免seek不准确的问题 1开启;0关闭
        //某些视频在SeekTo的时候，会跳回到拖动前的位置，这是因为视频的关键帧的问题，
        //通俗一点就是 FFMPEG 不兼容，视频压缩过于厉害，seek只支持关键帧，出现这个情况就是原始的视频文件中i 帧比较少
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
        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "dns_cache_timeout", -1);
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