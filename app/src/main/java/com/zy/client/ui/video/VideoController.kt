package com.zy.client.ui.video

import android.app.Activity
import android.content.res.Configuration
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack
import com.shuyu.gsyvideoplayer.utils.OrientationUtils
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer
import com.zy.client.bean.entity.VideoDetailInfo
import com.zy.client.utils.ext.gone

/**
 * @author javakam
 * @date 2020/6/10 12:40
 */
class VideoController {
    private lateinit var videoPlayer: StandardGSYVideoPlayer
    private lateinit var activity: Activity
    private lateinit var videoOptionBuilder: GSYVideoOptionBuilder

    private var isPlay = false
    private var isPause = false

    var curVideoDetailInfo: VideoDetailInfo? = null

    //外部辅助的旋转，帮助全屏
    private var orientationUtils: OrientationUtils? = null

    fun init(activity: Activity, videoPlayer: StandardGSYVideoPlayer) {
        this.videoPlayer = videoPlayer
        this.activity = activity

        //初始化视频播放器
        initVideoPlayer()
    }

    private fun initVideoPlayer() {
        videoPlayer.backButton.gone()       //设置返回键
        videoPlayer.titleTextView.gone()    //增加title
        //外部辅助的旋转，帮助全屏
        orientationUtils = OrientationUtils(activity, videoPlayer).apply {
            //初始化不打开外部的旋转
            isEnable = true
        }

        videoOptionBuilder = GSYVideoOptionBuilder()
        videoOptionBuilder
            //.setThumbImageView(imageView) //增加封面
            .setIsTouchWiget(false)
            .setIsTouchWigetFull(true)
            .setRotateViewAuto(false)
            .setLockLand(false)
            .setAutoFullWithSize(false)
            .setShowFullAnimation(false)
            .setNeedLockFull(true)
            .setCacheWithPlay(true)
            .setVideoAllCallBack(object : GSYSampleCallBack() {
                override fun onPrepared(url: String, vararg objects: Any) {
                    super.onPrepared(url, *objects)
                    //开始播放了才能旋转和全屏
                    orientationUtils?.isEnable = true
                    isPlay = true
                }

                override fun onQuitFullscreen(url: String?, vararg objects: Any?) {
                    super.onQuitFullscreen(url, *objects)
                    orientationUtils?.backToProtVideo()
                }
            })
            .setLockClickListener { _, lock ->
                orientationUtils?.isEnable = !lock
            }
            .build(videoPlayer)

        //直接横屏
        videoPlayer.fullscreenButton
            .setOnClickListener {
                orientationUtils?.resolveByClick()
                //第一个true是否需要隐藏actionbar，第二个true是否需要隐藏 statusbar
                videoPlayer.startWindowFullscreen(activity, true, true)
            }
    }


    fun play(videoDetailInfo: VideoDetailInfo?) {
        curVideoDetailInfo = videoDetailInfo
        videoDetailInfo?.let {
            videoOptionBuilder
                .setUrl(it.videoUrl)
                .setVideoTitle(it.name)
                .build(videoPlayer)
        }
        videoPlayer.startPlayLogic()
    }

    fun play(playUrl: String?, name: String?) {
        videoOptionBuilder
            .setUrl(playUrl)
            .setVideoTitle(name)
            .build(videoPlayer)
        videoPlayer.startPlayLogic()
    }

    fun onPause() {
        videoPlayer.currentPlayer.onVideoPause()
        isPause = true
    }

    fun onResume() {
        videoPlayer.currentPlayer.onVideoResume(false)
        isPause = false
    }

    fun onDestroy() {
        if (isPlay) {
            videoPlayer.currentPlayer.release()
        }
        orientationUtils?.releaseListener()
    }

    fun onBackPressed(): Boolean {
        orientationUtils?.backToProtVideo()
        if (GSYVideoManager.backFromWindowFull(activity)) {
            return false
        }
        activity.finish()
        return true
    }

    fun onConfigurationChanged(newConfig: Configuration) {
        //如果旋转了就全屏
        if (isPlay && !isPause) {
            videoPlayer.onConfigurationChanged(activity, newConfig, orientationUtils, true, true)
        }
    }
}