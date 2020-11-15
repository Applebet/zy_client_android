package com.zy.client.ui.video

import ando.player.IjkVideoView
import android.graphics.Color
import com.dueeeke.videoplayer.util.CutoutUtil
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.impl.BottomListPopupView
import com.wuhenzhizao.titlebar.statusbar.StatusBarUtils
import com.zy.client.R
import com.zy.client.base.BaseActivity
import com.zy.client.bean.VideoDetail
import com.zy.client.bean.Video
import com.zy.client.bean.event.CollectEvent
import com.zy.client.common.ID
import com.zy.client.common.SOURCE_KEY
import com.zy.client.common.VIDEO_VIEW_HEIGHT
import com.zy.client.database.CollectDBUtils
import com.zy.client.database.CollectModel
import com.zy.client.http.ConfigManager
import com.zy.client.http.repo.CommonRepository
import com.zy.client.utils.ClipboardUtils
import com.zy.client.utils.Utils
import com.zy.client.utils.ext.*
import com.zy.client.views.loader.LoadState
import com.zy.client.views.loader.LoaderLayout
import kotlinx.android.synthetic.main.activity_video_detail.*
import org.greenrobot.eventbus.EventBus

/**
 * 视频详情页
 *
 * @author javakam
 * @date 2020-11-12 15:00:17
 */
class VideoDetailActivity : BaseActivity() {

    private lateinit var source: CommonRepository
    private lateinit var id: String
    private lateinit var videoPlayer: IjkVideoView
    private lateinit var statusView: LoaderLayout

    private var videoController: VideoController? = null
    private var webController: WebController? = null
    private var mVideoDetail: VideoDetail? = null
    private var mVideo: Video? = null
    private var mVideoList: List<Video>? = null
    private var mCurrVideoListPos = 0

    private var mSelectDialog: BottomListPopupView? = null

    override fun getLayoutId() = R.layout.activity_video_detail

    override fun initView() {
        StatusBarUtils.setStatusBarColor(window, Color.BLACK, 0)
        //PermissionManager.verifyStoragePermissions(this)

        val sourceKey = intent?.getStringExtra(SOURCE_KEY)
        source = ConfigManager.generateSource(sourceKey.noNull())
        id = intent?.getStringExtra(ID).noNull()

        videoPlayer = findViewById(R.id.videoPlayer)
        statusView = findViewById(R.id.statusView)
        statusView.setLoadState(LoadState.LOADING)
        //初始化视频控制
        if (videoController == null) {
            videoController = VideoController()
            videoController?.init(this, videoPlayer)
            videoPlayer.isLive = false
        }
    }

    override fun initListener() {
        super.initListener()
        //网页播放
        ivWebPlay.setOnClickListener {
            if (mVideo == null || mVideo?.playUrl.isNullOrBlank()) {
                ToastUtils.showShort("无法播放")
                return@setOnClickListener
            }
            Utils.openBrowser(
                this,
                if (mVideo?.playUrl.isVideoUrl()) {
                    "http://zyplayer.fun/player/player.html?url=${mVideo?.playUrl?.noNull()}"
                } else {
                    mVideo?.playUrl.noNull()
                }
            )
        }

        //选集
        val hasLiuHai = CutoutUtil.allowDisplayToCutout(this)
        val dialogHeight =
            (screenHeight - resources.getDimensionPixelSize(VIDEO_VIEW_HEIGHT))
                .minus(if (hasLiuHai) 0 else DimensionUtils.getStatusBarHeight())

        llAnthology.setOnClickListener {
            if (mVideoList?.size ?: 0 > 1) {
                if (mSelectDialog == null) {
                    mCurrVideoListPos = mVideoList?.lastIndex ?: 0
                    mSelectDialog = XPopup.Builder(this)
                        .maxHeight(dialogHeight)
                        .asBottomList(
                            "选集",
                            mVideoList?.map { it.name }?.toTypedArray(),
                            null,
                            mCurrVideoListPos //传0会显示选中的✔号
                        ) { position, _ ->
                            mCurrVideoListPos = position
                            playVideo(mVideoList?.get(position))
                        }
                        .bindLayout(R.layout.fragment_search_result)
                    mSelectDialog?.popupInfo
                }
                mSelectDialog?.show()
            }
        }

        //收藏
        ivCollect.setOnClickListener {
            if (ivCollect.isSelected) {
                val delete = CollectDBUtils.delete(id + source.req.key)
                if (delete) {
                    ivCollect.isSelected = false
                    EventBus.getDefault().postSticky(CollectEvent())
                } else toastShort("取消收藏失败")
            } else {
                val collectDBModel = CollectModel()
                collectDBModel.uniqueKey = id + source.req.key
                collectDBModel.videoId = id
                collectDBModel.name = mVideoDetail?.name
                collectDBModel.sourceKey = source.req.key
                collectDBModel.sourceName = source.req.name
                CollectDBUtils.saveAsync(collectDBModel) {
                    if (it) {
                        ivCollect.isSelected = true
                        EventBus.getDefault().postSticky(CollectEvent())
                    } else toastShort("收藏失败")
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
            if (mVideo?.playUrl.isVideoUrl()) {
                ClipboardUtils.copyText(mVideo?.playUrl)
                toastLong("地址已复制，快去下载吧~\n${mVideo?.playUrl}")
            } else {
                toastShort("该资源暂不支持下载哦~")
            }
        }
    }

    override fun initData() {
        super.initData()

        CollectDBUtils.searchAsync(id + source.req.key) {
            if (it != null && it.isSaved) {
                ivCollect.isSelected = true
            }
        }

        source.requestDetailData(id) {
            if (it?.videoList == null) statusView.setLoadState(LoadState.ERROR) else refreshUI(it)
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
        mSelectDialog?.dismiss()
        webController?.onDestroy()
        videoController?.onDestroy()
        super.onDestroy()
    }

    override fun onBackPressed() {
        if (videoController?.onBackPressed() == true || webController?.onBackPressed() == true) {
            super.onBackPressed()
        }
    }

    private fun refreshUI(videoDetail: VideoDetail) {
        this.mVideoDetail = videoDetail
        videoDetail.run {
            mVideoList = this.videoList?.reversed()
            //是否支持选集
            if (mVideoList?.size ?: 0 > 1) {
                ivPlayMore.visible()
                statusView.setLoadState(LoadState.SUCCESS)
            } else {
                ivPlayMore.invisible()
                statusView.setLoadState(LoadState.EMPTY)
            }

            mCurrVideoListPos = mVideoList?.lastIndex ?: 0
            playVideo(mVideoList?.get(mCurrVideoListPos))
            //playVideo(mVideoList?.get(0))

            //名字
            tvName.text = name
            des.noNull().let {
                //剧情简介
                tvIntro.visibleOrGone(it.isNotBlank())
                //简介内容
                tvDesc.text = it
            }
        }
    }

    private fun playVideo(video: Video?) {
        if (video == null) return
        this.mVideo = video
        if (video.playUrl.isVideoUrl()) {
            videoController?.startPlay(video.playUrl, "${mVideoDetail?.name.noNull()}  ${video.name.noNull()}")
            videoPlayer.visible()
            flWebView.gone()
        } else {
            //网页播放
            if (webController == null) {
                webController = WebController()
            }
            webController?.loadUrl(this, video.playUrl, flWebView)
            videoPlayer.gone()
            flWebView.visible()
        }
        //正在播放
        video.name.noNull().let {
            tvCurPlayName.text = it
            ivPlayMore.visibleOrGone(it.isNotBlank())
        }

    }

}