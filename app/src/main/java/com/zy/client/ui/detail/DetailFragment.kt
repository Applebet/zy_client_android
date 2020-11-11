package com.zy.client.ui.detail

import android.content.res.Configuration
import android.os.Bundle
import androidx.core.os.bundleOf
import com.blankj.utilcode.util.ToastUtils
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BottomPopupView
import com.lxj.xpopup.interfaces.OnSelectListener
import com.zy.client.R
import com.zy.client.bean.event.CollectEvent
import com.zy.client.http.sources.BaseSource
import com.zy.client.bean.entity.DetailData
import com.zy.client.bean.entity.Video
import com.zy.client.base.BaseFragment
import com.zy.client.database.CollectModel
import com.zy.client.database.CollectDBUtils
import com.zy.client.ui.detail.controller.VideoController
import com.zy.client.ui.detail.controller.WebController
import com.zy.client.utils.ClipboardUtils
import com.zy.client.utils.Utils
import com.zy.client.views.CustomGSYVideoPlayer
import com.wuhenzhizao.titlebar.widget.CommonTitleBar
import com.zy.client.http.ConfigManager
import com.zy.client.utils.ext.gone
import com.zy.client.utils.ext.isVideoUrl
import com.zy.client.utils.ext.textOrDefault
import com.zy.client.utils.ext.visible
import kotlinx.android.synthetic.main.fragment_detail.*
import org.greenrobot.eventbus.EventBus


/**
 * @author javakam
 *
 * @date 2020/9/8 21:48
 * @desc 详情页
 */

const val SOURCE_KEY = "source_key"
const val ID = "id"

class DetailFragment : BaseFragment() {

    private var source: BaseSource? = null
    private lateinit var id: String
    private var playVideo: Video? = null
    private var detailData: DetailData? = null
    private var playVideoList: ArrayList<Video>? = null
    private var curPlayPos = 0

        private var videoController: VideoController? = null
//    private var videoController: JZVideoController? = null
    private var webController: WebController? = null

    private var anthologyList: BottomPopupView? = null

    companion object {
        fun instance(sourceKey: String, id: String): DetailFragment {
            return DetailFragment().apply {
                arguments = bundleOf(SOURCE_KEY to sourceKey, ID to id)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sourceKey = arguments?.getString(SOURCE_KEY)
        source = ConfigManager.generateSource(sourceKey.textOrDefault())
        id = arguments?.getString(ID).textOrDefault()

        CustomGSYVideoPlayer.reset()
    }

    override fun getLayoutId(): Int = R.layout.fragment_detail

    override fun initTitleBar(titleBar: CommonTitleBar?) {
        titleBar?.run {
            setListener { v, action, extra ->
                when (action) {
                    CommonTitleBar.ACTION_LEFT_BUTTON -> {
                        requireActivity().finish()
                    }
                }
            }
        }
    }

    override fun initView() {
        super.initView()
        statusView.failRetryClickListener = {
            initData()
        }

        CollectDBUtils.searchAsync(id + source?.key) {
            if (it != null && it.isSaved) {
                ivCollect.isSelected = true
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
                requireActivity(),
                if (playVideo?.playUrl.isVideoUrl()) {
                    "http://zyplayer.fun/player/player.html?url=${playVideo?.playUrl?.textOrDefault()}"
                } else {
                    playVideo?.playUrl.textOrDefault()
                }
            )
        }
        //选集
        llAnthology.setOnClickListener {
            if (playVideoList?.size ?: 0 > 1) {
                if (anthologyList == null) {
                    anthologyList = XPopup.Builder(requireActivity())
                        .asBottomList("选集",
                            playVideoList?.map { it.name }?.toTypedArray(),
                            null,
                            0,
                            OnSelectListener { position, text ->
                                curPlayPos = position
                                playVideo(playVideoList?.get(position))
                            })
                        .bindLayout(R.layout.fragment_search_result)
                }
                anthologyList?.show()
            }
        }

        ivCollect.setOnClickListener {
            //收藏
            if (ivCollect.isSelected) {
                val delete = CollectDBUtils.delete(id + source?.key)
                if (delete) {
                    ivCollect.isSelected = false
                    EventBus.getDefault().postSticky(CollectEvent())
                } else {
                    ToastUtils.showShort("取消收藏失败")
                }
            } else {
                val collectDBModel = CollectModel()
                collectDBModel.uniqueKey = id + source?.key
                collectDBModel.videoId = id
                collectDBModel.name = detailData?.name
                collectDBModel.sourceKey = source?.key
                collectDBModel.sourceName = source?.name
                val save = CollectDBUtils.save(collectDBModel)
                if (save) {
                    ivCollect.isSelected = true
                    EventBus.getDefault().postSticky(CollectEvent())
                } else {
                    ToastUtils.showShort("收藏失败")
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
                ToastUtils.showShort("地址已复制，快去下载吧~")
            } else {
                ToastUtils.showShort("该资源暂不支持下载哦~")
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

    override fun onDestroyView() {
        webController?.onDestroy()
        videoController?.onDestroy()
        super.onDestroyView()
    }

    override fun onBackPressed(): Boolean {
        if (videoController?.onBackPressed() == true || webController?.onBackPressed() == true){
            return true
        }
        return false
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        videoController?.onConfigurationChanged(newConfig)
    }

    override fun initData() {
        super.initData()
        statusView.setLoadingStatus()
        source?.requestDetailData(id) {
            when {
                it == null -> {
                    statusView.setFailStatus()
                }
                it.videoList.isNullOrEmpty() -> {
                    statusView.setEmptyStatus()
                }
                else -> {
                    statusView.setSuccessStatus()
                    setData(it)
                }
            }
        }
    }

    private fun setData(detailData: DetailData) {
        this.detailData = detailData
        detailData.run {
            playVideoList = videoList

            //是否支持选集
            if (playVideoList?.size ?: 0 > 1) {
                ivPlayMore.visible()
            } else {
                ivPlayMore.gone()
            }

            playVideo(detailData.videoList?.get(0))
            //名字
            tvName.text = name
            titleBar?.centerTextView?.text = name
            //简介
            tvDesc.text = des.textOrDefault()
        }
    }

    private fun playVideo(playVideo: Video?) {
        if (playVideo == null) return
        this.playVideo = playVideo
        if (playVideo.playUrl.isVideoUrl()) {
            //初始化视频控制
            if (videoController == null) {
                videoController = VideoController()
                videoController?.init(requireActivity(), videoPlayer)
            }

//            if (videoController == null) {
//                videoController = JZVideoController()
//                videoController?.init(videoPlayer)
//            }
            videoController?.play(
                playVideo.playUrl,
                "${detailData?.name.textOrDefault()}   ${playVideo.name.textOrDefault()}"
            )
            videoPlayer.visible()
            flWebView.gone()
        } else {
            //网页播放
            if (webController == null) {
                webController = WebController()
            }
            webController?.loadUrl(this@DetailFragment, playVideo.playUrl, flWebView)
            videoPlayer.gone()
            flWebView.visible()
        }
        //正在播放
        tvCurPlayName.text = playVideo?.name.textOrDefault()
    }

}