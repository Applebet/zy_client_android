package com.zy.client.ui.video

import ando.player.IjkVideoView
import android.graphics.Color
import android.widget.TextView
import com.wuhenzhizao.titlebar.statusbar.StatusBarUtils
import com.zy.client.R
import com.zy.client.base.BaseActivity
import com.zy.client.common.TV_BEAN
import com.zy.client.database.TvModel

class VideoTvActivity : BaseActivity() {

    private lateinit var mIPTVModel: TvModel
    private lateinit var mTvName: TextView
    private lateinit var mTvGroup: TextView
    private lateinit var videoPlayer: IjkVideoView
    private lateinit var videoController: VideoController

    override fun getLayoutId(): Int = R.layout.activity_video_tv

    override fun initView() {
        super.initView()
        StatusBarUtils.setStatusBarColor(window, Color.BLACK, 0)
        mIPTVModel = intent.getSerializableExtra(TV_BEAN) as? TvModel ?: return

        videoPlayer = findViewById(R.id.videoPlayer)

        //初始化视频控制
        videoController = VideoController()
        videoController.init(this, videoPlayer)
        videoPlayer.isLive = true
    }

    override fun initData() {
        super.initData()
        videoController.startPlay(mIPTVModel.url, mIPTVModel.name)
        mIPTVModel.apply {
            mTvName.text = name
            mTvGroup.text = group
        }
    }

    override fun onPause() {
        videoController.onPause()
        super.onPause()
    }

    override fun onResume() {
        videoController.onResume()
        super.onResume()
    }

    override fun onDestroy() {
        videoController.onDestroy()
        super.onDestroy()
    }

    override fun onBackPressed() {
        if (videoController.onBackPressed()) {
            super.onBackPressed()
        }
    }

}