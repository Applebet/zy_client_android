package com.zy.client.ui.video

import ando.player.IjkVideoView
import ando.player.StandardVideoController
import ando.player.component.*
import ando.player.pip.PIPManager
import ando.player.utils.VideoUtils
import android.app.Activity
import android.content.Context
import android.view.MenuItem
import android.widget.ImageView
import com.dueeeke.videoplayer.controller.GestureVideoController
import com.dueeeke.videoplayer.player.VideoView.*
import com.dueeeke.videoplayer.player.VideoViewManager
import com.dueeeke.videoplayer.util.L
import com.zy.client.utils.PermissionManager.overlay
import com.zy.client.utils.ext.isVideoUrl
import com.zy.client.utils.ext.noNull

/**
 * @author javakam
 * @date 2020/6/10 12:40
 */
class VideoController {

    companion object {
        //private const val THUMB = "https://cdn.pixabay.com/photo/2017/07/10/23/35/globe-2491989_960_720.jpg"
        private const val THUMB =
            "https://cdn.pixabay.com/photo/2020/03/21/03/04/future-4952543_960_720.jpg"
        private const val VOD_URL =
            "http://vfx.mtime.cn/Video/2019/03/14/mp4/190314223540373995.mp4"
        private const val LIVE_URL = "http://220.161.87.62:8800/hls/0/index.m3u8"
    }

    private lateinit var context: Context
    private lateinit var controller: GestureVideoController
    private lateinit var titleView: TitleView
    lateinit var videoPlayer: IjkVideoView
    var pipManager: PIPManager? = null
    var currUrl: String? = null
    var enableBackgroundPlay = false

    fun init(context: Context, isLive: Boolean) {
        this.pipManager = PIPManager.get()
        val videoPlayer = VideoViewManager.instance().get(VideoUtils.PIP) as IjkVideoView
        init(context = context, ijkVideoView = videoPlayer, isLive = isLive){
            //从 FloatView 上移除 VideoView
            if (pipManager?.isStartFloatWindow == true) {
                pipManager?.stopFloatWindow()
                controller.setPlayerState(videoPlayer.currentPlayerState)
                controller.setPlayState(videoPlayer.currentPlayState)
            }
        }
    }

    fun init(
        context: Context,
        ijkVideoView: IjkVideoView,
        isLive: Boolean,
        block: () -> Unit = {}
    ) {
        this.videoPlayer = ijkVideoView
        this.context = context

        controller = StandardVideoController(context)

        //在控制器上显示调试信息
        //controller.addControlComponent(DebugInfoView(context))
        //在LogCat显示调试信息
        //controller.addControlComponent(PlayerMonitor())

        //根据屏幕方向自动进入/退出全屏
        controller.setEnableOrientation(false)

        //准备播放界面
        val prepareView = PrepareView(context)
        //val thumb = prepareView.findViewById<ImageView>(R.id.thumb)
        //loadImage(thumb, THUMB)
        //loadImage(thumb, ContextCompat.getDrawable(context, R.drawable.rectangle_video_preview), null)
        controller.addControlComponent(prepareView)

        controller.addControlComponent(CompleteView(context)) //自动完成播放界面
        controller.addControlComponent(ErrorView(context)) //错误界面

        titleView = TitleView(context) //标题栏
        titleView.setPortraitVisibility(true)
        controller.addControlComponent(titleView)

        //根据是否为直播设置不同的底部控制条
        if (isLive) {
            controller.addControlComponent(LiveControlView(context)) //直播控制条
        } else {
            val vodControlView = VodControlView(context) //点播控制条
            //是否显示底部进度条。默认显示
            //vodControlView.showBottomProgress(false);
            controller.addControlComponent(vodControlView)
        }

        val gestureControlView = GestureView(context) //滑动控制视图
        controller.addControlComponent(gestureControlView)
        //根据是否为直播决定是否需要滑动调节进度
        controller.setCanChangePosition(!isLive)

        //如果你不需要单独配置各个组件，可以直接调用此方法快速添加以上组件
        //controller.addDefaultControlComponent(title, isLive);

        //竖屏也开启手势操作，默认关闭
        //controller.setEnableInNormal(true);

        //滑动调节亮度，音量，进度，默认开启
        //controller.setGestureEnabled(false);

        //适配刘海屏，默认开启
        //controller.setAdaptCutout(false);

        //设置静音
        //videoPlayer.isMute = true

        //设置镜像旋转，暂不支持SurfaceView
        //videoPlayer.setMirrorRotation(true)

        //截图，暂不支持SurfaceView
        //videoPlayer.doScreenShot()

        //设置播放速度 eg: 0.5f 0.75f 1.0f 1.5f 2.0f
        //videoPlayer.speed = 2.0f

        //如果你不想要UI，不要设置控制器即可
        videoPlayer.setVideoController(controller)

        //保存播放进度
        //videoPlayer.setProgressManager(new ProgressManagerImpl());
        //播放状态监听
        videoPlayer.addOnStateChangeListener(mOnStateChangeListener)

        //临时切换播放核心，全局请在Application中通过VideoConfig配置
        //videoPlayer.setPlayerFactory(IjkPlayerFactory.create())
        //videoPlayer.setPlayerFactory(ExoMediaPlayerFactory.create())
        //videoPlayer.setPlayerFactory(AndroidMediaPlayerFactory.create())

        videoPlayer.setScreenScaleType(SCREEN_SCALE_16_9)

        initPipEvent()

        block.invoke()
    }

