package com.zy.client.ui.video

import android.content.res.Configuration
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BottomPopupView
import com.wuhenzhizao.titlebar.widget.CommonTitleBar
import com.zy.client.R
import com.zy.client.base.BaseActivity
import com.zy.client.bean.entity.DetailData
import com.zy.client.bean.entity.Video
import com.zy.client.bean.event.CollectEvent
import com.zy.client.common.ID
import com.zy.client.common.SOURCE_KEY
import com.zy.client.database.CollectDBUtils
import com.zy.client.database.CollectModel
import com.zy.client.http.ConfigManager
import com.zy.client.http.sources.BaseSource
import com.zy.client.utils.ClipboardUtils
import com.zy.client.utils.Utils
import com.zy.client.utils.ext.*
import com.zy.client.views.CustomGSYVideoPlayer
import kotlinx.android.synthetic.main.activity_video_detail.*
import org.greenrobot.eventbus.EventBus

/**
 * 视频详情页
 *
 * @author javakam
 * @date 2020-11-12 15:00:17
 */
class VideoDetailActivity : BaseActivity() {

    private lateinit var source: BaseSource
    private lateinit var id: String

    //
    private var videoController: VideoController? = null
    private var webController: WebController? = null
    private var playVideo: Video? = null
    private var detailData: DetailData? = null
    private var playVideoList: ArrayList<Video>? = null
    private var curPlayPos = 0

    //
    private var anthologyList: BottomPopupView? = null

    //
    private lateinit var titleBar: CommonTitleBar

    override fun getLayoutId() = R.layout.activity_video_detail

    override fun initView() {
        CustomGSYVideoPlayer.reset()

        val sourceKey = intent?.getStringExtra(SOURCE_KEY)
        source = ConfigManager.generateSource(sourceKey.noNull())
        id = intent?.getStringExtra(ID).noNull()

        initTitleBar()
    }

    private fun initTitleBar() {
        titleBar = findViewById(R.id.title_bar)
        titleBar.run {
            setListener { _, action, _ ->
                if (action == CommonTitleBar.ACTION_LEFT_BUTTON) {
                    finish()
                }
            }
        }
    }

    override fun initListener() {
        super.initListener()
        //网页播放
        ivWebPlay.setOnClickListener {
            if (playVideo == null || playVideo?.playUrl.isNullOrBlank()) {
                ToastUtils.showShort("无法播放")
                return@setOnClickListener
            }
            Utils.openBrowser(
                this,
                if (playVideo?.playUrl.isVideoUrl()) {
                    "http://zyplayer.fun/player/player.html?url=${playVideo?.playUrl?.noNull()}"
                } else {
                    playVideo?.playUrl.noNull()
                }
            )
        }

        //选集
        llAnthology.setOnClickListener {
            if (playVideoList?.size ?: 0 > 1) {
                if (anthologyList == null) {
                    anthologyList = XPopup.Builder(this)
                        .asBottomList(
                            "选集",
                            playVideoList?.map { it.name }?.toTypedArray(),
                            null,
                            0
                        ) { position, _ ->
                            curPlayPos = position
                            playVideo(playVideoList?.get(position))
                        }
                        .bindLayout(R.layout.fragment_search_result)
                }
                anthologyList?.show()
            }
        }

        //收藏
        ivCollect.setOnClickListener {
            if (ivCollect.isSelected) {
                val delete = CollectDBUtils.delete(id + source.key)
                if (delete) {
                    ivCollect.isSelected = false
                    EventBus.getDefault().postSticky(CollectEvent())
                } else {
                    ToastUtils.showShort("取消收藏失败")
                }
            } else {
                val collectDBModel = CollectModel()
                collectDBModel.uniqueKey = id + source.key
                collectDBModel.videoId = id
                collectDBModel.name = detailData?.name
                collectDBModel.sourceKey = source.key
                collectDBModel.sourceName = source.name
                CollectDBUtils.saveAsync(collectDBModel) {
                    if (it) {
                        ivCollect.isSelected = true
                        EventBus.getDefault().postSticky(CollectEvent())
                    } else {
                        ToastUtils.showShort("收藏失败")
                    }
                }
            }
        }

        //下载
        ivDownload.setOnClickListener {
//            if (source?.downloadBaseUrl.isNullOrBlank()) {
//                ToastUtils.showShort("该资源暂不支持下载哦~")
//                return@setOnClickListener
//            }
//            source?.requestDownloadData(id) {
//                if (it.isNullOrEmpty()) {
//                    ToastUtils.showShort("该资源暂不支持下载哦~")
//                    return@requestDownloadData
//                } else {
//                    DetailFilmDownloadDialog().show(it.map { data ->
//                        FilmItemInfo(
//                            playVideo?.name.textOrDefault(),
//                            data.name,
//                            data.downloadUrl
//                        )
//                    } as ArrayList<FilmItemInfo>, childFragmentManager)
//                }
//            }
            if (playVideo?.playUrl.isVideoUrl()) {
                ClipboardUtils.copyText(playVideo?.playUrl)
                ToastUtils.showLong("地址已复制，快去下载吧~\n${playVideo?.playUrl}")
            } else {
                ToastUtils.showShort("该资源暂不支持下载哦~")
            }
        }
    }

    override fun initData() {
        super.initData()

        CollectDBUtils.searchAsync(id + source.key) {
            if (it != null && it.isSaved) {
                ivCollect.isSelected = true
            }
        }

        source.requestDetailData(id) {
            it?.apply {
                if (!videoList.isNullOrEmpty()) {
                    setData(it)
                }
            }
        }
    }

    override fun onPause() {
        webController?.onPause()
        videoController?.onPause()
        super.onPause()
    }

    override fun onResume() {
        webController?.onResume()
        videoController?.onResume()
        super.onResume()
    }


    override fun onDestroy() {
        webController?.onDestroy()
        videoController?.onDestroy()
        super.onDestroy()
    }

    override fun onBackPressed() {
        if (videoController?.onBackPressed() == true || webController?.onBackPressed() == true) {
            super.onBackPressed()
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        videoController?.onConfigurationChanged(newConfig)
    }

    private fun setData(detailData: DetailData) {
        this.detailData = detailData
        detailData.run {
            playVideoList = videoList

            //是否支持选集
            if (playVideoList?.size ?: 0 > 1) ivPlayMore.visible() else ivPlayMore.gone()

            playVideo(detailData.videoList?.get(0))
            //名字
            tvName.text = name
            titleBar.centerTextView?.text = name
            //简介
            tvDesc.text = des.noNull()
        }
    }

    private fun playVideo(playVideo: Video?) {
        if (playVideo == null) return
        this.playVideo = playVideo
        if (playVideo.playUrl.isVideoUrl()) {
            //初始化视频控制
            if (videoController == null) {
                videoController = VideoController()
                videoController?.init(this, videoPlayer)
            }

            videoController?.play(
                playVideo.playUrl,
                "${detailData?.name.noNull()}   ${playVideo.name.noNull()}"
            )
            videoPlayer.visible()
            flWebView.gone()
        } else {
            //网页播放
            if (webController == null) {
                webController = WebController()
            }
            webController?.loadUrl(this, playVideo.playUrl, flWebView)
            videoPlayer.gone()
            flWebView.visible()
        }
        //正在播放
        tvCurPlayName.text = playVideo.name.noNull()
    }
}