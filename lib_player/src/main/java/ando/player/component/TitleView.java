package ando.player.component;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dueeeke.videoplayer.controller.ControlWrapper;
import com.dueeeke.videoplayer.controller.IControlComponent;
import com.dueeeke.videoplayer.player.VideoView;
import com.dueeeke.videoplayer.util.PlayerUtils;

import ando.player.R;

/**
 * 播放器顶部标题栏
 */
public class TitleView extends FrameLayout implements IControlComponent {

    private ControlWrapper mControlWrapper;

    private final LinearLayout mLlTitleContainer;
    private final TextView mTvTitle;
    private final TextView mTvTime; //系统当前时间
    private final ImageView mIvPip; //悬浮窗
    private final ImageView mIvBattery;

    private String mTitle;
    private boolean isUserPaused = false;
    private boolean isShowWhenPortrait = false;//竖屏是否显示TitleView, 默认不显示

    private final BatteryReceiver mBatteryReceiver;
    private boolean mIsRegister;//是否注册BatteryReceiver

    public TitleView(@NonNull Context context) {
        super(context);
    }

    public TitleView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TitleView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    {
        setVisibility(GONE);
        LayoutInflater.from(getContext()).inflate(R.layout.player_layout_title, this, true);
        mLlTitleContainer = findViewById(R.id.ll_title_container);
        ImageView back = findViewById(R.id.iv_back);
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = PlayerUtils.scanForActivity(getContext());
                if (activity != null) {
                    if (mControlWrapper.isFullScreen()) {
                        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        mControlWrapper.stopFullScreen();
                    } else {
                        activity.onBackPressed();
                    }
                }
            }
        });
        mTvTitle = findViewById(R.id.tv_title);
        mTvTime = findViewById(R.id.tv_sys_time);
        mIvPip = findViewById(R.id.iv_pip);

        //电量
        mIvBattery = findViewById(R.id.iv_battery);
        mBatteryReceiver = new BatteryReceiver(mIvBattery);
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mIsRegister) {
            getContext().unregisterReceiver(mBatteryReceiver);
            mIsRegister = false;
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!mIsRegister) {
            getContext().registerReceiver(mBatteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            mIsRegister = true;
        }
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
        Log.e("123", "onVisibilityChanged title = " + isVisible);
        final boolean isFullScreen = mControlWrapper.isFullScreen();
        //竖屏播放暂停时,Title不隐藏
        if (!isVisible && !isUserPaused && isShowWhenPortrait && !mControlWrapper.isPlaying() && !isFullScreen) {
            return;
        }
        isUserPaused = false;

        //只在全屏时才有效
        if (!isFullScreen && !isShowWhenPortrait) {
            return;
        }
        if (isVisible) {
            if (getVisibility() == GONE) {
                if (isFullScreen && isShowWhenPortrait) {
                    mTvTitle.setText(mTitle);
                } else {
                    mTvTitle.setText("");
                }
                mTvTime.setText(PlayerUtils.getCurrentSystemTime());
                setVisibility(VISIBLE);
                if (anim != null) {
                    startAnimation(anim);
                }
            }
        } else {
            if (getVisibility() == VISIBLE) {
                setVisibility(GONE);
                if (anim != null) {
                    startAnimation(anim);
                }
            }
        }
    }

    @Override
    public void onPlayStateChanged(int playState) {
        switch (playState) {
            case VideoView.STATE_PAUSED:
                isUserPaused = true;
                setVisibility(VISIBLE);
                break;
            case VideoView.STATE_IDLE:
            case VideoView.STATE_START_ABORT:
            case VideoView.STATE_PREPARING:
            case VideoView.STATE_PREPARED:
            case VideoView.STATE_ERROR:
            case VideoView.STATE_PLAYBACK_COMPLETED:
                setVisibility(GONE);
                break;
            case VideoView.STATE_PLAYING:
                if (isUserPaused) {
                    setVisibility(GONE);
                }
                isUserPaused = false;
                break;
            default:
        }
    }

    @Override
    public void onPlayerStateChanged(int playerState) {

        switch (playerState) {
            case VideoView.PLAYER_NORMAL:
                if (!isShowWhenPortrait) {
                    setVisibility(GONE);
                } else {
                    mTvTitle.setText("");
                    mIvPip.setVisibility(VISIBLE);
                    mIvBattery.setVisibility(GONE);
                    mTvTime.setVisibility(GONE);
                }
                mTvTitle.setSelected(false);
                break;
            case VideoView.PLAYER_FULL_SCREEN:
                if (mControlWrapper.isShowing() && !mControlWrapper.isLocked()) {
                    setVisibility(VISIBLE);

                    mIvPip.setVisibility(GONE);
                    mIvBattery.setVisibility(VISIBLE);
                    mTvTime.setVisibility(VISIBLE);
                    mTvTime.setText(PlayerUtils.getCurrentSystemTime());
                }
                mTvTitle.setText(mTitle);
                mTvTitle.setSelected(true);
                break;
            default:
        }

        final int fullTopPadding = PlayerUtils.dp2px(getContext(), 15.0F);
        final Activity activity = PlayerUtils.scanForActivity(getContext());
        if (activity != null && mControlWrapper.hasCutout()) {
            int orientation = activity.getRequestedOrientation();
            int cutoutHeight = mControlWrapper.getCutoutHeight();
            if (orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                mLlTitleContainer.setPadding(0, 0, 0, 0);
            } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
//                LayoutParams params= (LayoutParams) getLayoutParams();
//                params.setMargins(0,20,0,0);
//                setLayoutParams(params);
                mLlTitleContainer.setPadding(cutoutHeight, fullTopPadding, 0, 0);
            } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
                mLlTitleContainer.setPadding(0, fullTopPadding, cutoutHeight, 0);
            }
        }
    }

    @Override
    public void setProgress(int duration, int position) {
    }

    @Override
    public void onLockStateChanged(boolean isLocked) {
        if (isLocked) {
            setVisibility(GONE);
        } else {
            setVisibility(VISIBLE);
            mTvTime.setText(PlayerUtils.getCurrentSystemTime());
        }
    }

    public void showWhenPortrait(boolean visible) {
        this.isShowWhenPortrait = visible;
    }

    private static class BatteryReceiver extends BroadcastReceiver {
        private final ImageView pow;

        public BatteryReceiver(ImageView pow) {
            this.pow = pow;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();
            if (extras == null) {
                return;
            }
            int current = extras.getInt("level");// 获得当前电量
            int total = extras.getInt("scale");// 获得总电量
            int percent = current * 100 / total;
            pow.getDrawable().setLevel(percent);
        }
    }

}