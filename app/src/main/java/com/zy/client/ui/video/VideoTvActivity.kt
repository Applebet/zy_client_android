package com.zy.client.ui.video

import ando.player.IjkVideoView
import android.graphics.Color
import android.widget.TextView
import com.wuhenzhizao.titlebar.statusbar.StatusBarUtils
import com.zy.client.R
import com.zy.client.base.BaseMediaActivity
import com.zy.client.common.TV_BEAN
import com.zy.client.database.SourceModel

class VideoTvActivity : BaseMediaActivity() {

    private lateinit var mSourceModel: SourceModel
    private lateinit var mTvName: TextView
    private lateinit var mTvGroup: TextView
    private lateinit var videoPlayer: IjkVideoView

    override fun getLayoutId(): Int = R.layout.activity_video_tv

    override fun initView() {
        super.initView()
        StatusBarUtils.setStatusBarColor(window, Color.BLACK, 0)
        mSourceModel = intent.getSerializableExtra(TV_BEAN) as? SourceModel ?: return

        videoPlayer = findViewById(R.id.videoPlayer)
        mTvName = findViewById(R.id.tv_name)
        mTvGroup = findViewById(R.id.tv_group)

        //初始化视频控制
        videoController = VideoController()
        videoController?.init(this, videoPlayer, true)
        videoPlayer.isLive = true
    }

    override fun initData() {
        super.initData()
        videoController?.startPlay(mSourceModel.url, mSourceModel.name)
        mSourceModel.apply {
            mTvName.text = name
            mTvGroup.text = group
        }
    }

}