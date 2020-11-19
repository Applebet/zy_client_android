package ando.player;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dueeeke.videoplayer.controller.GestureVideoController;
import com.dueeeke.videoplayer.player.VideoView;
import com.dueeeke.videoplayer.util.L;
import com.dueeeke.videoplayer.util.PlayerUtils;

import ando.player.component.CompleteView;
import ando.player.component.ErrorView;
import ando.player.component.GestureView;
import ando.player.component.LiveControlView;
import ando.player.component.PrepareView;
import ando.player.component.TitleView;
import ando.player.component.VodControlView;
import ando.player.utils.VideoUtils;

/**
 * 直播/点播控制器
 *
 * @author javakam
 */
public class StandardVideoController extends GestureVideoController implements View.OnClickListener {

    protected int systemUiVisibility;

    protected ImageView mLockButton;
    protected ImageView mScreenShot;
    protected ProgressBar mLoadingProgress;

    public StandardVideoController(@NonNull Context context) {
        this(context, null);
    }

    public StandardVideoController(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StandardVideoController(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.player_layout_standard_controller;
    }

    @Override
    protected void initView() {
        super.initView();
        mLockButton = findViewById(R.id.player_lock);
        mScreenShot = findViewById(R.id.player_screenshot);
        mLoadingProgress = findViewById(R.id.player_loading);
        mLockButton.setOnClickListener(this);
    }

    /**
     * 快速添加各个组件
     *
     * @param title  标题
     * @param isLive 是否为直播
     */
    public void addDefaultControlComponent(String title, boolean isLive) {
        CompleteView completeView = new CompleteView(getContext());
        ErrorView errorView = new ErrorView(getContext());
        PrepareView prepareView = new PrepareView(getContext());
        prepareView.setClickStart();
        TitleView titleView = new TitleView(getContext());
        titleView.setTitle(title);
        addControlComponent(completeView, errorView, prepareView, titleView);
        if (isLive) {
            addControlComponent(new LiveControlView(getContext()));
        } else {
            addControlComponent(new VodControlView(getContext()));
        }
        addControlComponent(new GestureView(getContext()));
        setCanChangePosition(!isLive);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.player_lock) {
            mControlWrapper.toggleLockState();
        }
    }

    @Override
    protected void onLockStateChanged(boolean isLocked) {
        if (isLocked) {
            mLockButton.setSelected(true);
            Toast.makeText(getContext(), R.string.str_player_locked, Toast.LENGTH_SHORT).show();
        } else {
            mLockButton.setSelected(false);
            Toast.makeText(getContext(), R.string.str_player_unlocked, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onVisibilityChanged(boolean isVisible, Animation anim) {
        if (mLockButton == null) {
            return;
        }
        if (mControlWrapper.isFullScreen()) {
            if (isVisible) {
                if (mLockButton.getVisibility() == GONE) {
                    mLockButton.setVisibility(VISIBLE);
                    mScreenShot.setVisibility(VISIBLE);
                    if (anim != null) {
                        mLockButton.startAnimation(anim);
                        mScreenShot.startAnimation(anim);
                    }
                }
            } else {
                mLockButton.setVisibility(GONE);
                mScreenShot.setVisibility(GONE);
                if (anim != null) {
                    mLockButton.startAnimation(anim);
                    mScreenShot.startAnimation(anim);
                }
            }
        }
    }

    @Override
    protected void onPlayerStateChanged(int playerState) {
        super.onPlayerStateChanged(playerState);
        switch (playerState) {
            case VideoView.PLAYER_NORMAL:
                L.e("PLAYER_NORMAL");
                postDelayed(() -> VideoUtils.showNavKey(getContext(), systemUiVisibility), 200);

                setLayoutParams(new LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
                mLockButton.setVisibility(GONE);
                mScreenShot.setVisibility(GONE);
                break;
            case VideoView.PLAYER_FULL_SCREEN:
                L.e("PLAYER_FULL_SCREEN");
                //Fixed: VIVO 无法关闭导航栏的问题
                systemUiVisibility = ((Activity) getContext()).getWindow().getDecorView().getSystemUiVisibility();
                postDelayed(() -> VideoUtils.hideNavigation(getContext()), 500);

                if (isShowing()) {
                    mLockButton.setVisibility(VISIBLE);
                    mScreenShot.setVisibility(VISIBLE);
                } else {
                    mLockButton.setVisibility(GONE);
                    mScreenShot.setVisibility(GONE);
                }
                break;
            default:
        }

        if (mActivity != null && hasCutout()) {
            int orientation = mActivity.getRequestedOrientation();
            int dp24 = PlayerUtils.dp2px(getContext(), 24);
            int cutoutHeight = getCutoutHeight();
            if (orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                ((LayoutParams) mLockButton.getLayoutParams()).setMargins(dp24, 0, dp24, 0);
            } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                LayoutParams params = (LayoutParams) mLockButton.getLayoutParams();
                params.setMargins(dp24 + cutoutHeight, 0, dp24 + cutoutHeight, 0);
            } else if (orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
                LayoutParams params = (LayoutParams) mLockButton.getLayoutParams();
                params.setMargins(dp24, 0, dp24, 0);
            }
        }

    }

    @Override
    protected void onPlayStateChanged(int playState) {
        super.onPlayStateChanged(playState);
        switch (playState) {
            //调用release方法会回到此状态
            case VideoView.STATE_IDLE:
                L.e("STATE_IDLE");
                mLockButton.setSelected(false);
                mLoadingProgress.setVisibility(GONE);
                break;
            case VideoView.STATE_PLAYING:
                L.e("STATE_PLAYING");
                mLoadingProgress.setVisibility(GONE);
                break;
            case VideoView.STATE_PAUSED:
                L.e("STATE_PAUSED");
                mLoadingProgress.setVisibility(GONE);
                break;
            case VideoView.STATE_PREPARING:
                L.e("STATE_PREPARING");
                mLoadingProgress.setVisibility(VISIBLE);
                break;
            case VideoView.STATE_PREPARED:
                L.e("STATE_PREPARED");
                mLoadingProgress.setVisibility(GONE);
                break;
            case VideoView.STATE_ERROR:
                L.e("STATE_ERROR");
                mLoadingProgress.setVisibility(GONE);
                break;
            case VideoView.STATE_BUFFERING:
                L.e("STATE_BUFFERING");
                mLoadingProgress.setVisibility(VISIBLE);
                break;
            case VideoView.STATE_BUFFERED:
                L.e("STATE_BUFFERED");
                mLoadingProgress.setVisibility(GONE);
                break;
            case VideoView.STATE_PLAYBACK_COMPLETED:
                L.e("STATE_PLAYBACK_COMPLETED");
                mLoadingProgress.setVisibility(GONE);
                mLockButton.setVisibility(GONE);
                mLockButton.setSelected(false);
                break;
            default:
        }
    }

    @Override
    public boolean onBackPressed() {
        if (isLocked()) {
            show();
            Toast.makeText(getContext(), R.string.str_player_lock_tip, Toast.LENGTH_SHORT).show();
            return true;
        }
        if (mControlWrapper.isFullScreen()) {
            return stopFullScreen();
        }
        return super.onBackPressed();
    }

    ///////////

    /**
     * 截屏
     */
    public void setScreenShotListener(View.OnClickListener listener) {
        mScreenShot.setOnClickListener(listener);
    }

}