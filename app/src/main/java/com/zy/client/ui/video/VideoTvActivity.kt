package com.zy.client.ui.video

import android.graphics.Color
import android.widget.FrameLayout
import android.widget.TextView
import com.wuhenzhizao.titlebar.statusbar.StatusBarUtils
import com.zy.client.R
import com.zy.client.base.BaseMediaActivity
import com.zy.client.bean.VideoSource
import com.zy.client.common.TV_BEAN
import com.zy.client.database.SourceModel

class VideoTvActivity : BaseMediaActivity() {

    private lateinit var mTvName: TextView
    private lateinit var mTvGroup: TextView
    private lateinit var videoContainer: FrameLayout
    private var mVideoSource: VideoSource? = null

    override fun getLayoutId(): Int = R.layout.activity_video_tv

    override fun initView() {
        super.initView()
        StatusBarUtils.setStatusBarColor(window, Color.BLACK, 0)

        videoContainer = findViewById(R.id.playerContainer)
        mTvName = findViewById(R.id.tv_name)
        mTvGroup = findViewById(R.id.tv_group)

        //初始化视频控制
        videoController = VideoController()
        videoController?.init(this, true)

        //小窗情况下 缓存请求数据 Bundle
        val sourceModel = intent.getSerializableExtra(TV_BEAN) as? SourceModel
        if (sourceModel != null) {
            mVideoSource = VideoSource(name = sourceModel.name, tvUrl = sourceModel.url, group = sourceModel.group)
        }
        if (mVideoSource == null) {
            mVideoSource = videoController?.getPipCacheData()
        } else {//重置
            videoController?.setPipCacheData(mVideoSource)
        }

        videoController?.setRecoverActivity(VideoTvActivity::class.java)
        videoContainer.addView(videoController?.getPlayer())
    }

    override fun initData() {
        super.initData()
        videoController?.startPlay(mVideoSource?.tvUrl, mVideoSource?.name)
        mVideoSource?.apply {
            mTvName.text = name
            mTvGroup.text = group
        }
    }

}