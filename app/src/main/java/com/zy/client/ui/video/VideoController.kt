package com.zy.client.ui.video

import ando.player.IjkVideoView
import ando.player.R
import ando.player.StandardVideoController
import ando.player.component.*
import ando.player.dialog.SimplePlayerCallBack
import ando.player.pip.PIPManager
import ando.player.setting.UserSetting
import ando.player.setting.UserSetting.PIP
import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import com.dueeeke.videoplayer.player.VideoView.*
import com.dueeeke.videoplayer.player.VideoViewManager
import com.dueeeke.videoplayer.util.L
import com.lxj.xpopup.XPopup
import com.zy.client.bean.Video
import com.zy.client.bean.VideoHistory
import com.zy.client.bean.VideoEntity
import com.zy.client.common.getScreenShotPath
import com.zy.client.database.HistoryDBUtils
import com.zy.client.utils.PermissionManager.overlay
import com.zy.client.utils.PermissionManager.proceedStoragePermission
import com.zy.client.utils.ext.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author javakam
 * @date 2020/6/10 12:40
 */
class VideoController {

    interface CallBack {
        fun onListVideoSelected(video: Video)
    }

    companion object {
        //private const val THUMB = "https://cdn.pixabay.com/photo/2017/07/10/23/35/globe-2491989_960_720.jpg"
        private const val THUMB =
            "https://cdn.pixabay.com/photo/2020/03/21/03/04/future-4952543_960_720.jpg"
        private const val VOD_URL =
            "http://vfx.mtime.cn/Video/2019/03/14/mp4/190314223540373995.mp4"
        private const val LIVE_URL = "http://220.161.87.62:8800/hls/0/index.m3u8"
    }

    private lateinit var context: Context
    private lateinit var controller: StandardVideoController
    private lateinit var videoPlayer: IjkVideoView

    private lateinit var prepareView: PrepareView
    private lateinit var titleView: TitleView
    private lateinit var vodControlView: VodControlView

    private var pipManager: PIPManager? = null
    private var videoList: List<Video>? = null
    var currentUrl: String? = null
    var currentListPosition: Int = 0
    var callBack: CallBack? = null

