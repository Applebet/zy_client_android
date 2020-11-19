package ando.player.component;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dueeeke.videoplayer.controller.ControlWrapper;
import com.dueeeke.videoplayer.controller.IControlComponent;
import com.dueeeke.videoplayer.player.VideoView;
import com.dueeeke.videoplayer.util.PlayerUtils;

import java.util.Locale;

import ando.player.R;
import ando.player.dialog.BaseDialog;
import ando.player.dialog.PlaySpeedDialog;
import ando.player.dialog.PlayerDialogFactory;
import ando.player.dialog.SimpleItemClickListener;
import ando.player.setting.ITheme;
import ando.player.setting.MediaConstants;
import ando.player.setting.Theme;

import static com.dueeeke.videoplayer.util.PlayerUtils.stringForTime;

/**
 * 点播底部控制栏  横向全屏
 */
public class VodFullControlView extends FrameLayout implements IControlComponent, View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    protected ControlWrapper mControlWrapper;

    private final TextView mTvSelectList;
    private final TextView mTvSpeed;
    private final TextView mTvDefinition;
    private final TextView mTimePercent;
    private final LinearLayout mBottomContainer;
    private final SeekBar mVideoProgress;
    private final ProgressBar mBottomProgress;
    private final ImageView mPlayButton;

    private boolean mIsDragging;

    private boolean mIsShowBottomProgress = true;

    protected Theme mTheme;
    protected BaseDialog baseDialog;

    public VodFullControlView(@NonNull Context context) {
        super(context);
    }

    public VodFullControlView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public VodFullControlView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    {
        setVisibility(GONE);
        LayoutInflater.from(getContext()).inflate(getLayoutId(), this, true);
        setTheme(Theme.DEFAULT);
        mBottomContainer = findViewById(R.id.bottom_container_vod_full);
        mPlayButton = findViewById(R.id.iv_vod_full_play);
        mVideoProgress = findViewById(R.id.seekBar_vod_full);
        mTimePercent = findViewById(R.id.tv_vod_full_time_percent);
        mTvSelectList = findViewById(R.id.tv_vod_full_select_list);
        mTvSpeed = findViewById(R.id.tv_vod_full_speed);
        mTvDefinition = findViewById(R.id.tv_vod_full_definition);

        mBottomProgress = findViewById(R.id.bottom_progress_vod_full);

        mTvSelectList.setOnClickListener(this);
        mTvSpeed.setOnClickListener(this);
        mTvDefinition.setOnClickListener(this);
        mPlayButton.setOnClickListener(this);
        mVideoProgress.setOnSeekBarChangeListener(this);

        //5.1以下系统SeekBar高度需要设置成WRAP_CONTENT
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
            mVideoProgress.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
        }
    }

    protected int getLayoutId() {
        return R.layout.player_layout_vod_full;
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
        Log.w("123", "onVisibilityChanged Vod FULL= " + isVisible + "  isFull = " + mControlWrapper.isFullScreen());

        if (!checkIsFullScreen()) {
            mBottomProgress.setProgress(0);
            mBottomProgress.setSecondaryProgress(0);
            mVideoProgress.setProgress(0);
            mVideoProgress.setSecondaryProgress(0);

            mBottomContainer.setVisibility(GONE);
            if (mIsShowBottomProgress) {
                mBottomProgress.setVisibility(GONE);
            }
            return;
        }

        if (isVisible) {
            if (getVisibility() == GONE) {
                setVisibility(VISIBLE);
            }
            mBottomContainer.setVisibility(VISIBLE);
            if (anim != null) {
                mBottomContainer.startAnimation(anim);
            }
            if (mIsShowBottomProgress) {
                mBottomProgress.setVisibility(GONE);
            }
        } else {
            if (getVisibility() == VISIBLE) {
                setVisibility(GONE);
            }
            mBottomContainer.setVisibility(GONE);
            if (anim != null) {
                mBottomContainer.startAnimation(anim);
            }
            if (mIsShowBottomProgress) {
                mBottomProgress.setVisibility(VISIBLE);
                AlphaAnimation animation = new AlphaAnimation(0f, 1f);
                animation.setDuration(300);
                mBottomProgress.startAnimation(animation);
            }
        }
    }

    @Override
    public void onPlayStateChanged(int playState) {
        switch (playState) {
            case VideoView.STATE_IDLE:
            case VideoView.STATE_PLAYBACK_COMPLETED:
                setVisibility(GONE);
                mBottomProgress.setProgress(0);
                mBottomProgress.setSecondaryProgress(0);
                mVideoProgress.setProgress(0);
                mVideoProgress.setSecondaryProgress(0);
                break;
            case VideoView.STATE_START_ABORT:
            case VideoView.STATE_PREPARING:
            case VideoView.STATE_PREPARED:
            case VideoView.STATE_ERROR:
                setVisibility(GONE);
                break;
            case VideoView.STATE_PLAYING:
                mPlayButton.setSelected(true);
                if (mIsShowBottomProgress) {
                    if (mControlWrapper.isShowing()) {
                        mBottomProgress.setVisibility(GONE);
                        mBottomContainer.setVisibility(VISIBLE);
                    } else {
                        mBottomContainer.setVisibility(GONE);
                        mBottomProgress.setVisibility(VISIBLE);
                    }
                } else {
                    mBottomContainer.setVisibility(GONE);
                }
                setVisibility(VISIBLE);
                //开始刷新进度
                mControlWrapper.startProgress();
                break;
            case VideoView.STATE_PAUSED:
                mPlayButton.setSelected(false);
                break;
            case VideoView.STATE_BUFFERING:
            case VideoView.STATE_BUFFERED:
                mPlayButton.setSelected(mControlWrapper.isPlaying());
                break;
            default:
        }
    }

    @Override
    public void onPlayerStateChanged(int playerState) {
        Log.w("123", "onPlayerStateChanged vod FULL = " + playerState);

        switch (playerState) {
            case VideoView.PLAYER_NORMAL:
                return;
            case VideoView.PLAYER_FULL_SCREEN:
                setVisibility(VISIBLE);
                break;
            default:
        }

        final int fullTopPadding = PlayerUtils.dp2px(getContext(), 15.0F);
        final Activity activity = PlayerUtils.scanForActivity(getContext());
        if (activity != null && mControlWrapper.hasCutout()) {
            int orientation = activity.getRequestedOrientation();
            int cutoutHeight = mControlWrapper.getCutoutHeight();
            if (orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                mBottomContainer.setPadding(0, 0, 0, 0);
                mBottomProgress.setPadding(0, 0, 0, 0);
            } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                Log.e("123", "onPlayerStateChanged vod fullTopPadding = " + fullTopPadding);
                mBottomContainer.setPadding(cutoutHeight, 0, 0, fullTopPadding);
                mBottomProgress.setPadding(cutoutHeight, 0, 0, 0);
            } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
                mBottomContainer.setPadding(0, 0, cutoutHeight, fullTopPadding);
                mBottomProgress.setPadding(0, 0, cutoutHeight, 0);
            }
        }
    }

    @Override
    public void setProgress(int duration, int position) {
        if (mIsDragging) {
            return;
        }

        if (mVideoProgress != null) {
            if (duration > 0) {
                mVideoProgress.setEnabled(true);
                int pos = (int) (position * 1.0 / duration * mVideoProgress.getMax());
                mVideoProgress.setProgress(pos);
                mBottomProgress.setProgress(pos);
            } else {
                mVideoProgress.setEnabled(false);
            }
            int percent = mControlWrapper.getBufferedPercentage();
            if (percent >= 96) { //解决缓冲进度不能100%问题
                mVideoProgress.setSecondaryProgress(mVideoProgress.getMax());
                mBottomProgress.setSecondaryProgress(mBottomProgress.getMax());
            } else {
                mVideoProgress.setSecondaryProgress(percent * 10);
                mBottomProgress.setSecondaryProgress(percent * 10);
            }
        }

        if (mTimePercent != null) {
            String t = String.format(Locale.getDefault(), getContext().getString(R.string.str_player_time_percent),
                    stringForTime(position), stringForTime(duration));
            mTimePercent.setText(t);
        }
    }

    @Override
    public void onLockStateChanged(boolean isLocked) {
        onVisibilityChanged(!isLocked, null);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_vod_full_play) {
            mControlWrapper.togglePlay();
        } else if (id == R.id.tv_vod_full_select_list) {

        } else if (id == R.id.tv_vod_full_speed) {

            mControlWrapper.hide();//隐藏掉其他所有遮盖物

            PlaySpeedDialog dialog = PlayerDialogFactory.getFullSpeedDialog(getContext(), true,
                    MediaConstants.PLAYSPEED_10, getLayoutParams(), mTheme, new SimpleItemClickListener() {
                        @Override
                        public void onSpeedItemClick(int speedType, float speed, String name) {
                            super.onSpeedItemClick(speedType, speed, name);
                            mControlWrapper.setSpeed(speed);
                            mTvSpeed.setText(name);
                        }
                    });
            baseDialog = dialog.show();
        } else if (id == R.id.tv_vod_full_definition) {

        }

    }

    /**
     * 横竖屏切换
     */
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
        long newPosition = (duration * seekBar.getProgress()) / mVideoProgress.getMax();
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
        long newPosition = (duration * progress) / mVideoProgress.getMax();
        if (mTimePercent != null) {
            mTimePercent.setText(stringForTime((int) newPosition));
        }
    }

    public void dismissDialog() {
        if (baseDialog != null) {
            baseDialog.dismiss();
        }
    }

    public void setTheme(Theme theme) {
        this.mTheme = theme;
        //通过判断子View是否实现了ITheme的接口，去更新主题
        int childCounts = getChildCount();
        for (int i = 0; i < childCounts; i++) {
            View view = getChildAt(i);
            if (view instanceof ITheme) {
                ((ITheme) view).setTheme(theme);
            }
        }
    }

    private boolean checkIsFullScreen() {
        if (mControlWrapper != null && mControlWrapper.isFullScreen()) {
            setVisibility(GONE);
            return true;
        }
        return false;
    }

}