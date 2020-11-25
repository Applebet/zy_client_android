package ando.player.component;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.dueeeke.videoplayer.controller.ControlWrapper;
import com.dueeeke.videoplayer.controller.IControlComponent;
import com.dueeeke.videoplayer.player.VideoView;
import com.dueeeke.videoplayer.util.PlayerUtils;

import java.util.List;
import java.util.Locale;

import ando.player.R;
import ando.player.dialog.IPlayerCallBack;
import ando.player.dialog.DialogFactory;
import ando.player.dialog.SimplePlayerCallBack;
import ando.player.dialog.SpeedDialog;
import ando.player.dialog.VideoListDialog;
import ando.player.setting.ITheme;
import ando.player.setting.MediaConstants;
import ando.player.setting.Theme;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static com.dueeeke.videoplayer.util.PlayerUtils.stringForTime;

/**
 * 点播底部控制栏
 */
public class VodControlView extends FrameLayout implements IControlComponent, View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    protected ControlWrapper mControlWrapper;

    private final LinearLayout mBottomContainer;
    private final ImageView mPlayButton;
    private final SeekBar mVideoProgress;
    private final TextView mTimePercent;
    private final ImageView mFullScreen;

    private final SeekBar mFullVideoProgress;
    private final LinearLayout mFullBottomContainer;
    private final ImageView mFullPlayButton;
    private final TextView mFullTimePercent;
    private final TextView mFullTvSelectList;
    private final TextView mFullTvSpeed;
    private final TextView mFullTvDefinition;

    private final ProgressBar mBottomProgress;

    private boolean mIsDragging;

    private boolean mIsShowBottomProgress = true;

    //播放速度
    private SpeedDialog mSpeedDialog;
    private Theme mSpeedDialogTheme;
    private int mSpeed = MediaConstants.PLAYSPEED_10;//播放速度, 临时变量

    //剧集
    private VideoListDialog mListDialog;
    private List<String> mVideoList;
    private int mCurrPosition;
    private IPlayerCallBack mCallBack;

    public VodControlView(@NonNull Context context) {
        super(context);
    }

    public VodControlView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public VodControlView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    {
        setVisibility(GONE);
        LayoutInflater.from(getContext()).inflate(getLayoutId(), this, true);
        setTheme(Theme.DEFAULT);
        mFullScreen = findViewById(R.id.fullscreen);
        mBottomContainer = findViewById(R.id.bottom_container);
        mVideoProgress = findViewById(R.id.seekBar);
        mTimePercent = findViewById(R.id.player_time_percent);
        mPlayButton = findViewById(R.id.iv_play);
        mBottomProgress = findViewById(R.id.bottom_progress);
        mFullScreen.setOnClickListener(this);
        mVideoProgress.setOnSeekBarChangeListener(this);
        mPlayButton.setOnClickListener(this);

        mFullBottomContainer = findViewById(R.id.bottom_container_vod_full);
        mFullPlayButton = findViewById(R.id.iv_vod_full_play);
        mFullVideoProgress = findViewById(R.id.seekBar_vod_full);
        mFullTimePercent = findViewById(R.id.tv_vod_full_time_percent);
        mFullTvSelectList = findViewById(R.id.tv_vod_full_select_list);
        mFullTvSpeed = findViewById(R.id.tv_vod_full_speed);
        mFullTvDefinition = findViewById(R.id.tv_vod_full_definition);
        mFullTvSelectList.setOnClickListener(this);
        mFullTvSpeed.setOnClickListener(this);
        mFullTvDefinition.setOnClickListener(this);
        mFullPlayButton.setOnClickListener(this);
        mFullVideoProgress.setOnSeekBarChangeListener(this);

        //5.1以下系统SeekBar高度需要设置成WRAP_CONTENT
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
            mVideoProgress.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
            mFullVideoProgress.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
        }
    }

    protected int getLayoutId() {
        return R.layout.player_layout_vod;
    }

    /**
     * 是否显示底部进度条，默认显示
     */
    public void showBottomProgress(boolean isShow) {
        mIsShowBottomProgress = isShow;
    }

    @Override
    public void attach(@NonNull ControlWrapper controlWrapper) {
        mControlWrapper = controlWrapper;
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void onVisibilityChanged(boolean isVisible, Animation anim) {
        //Log.e("123", "onVisibilityChanged Vod = " + isVisible + "  isFull = " + mControlWrapper.isFullScreen());

        if (isVisible) {
            if (getVisibility() == GONE) {
                setVisibility(VISIBLE);
            }

            getRealBottomContainer().setVisibility(VISIBLE);
            if (anim != null) {
                getRealBottomContainer().startAnimation(anim);
            }

            if (mIsShowBottomProgress) {
                mBottomProgress.setVisibility(GONE);
            }
        } else {
            if (getVisibility() == VISIBLE) {
                setVisibility(GONE);
            }

            getRealBottomContainer().setVisibility(GONE);
            if (anim != null) {
                getRealBottomContainer().startAnimation(anim);
            }

            if (mIsShowBottomProgress && !checkFullScreen()) {
                AlphaAnimation animation = new AlphaAnimation(0f, 1f);
                animation.setDuration(300);

                mBottomProgress.setVisibility(VISIBLE);
                mBottomProgress.startAnimation(animation);
            }
        }
    }


    @Override
    public void onPlayStateChanged(int playState) {
        //Log.e("123", "onPlayStateChanged vod = " + playState);

        switch (playState) {
            case VideoView.STATE_IDLE:
            case VideoView.STATE_PLAYBACK_COMPLETED:
                setVisibility(GONE);
                mBottomProgress.setProgress(0);
                mBottomProgress.setSecondaryProgress(0);
                getRealVideoProgress().setProgress(0);
                getRealVideoProgress().setSecondaryProgress(0);
                break;
            case VideoView.STATE_START_ABORT:
            case VideoView.STATE_PREPARING:
            case VideoView.STATE_PREPARED:
            case VideoView.STATE_ERROR:
                setVisibility(GONE);
                break;
            case VideoView.STATE_PLAYING:
                getRealPlayButton().setSelected(true);
                if (mIsShowBottomProgress) {
                    if (mControlWrapper.isShowing()) {
                        mBottomProgress.setVisibility(GONE);
                        getRealBottomContainer().setVisibility(VISIBLE);
                    } else {
                        getRealBottomContainer().setVisibility(GONE);
                        //Fixed: 屏幕中间横线的问题
                        if (!checkFullScreen()) {
                            mBottomProgress.setVisibility(VISIBLE);
                        }
                    }
                } else {
                    getRealBottomContainer().setVisibility(GONE);
                }
                setVisibility(VISIBLE);
                //开始刷新进度
                mControlWrapper.startProgress();
                break;
            case VideoView.STATE_PAUSED:
                getRealPlayButton().setSelected(false);
                break;
            case VideoView.STATE_BUFFERING:
            case VideoView.STATE_BUFFERED:
                getRealPlayButton().setSelected(mControlWrapper.isPlaying());
                break;
            default:
        }
    }

    @Override
    public void onPlayerStateChanged(int playerState) {
//        Log.e("123", "onPlayerStateChanged vod = " + playerState);

        dismissDialogs();

        switch (playerState) {
            case VideoView.PLAYER_NORMAL:
                mFullBottomContainer.setVisibility(GONE);
                mBottomContainer.setVisibility(VISIBLE);
                mPlayButton.setSelected(mControlWrapper.isPlaying());

                mFullScreen.setSelected(false);
                mFullScreen.setVisibility(VISIBLE);
                break;
            case VideoView.PLAYER_FULL_SCREEN:
                mBottomContainer.setVisibility(GONE);
                mBottomProgress.setVisibility(GONE);
                mFullBottomContainer.setVisibility(VISIBLE);
                mFullPlayButton.setSelected(mControlWrapper.isPlaying());

                mFullScreen.setSelected(true);
                mFullScreen.setVisibility(GONE);
                return;
            default:
        }

        final int fullTopPadding = PlayerUtils.dp2px(getContext(), 15.0F);
        final Activity activity = PlayerUtils.scanForActivity(getContext());
        if (activity != null && mControlWrapper.hasCutout()) {
            int orientation = activity.getRequestedOrientation();
            int cutoutHeight = mControlWrapper.getCutoutHeight();
            if (orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                getRealBottomContainer().setPadding(0, 0, 0, 0);
                mBottomProgress.setPadding(0, 0, 0, 0);
            } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                //Log.e("123", "onPlayerStateChanged vod fullTopPadding = " + fullTopPadding);
                getRealBottomContainer().setPadding(cutoutHeight, 0, 0, fullTopPadding);
                mBottomProgress.setPadding(cutoutHeight, 0, 0, 0);
            } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
                getRealBottomContainer().setPadding(0, 0, cutoutHeight, fullTopPadding);
                mBottomProgress.setPadding(0, 0, cutoutHeight, 0);
            }
        }
    }

    @Override
    public void setProgress(int duration, int position) {
        if (mIsDragging) {
            return;
        }

        final SeekBar seekBar = getRealVideoProgress();
        if (seekBar != null) {
            if (duration > 0) {
                seekBar.setEnabled(true);
                int pos = (int) (position * 1.0 / duration * seekBar.getMax());
                seekBar.setProgress(pos);
                mBottomProgress.setProgress(pos);
            } else {
                seekBar.setEnabled(false);
            }
            int percent = mControlWrapper.getBufferedPercentage();
            if (percent >= 96) { //解决缓冲进度不能100%问题
                seekBar.setSecondaryProgress(getRealVideoProgress().getMax());
                mBottomProgress.setSecondaryProgress(mBottomProgress.getMax());
            } else {
                seekBar.setSecondaryProgress(percent * 10);
                mBottomProgress.setSecondaryProgress(percent * 10);
            }
        }
        setRealTimePercent(position, duration);
    }

    @Override
    public void onLockStateChanged(boolean isLocked) {
        onVisibilityChanged(!isLocked, null);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.fullscreen) {
            toggleFullScreen();
        } else if (id == R.id.iv_play) {
            mControlWrapper.togglePlay();
        } else if (id == R.id.iv_vod_full_play) {
            mControlWrapper.togglePlay();
        } else if (id == R.id.tv_vod_full_select_list) {
            //隐藏掉其他所有遮盖物
            mControlWrapper.hide();
            //选集
            mListDialog = DialogFactory.INSTANCE.createVideoListDialog(this, true, mVideoList, mCurrPosition, mCallBack);
        } else if (id == R.id.tv_vod_full_speed) {
            //隐藏掉其他所有遮盖物
            mControlWrapper.hide();

            mSpeedDialog = DialogFactory.INSTANCE.createFullSpeedDialog(
                    this, true, mSpeed,
                    mSpeedDialogTheme, new SimplePlayerCallBack() {
                        @Override
                        public void onSpeedItemClick(int speedType, float speed, String name) {
                            super.onSpeedItemClick(speedType, speed, name);
                            mSpeed = speedType;
                            mControlWrapper.setSpeed(speed);
                            mFullTvSpeed.setText(name);
                        }
                    });
        } else if (id == R.id.tv_vod_full_definition) {

        }
    }

    private void toggleFullScreen() {
        Activity activity = PlayerUtils.scanForActivity(getContext());
        mControlWrapper.toggleFullScreen(activity);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mIsDragging = true;
        mControlWrapper.stopProgress();
        mControlWrapper.stopFadeOut();
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        long duration = mControlWrapper.getDuration();
        long newPosition = (duration * getRealVideoProgress().getProgress()) / getRealVideoProgress().getMax();
        mControlWrapper.seekTo((int) newPosition);
        mIsDragging = false;
        mControlWrapper.startProgress();
        mControlWrapper.startFadeOut();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (!fromUser) {
            return;
        }

        long duration = mControlWrapper.getDuration();
        long newPosition = (duration * progress) / getRealVideoProgress().getMax();
        setRealTimePercent((int) newPosition, (int) duration);
    }

    @Override
    protected void onDetachedFromWindow() {
        dismissDialogs();
        super.onDetachedFromWindow();
    }

    private boolean checkFullScreen() {
        return mControlWrapper != null && mControlWrapper.isFullScreen();
    }

    private LinearLayout getRealBottomContainer() {
        return checkFullScreen() ? mFullBottomContainer : mBottomContainer;
    }

    private ImageView getRealPlayButton() {
        return checkFullScreen() ? mFullPlayButton : mPlayButton;
    }

    private SeekBar getRealVideoProgress() {
        return checkFullScreen() ? mFullVideoProgress : mVideoProgress;
    }

    public void dismissDialogs() {
        if (mListDialog != null) {
            mListDialog.dismiss();
            mListDialog = null;
        }
        if (mSpeedDialog != null) {
            mSpeedDialog.dismiss();
            mSpeedDialog = null;
        }
    }

    private void setRealTimePercent(int position, int duration) {
        final TextView tvTimePercent = checkFullScreen() ? mFullTimePercent : mTimePercent;
        tvTimePercent.setText(
                String.format(Locale.getDefault(), getContext().
                        getString(R.string.str_player_time_percent), stringForTime(position), stringForTime(duration))
        );
    }

    public void setTheme(Theme theme) {
        this.mSpeedDialogTheme = theme;
        //通过判断子View是否实现了ITheme的接口，去更新主题
        int childCounts = getChildCount();
        for (int i = 0; i < childCounts; i++) {
            View view = getChildAt(i);
            if (view instanceof ITheme) {
                ((ITheme) view).setTheme(theme);
            }
        }
    }

    public void setVideoList(List<String> videoList, int position) {
        this.mVideoList = videoList;
        this.mCurrPosition = position;
        if (mVideoList != null && mVideoList.size() > 1) {
            mFullTvSelectList.setVisibility(VISIBLE);
        } else {
            mFullTvSelectList.setVisibility(GONE);
        }
    }

    public void setCallBack(SimplePlayerCallBack callBack) {
        this.mCallBack = callBack;
    }
}