    fun init(context: Context, isLive: Boolean) {
        this.pipManager = PIPManager.get()
        val videoPlayer = VideoViewManager.instance().get(PIP) as IjkVideoView
        init(context = context, ijkVideoView = videoPlayer, isLive = isLive) {
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
        prepareView = PrepareView(context)
        //val thumb = prepareView.findViewById<ImageView>(R.id.thumb)
        //loadImage(thumb, THUMB)
        //loadImage(thumb, ContextCompat.getDrawable(context, R.drawable.rectangle_video_preview), null)
        controller.addControlComponent(prepareView)


        controller.addControlComponent(CompleteView(context)) //自动完成播放界面
        controller.addControlComponent(ErrorView(context)) //错误界面

        titleView = TitleView(context) //标题栏
        titleView.setLive(isLive)
        titleView.showWhenPortrait(true)
        controller.addControlComponent(titleView)

        //根据是否为直播设置不同的底部控制条
        if (isLive) {
            controller.addControlComponent(LiveControlView(context)) //直播控制条
        } else {
            vodControlView = VodControlView(context) //点播控制条
            //是否显示底部进度条。默认显示
            vodControlView.showBottomProgress(true)
            vodControlView.setCallBack(object : SimplePlayerCallBack() {
                override fun onListItemClick(item: String, position: Int) {
                    super.onListItemClick(item, position)
                    vodControlView.postDelayed({
                        videoList?.get(position)?.apply {
                            currentListPosition = position
                            if (playUrl?.isVideoUrl() == true) {
                                vodControlView.dismissDialogs()
                                startPlay(videoUrl = playUrl, title = name)
                                updateVodViewPosition()
                                callBack?.onListVideoSelected(this)
                            }
                        }
                    }, 200)
                }
            })
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

        //截屏
        controller.setScreenShotListener {
            proceedStoragePermission((context as Activity)) {
                if (it) {
                    val timestamp =
                        SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Date())
                    insertBitmap(
                        context, videoPlayer.doScreenShot(),
                        createContentValues(
                            "screenshot_$timestamp",
                            relativePath = getScreenShotPath()
                        )
                    ) {
                        context.toastShort("保存截图成功!")
                    }
                }
            }
        }

        initListeners()
        block.invoke()
    }

    private fun initListeners() {
        val ivSetting = titleView.findViewById<ImageView>(R.id.iv_setting)
        //悬浮窗按钮
        titleView.findViewById<ImageView>(R.id.iv_pip).setOnClickListener {
            overlay(context as Activity, onGranted = {
                pipManager?.startFloatWindow()
                pipManager?.resume()
                (context as? Activity)?.finish()
            })
        }

        //设置
        ivSetting.setOnClickListener {
            val s = XPopup.Builder(context).asConfirm(
                "是否开启后台播放?", ""
            ) {
                UserSetting.setBackgroundPlay(context, true)
            }.show()
        }
    }

    fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            (context as Activity).finish()
        }
        return (context as Activity).onOptionsItemSelected(item)
    }

    fun onResume() {
        if (isEnableBackPlay()) return
        if (pipManager != null) {
            pipManager?.resume()
        } else videoPlayer.resume()
    }

    fun onPause() {
        if (isEnableBackPlay()) return
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

    private val mOnStateChangeListener: OnStateChangeListener =
        object : SimpleOnStateChangeListener() {
            override fun onPlayerStateChanged(playerState: Int) {

                //Log.e("123", "onPlayerStateChanged My Controller = $playerState")
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


    fun getPlayer(): IjkVideoView? = if (pipManager != null) {
        pipManager?.getPlayer()
    } else videoPlayer

    fun isEnableBackPlay(): Boolean = UserSetting.getBackgroundPlay(context)

    /**
     * use cache :
     *      PreloadManager.getInstance(this).getPlayUrl(item.videoDownloadUrl);
     *      val cacheServer: HttpProxyCacheServer = ProxyVideoCacheManager.getProxy(context)
     *      val proxyUrl = cacheServer.getProxyUrl(videoUrl)
     *      videoPlayer.setUrl(proxyUrl)
     */
    fun startPlay(videoUrl: String?, title: String?) {
        Log.i("123", "startPlay  currentUrl= $currentUrl  videoUrl= $videoUrl  title=$title")
        if (videoUrl?.isVideoUrl() == false) return
        //放止同一剧集重复点击
        if (currentUrl != null && currentUrl.equals(videoUrl, true)) return

        currentUrl = videoUrl
        titleView.setTitle(title.noNull())
        videoPlayer.release()
        videoPlayer.setUrl(videoUrl)//videoUrl  VOD_URL
//        videoPlayer.setUrl(
//            "https://cctvtxyh5c.liveplay.myqcloud.com/live/cdrmcctv2_1_td.m3u8"
//
//            ,mutableMapOf(
//                "User-Agent" to "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.25 Safari/537.36 Core/1.70.3776.400 QQBrowser/10.6.4212.400",
////                "Referer" to "https://tv.cctv.com/live/cctv2/?spm=C28340.PGJXtt9IosRP.S63207.89",
////                "Host" to "vdn.live.cntv.cn"
//            )
//        )
        videoPlayer.start()
    }

    fun setVideoList(data: List<Video>?) {
        this.videoList = data
        updateVodViewPosition()
    }

    fun updateVodViewPosition() {
        videoList?.apply {
            if (size > 1) {
                val list = mutableListOf<String>()
                this.forEachIndexed { i: Int, _: Video ->
                    list.add((size - i).toString())
                }
                Log.w("123", "updateVodViewPosition setVideoList $currentListPosition")
                vodControlView.setVideoList(list, currentListPosition)
            }
        }
    }

    //PipManager
    //------------------------------------------------

    /**
     * 小窗返回的页面
     */
    fun setRecoverActivity(clz: Class<*>) {
        pipManager?.actClass = clz
    }

    fun setPipCacheData(data: VideoEntity?) {
        data?.apply {
            pipManager?.cacheData?.putSerializable("pipCache", data)
        }
    }

    fun getPipCacheData(): VideoEntity? =
        pipManager?.cacheData?.getSerializable("pipCache") as? VideoEntity

    fun clearPipCacheData() = pipManager?.clearCacheData()

    //History -> HistoryDBUtils
    //------------------------------------------------

    fun saveHistory(history: VideoHistory) {
        HistoryDBUtils.saveAsync(history) {
            //Log.w("123", "saveHistory $it")
        }
    }

    fun searchHistory(
        sourceKey: String?,
        tid: String?,
        vid: String?,
        callback: ((VideoHistory?) -> Unit)?
    ) {
        HistoryDBUtils.searchAsync(uniqueId = "$sourceKey$tid$vid", callback)
    }

    fun clearHistory(
        sourceKey: String?,
        tid: String?,
        vid: String?,
    ): Boolean = HistoryDBUtils.delete(uniqueId = "$sourceKey$tid$vid")

    fun clearAllHistory(): Boolean {
        return HistoryDBUtils.deleteAll()
    }

}