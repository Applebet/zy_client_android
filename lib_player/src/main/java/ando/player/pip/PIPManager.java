package ando.player.pip;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;

import com.dueeeke.videoplayer.player.VideoViewManager;

import ando.player.IjkVideoView;
import ando.player.utils.PlayerUtils;

/**
 * 悬浮播放
 */
@SuppressLint("StaticFieldLeak")
public class PIPManager {

    private static Context context;
    private static PIPManager instance;

    private final IjkVideoView mVideoView;
    private final FloatView mFloatView;
    private final FloatController mFloatController;

    private boolean mIsShowing;
    private int mPlayingPosition = -1;
    private Class mActClass;

    private PIPManager() {
        mVideoView = new IjkVideoView(context);
        VideoViewManager.instance().add(mVideoView, PlayerUtils.PIP);
        mFloatController = new FloatController(context);
        mFloatView = new FloatView(context, 0, 0);
    }

    public static PIPManager init(Context ctx) {
        PIPManager.context = ctx.getApplicationContext();
        if (instance == null) {
            synchronized (PIPManager.class) {
                if (instance == null) {
                    instance = new PIPManager();
                }
            }
        }
        return instance;
    }

    public static PIPManager get() {
        if (instance == null) {
            synchronized (PIPManager.class) {
                if (instance == null) {
                    instance = new PIPManager();
                }
            }
        }
        return instance;
    }

    public void startFloatWindow() {
        if (mIsShowing) {
            return;
        }
        PlayerUtils.removeViewFormParent(mVideoView);
        mVideoView.setVideoController(mFloatController);
        mFloatController.setPlayState(mVideoView.getCurrentPlayState());
        mFloatController.setPlayerState(mVideoView.getCurrentPlayerState());
        mFloatView.addView(mVideoView);
        mFloatView.addToWindow();
        mIsShowing = true;
    }

    public void stopFloatWindow() {
        if (!mIsShowing) {
            return;
        }
        mFloatView.removeFromWindow();
        PlayerUtils.removeViewFormParent(mVideoView);
        mIsShowing = false;
    }

    public void setPlayingPosition(int position) {
        this.mPlayingPosition = position;
    }

    public int getPlayingPosition() {
        return mPlayingPosition;
    }

    public void pause() {
        if (mIsShowing) {
            return;
        }
        mVideoView.pause();
    }

    public void resume() {
        if (mIsShowing) {
            return;
        }
        mVideoView.resume();
    }

    public void reset() {
        if (mIsShowing) {
            return;
        }
        PlayerUtils.removeViewFormParent(mVideoView);
        mVideoView.release();
        mVideoView.setVideoController(null);
        mPlayingPosition = -1;
        mActClass = null;
    }

    public boolean onBackPress() {
        return !mIsShowing && mVideoView.onBackPressed();
    }

    public boolean isStartFloatWindow() {
        return mIsShowing;
    }

    /**
     * 显示悬浮窗
     */
    public void setFloatViewVisible() {
        if (mIsShowing) {
            mVideoView.resume();
            mFloatView.setVisibility(View.VISIBLE);
        }
    }

    public void setActClass(Class cls) {
        this.mActClass = cls;
    }

    public Class getActClass() {
        return mActClass;
    }

}