    private val mOnStateChangeListener: OnStateChangeListener =
        object : SimpleOnStateChangeListener() {
            override fun onPlayerStateChanged(playerState: Int) {
                when (playerState) {
                    PLAYER_NORMAL -> {
                    }
                    PLAYER_FULL_SCREEN -> {
                    }
                }
            }

            override fun onPlayStateChanged(playState: Int) {
                when (playState) {
                    STATE_IDLE -> {
                    }
                    STATE_PREPARING -> {
                    }
                    STATE_PREPARED -> {
                    }
                    STATE_PLAYING -> {
                        //需在此时获取视频宽高
                        val videoSize: IntArray = videoPlayer.videoSize
                        L.d("视频宽：" + videoSize[0])
                        L.d("视频高：" + videoSize[1])
                    }
                    STATE_PAUSED -> {
                    }
                    STATE_BUFFERING -> {
                    }
                    STATE_BUFFERED -> {
                    }
                    STATE_PLAYBACK_COMPLETED -> {
                    }
                    STATE_ERROR -> {
                    }
                }
            }
        }

    /**
     * use cache :
     *      PreloadManager.getInstance(this).getPlayUrl(item.videoDownloadUrl);
     *      val cacheServer: HttpProxyCacheServer = ProxyVideoCacheManager.getProxy(context)
     *      val proxyUrl = cacheServer.getProxyUrl(videoUrl)
     *      videoPlayer.setUrl(proxyUrl)
     */
    fun startPlay(videoUrl: String?, title: String?) {
        if (videoUrl?.isVideoUrl() == false || videoPlayer.isPlaying) return
        currUrl = videoUrl
        titleView.setTitle(title.noNull())
        videoPlayer.release()
        videoPlayer.setUrl(VOD_URL)//videoUrl
        videoPlayer.start()
    }

    /**
     * 小窗返回的页面
     */
    fun setRecoverActivity(clz: Class<*>) {
        pipManager?.actClass = clz
    }

    fun setVideoTag(any: Any?) {
        any?.apply {
            pipManager?.videoTag = any
        }
    }

    fun getVideoTag(): Any? = pipManager?.videoTag

    /**
     * 悬浮窗按钮
     */
    fun initPipEvent() {
        val ivPip: ImageView = titleView.findViewById(ando.player.R.id.iv_pip)
        ivPip.setOnClickListener {
            overlay(context as Activity, onGranted = {
                pipManager?.startFloatWindow()
                pipManager?.resume()
                (context as Activity).finish()
            })
        }
    }

    fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            (context as Activity).finish()
        }
        return (context as Activity).onOptionsItemSelected(item)
    }

    fun onResume() {
        if (enableBackgroundPlay) return
        if (pipManager != null) {
            pipManager?.resume()
        } else videoPlayer.resume()
    }

    fun onPause() {
        if (enableBackgroundPlay) return
        if (pipManager != null) {
            pipManager?.pause()
        } else videoPlayer.pause()
    }

    fun onDestroy() {
        if (pipManager != null) {
            pipManager?.release()
        } else videoPlayer.release()
    }

    fun onBackPressed(): Boolean {
        if (pipManager != null && pipManager?.onBackPressed() == true) {
            return false
        }
        if (videoPlayer.isFullScreen) {
            videoPlayer.stopFullScreen()
            return false
        }
        return true
    }

}