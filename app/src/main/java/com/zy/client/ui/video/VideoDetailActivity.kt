package com.zy.client.ui.video

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.widget.FrameLayout
import com.dueeeke.videoplayer.util.PlayerUtils
import com.lxj.xpopup.XPopup
import com.zy.client.R
import com.zy.client.base.BaseMediaActivity
import com.zy.client.bean.*
import com.zy.client.common.BROWSER_URL
import com.zy.client.common.ID
import com.zy.client.common.SOURCE_KEY
import com.zy.client.common.VIDEO_VIEW_HEIGHT
import com.zy.client.database.CollectDBUtils
import com.zy.client.database.CollectModel
import com.zy.client.database.SourceDBUtils
import com.zy.client.http.ConfigManager
import com.zy.client.http.NetRepository
import com.zy.client.download.ui.DownloadActivity
import com.zy.client.utils.NotchUtils
import com.zy.client.utils.PermissionManager
import com.zy.client.utils.Utils
import com.zy.client.utils.ext.*
import com.zy.client.views.loader.LoadState
import com.zy.client.views.loader.LoaderLayout
import kotlinx.android.synthetic.main.activity_video_detail.*
import org.greenrobot.eventbus.EventBus
import java.util.*

/**
 * 视频详情页
 *
 * @author javakam
 * @date 2020-11-12 15:00:17
 */
class VideoDetailActivity : BaseMediaActivity() {

    private lateinit var id: String
    private lateinit var sourceKey: String
    private lateinit var mRepo: NetRepository

    //
    private lateinit var playerWebContainer: FrameLayout
    private lateinit var videoContainer: FrameLayout
    private lateinit var statusView: LoaderLayout

    //
    private var mVideoDetail: VideoDetail? = null
    private var mVideo: Video? = null
    private var mVideoList: List<Video>? = null
    private var mHistory: VideoHistory? = null

    override fun getLayoutId() = R.layout.activity_video_detail

    override fun initView(savedInstanceState: Bundle?) {
        id = intent?.getStringExtra(ID).noNull()
        sourceKey = intent?.getStringExtra(SOURCE_KEY).noNull()
        mRepo = ConfigManager.generateSource(sourceKey)

        statusView = findViewById(R.id.statusView)
        statusView.setLoadState(LoadState.LOADING)

        //Player Container
        playerWebContainer = findViewById(R.id.playerWebContainer)
        videoContainer = findViewById(R.id.playerContainer)

        //IjkPlayer
        videoController = VideoController()
        videoController?.init(this, false)
        videoController?.callBack = object : VideoController.CallBack {
            override fun onListVideoSelected(video: Video) {
                tvCurPlayName.text = video.name.noNull()
                ivPlayMore.visibleOrGone((video.name?.isNotBlank() == true) && (mVideoDetail?.videoList?.size ?: 0 > 1))
            }
        }

        //小窗情况下 缓存请求数据 Bundle
        if (id.isBlank() || sourceKey.isBlank()) {
            videoController?.getPipCacheData()?.let {
                id = it.id.noNull()
                sourceKey = it.sourceKey.noNull()
                mRepo = ConfigManager.generateSource(sourceKey.noNull())
            }
        } else {
            videoController?.setPipCacheData(VideoEntity(id = id, sourceKey = sourceKey))
        }

        videoContainer.addView(videoController?.getPlayer())
    }

    override fun initListener() {
        super.initListener()
        //网页播放
        ivWebPlay.setOnClickListener {
            if (mVideo == null || mVideo?.playUrl.isNullOrBlank()) {
                ToastUtils.showShort("无法播放")
                return@setOnClickListener
            }

            browser(
                if (mVideo?.playUrl.isVideoUrl()) "${BROWSER_URL}${mVideo?.playUrl?.noNull()}"
                else mVideo?.playUrl.noNull()
            )
        }

        //选集
        //val hasLiuHai = CutoutUtil.allowDisplayToCutout(this)
        val isNotchScreen = NotchUtils.hasNotchScreen(this)
        val dialogHeight =
            (screenHeight - resources.getDimensionPixelSize(VIDEO_VIEW_HEIGHT))
                .minus(if (isNotchScreen) 0 else Utils.getStatusBarHeight())

        llAnthology.setOnClickListener {
            if (mVideoList?.size ?: 0 > 1) {
                mSelectListDialog?.dismiss()
                mSelectListDialog = null

                mSelectListDialog = XPopup.Builder(this)
                    .hasShadowBg(false)
                    .maxHeight(dialogHeight)
                    .asBottomList(
                        "选集",
                        mVideoList?.map { it.name }?.toTypedArray(),
                        null,
                        videoController?.currentListPosition ?: 0 //传0会显示选中的✔号
                    ) { position, _ ->
                        videoController?.currentListPosition = position
                        videoController?.updateVodViewPosition()
                        playVideo(mVideoList?.get(position))
                    }
                    .bindLayout(R.layout.fragment_search_result)
                mSelectListDialog?.popupInfo
                mSelectListDialog?.show()
            }
        }

        //收藏
        ivCollect.setOnClickListener {
            if (ivCollect.isSelected) {
                val delete = CollectDBUtils.delete(id + mRepo.req.key)
                if (delete) {
                    ivCollect.isSelected = false
                    EventBus.getDefault().postSticky(CollectEvent())
                } else toastShort("取消收藏失败")
            } else {
                val collectModel = CollectModel()
                collectModel.uniqueKey = id + mRepo.req.key
                collectModel.videoId = id
                collectModel.name = mVideoDetail?.name
                collectModel.sourceKey = mRepo.req.key
                collectModel.sourceName = mRepo.req.name
                CollectDBUtils.saveAsync(collectModel) {
                    if (it) {
                        ivCollect.isSelected = true
                        EventBus.getDefault().postSticky(CollectEvent())
                    } else toastShort("收藏失败")
                }
            }
        }

        //下载
        ivDownload.setOnClickListener {

            val currUrl = mVideo?.playUrl
            //mRepo.requestDownloadData(id)
            PermissionManager.proceedStoragePermission(this) {
                if (it) {
                    if (currUrl.isVideoUrl()) {

//                        if (!BuildConfig.DEBUG) {
//                            currUrl?.copyToClipBoard()
//                            toastLong("地址已复制，快去下载吧~\n${currUrl}")
//                            return@proceedStoragePermission
//                        }

                        val testUrl = "https://vod3.buycar5.cn/20201118/ttum6IRH/index.m3u8"
                        Log.i("123","currUrl = $currUrl")
//                        if (mVideoList?.size ?: 0 <= 1) {
                            //toastLong("该资源已开始下载~\n${currUrl}")
                            DownloadActivity.openThis(this, true, testUrl)
//                        }

                    } else {
                        toastShort("该资源暂不支持下载哦~")
                    }

                }
            }

        }
    }

    override fun initData() {
        super.initData()
        CollectDBUtils.searchAsync(id + mRepo.req.key) {
            if (it != null && it.isSaved) {
                ivCollect.isSelected = true
            }
        }

        mRepo.getVideoDetail(id) {
            if (it?.videoList == null) statusView.setLoadState(LoadState.ERROR) else refreshUI(it)
        }
    }

    override fun onPause() {
        saveHistory()
        super.onPause()
    }

    override fun onBackPressed() {
        saveHistory()
        super.onBackPressed()
    }

    @SuppressLint("SetTextI18n")
    private fun refreshUI(detail: VideoDetail) {
        this.mVideoDetail = detail
        this.mVideoDetail?.apply {
            sourceKey = this@VideoDetailActivity.sourceKey
            videoController?.searchHistory(sourceKey, tid, id) { h ->
                this@VideoDetailActivity.mHistory = h

                mVideoList = this.videoList?.reversed()
                //状态视图
                if (mVideoList?.isNullOrEmpty() == true) {
                    //td 可先传入默认数据
                    statusView.setLoadState(LoadState.EMPTY)
                } else {
                    statusView.setLoadState(LoadState.SUCCESS)
                }
                //是否支持选集
                if (mVideoList?.size ?: 0 > 1) {
                    ivPlayMore.visible()
                } else {
                    ivPlayMore.invisible()
                }

                val pos = if (mHistory != null) {
                    mHistory?.position ?: 0
                } else {
                    mVideoList?.lastIndex ?: 0
                }
                playVideo(mVideoList?.get(pos))

                videoController?.currentListPosition = pos
                videoController?.setVideoList(mVideoList)

                //名字
                tvName.text = name
                //导演
                tvDirector.text = "导演: $director"
                tvDirector.visibleOrGone(director?.isNotBlank() == true)
                //演员
                tvActor.text = "演员: $actor"
                tvActor.visibleOrGone(actor?.isNotBlank() == true)
                //语言
                tvLanguage.text = "语言: $lang"
                tvLanguage.visibleOrGone(lang?.isNotBlank() == true)
                //影片类型
                tvType.text = "影片类型: $type"
                tvType.visibleOrGone(type?.isNotBlank() == true)
                //上映年份
                val realYear = if (year.noNull() == "0") "" else year
                tvYear.text = "上映年份: $realYear"
                tvYear.visibleOrGone(realYear?.isNotBlank() == true)

                //剧情简介 & 简介内容
                des.noNull().let {
                    tvIntro.visibleOrGone(it.isNotBlank())
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        tvDesc.text = Html.fromHtml(it, Html.FROM_HTML_MODE_LEGACY)
                    } else {
                        tvDesc.text = Html.fromHtml(it)
                    }
                }
            }
        }
    }

    private fun playVideo(video: Video?) {
        if (video == null) return
        this.mVideo = video
        if (mVideo?.playUrl.isVideoUrl()) {
            videoController?.setRecoverActivity(VideoDetailActivity::class.java)
            videoController?.startPlay(
                mVideo?.playUrl,
                "${mVideoDetail?.name.noNull()}  ${mVideo?.name.noNull()}"
            )

            videoContainer.visible()
            playerWebContainer.gone()
        } else {
            //网页播放
            videoContainer.gone()
            playerWebContainer.visible()
            if (webController == null) {
                webController = WebController()
            }
            webController?.loadUrl(this, mVideo?.playUrl, playerWebContainer)
        }
        //正在播放
        mVideo?.name.noNull().let {
            tvCurPlayName.text = it
            ivPlayMore.visibleOrGone(it.isNotBlank() && (mVideoDetail?.videoList?.size ?: 0 > 1))
        }

    }

    private fun saveHistory() {
        if (mVideoDetail == null) return
        videoController?.apply {
            val uniqueId = "${mVideoDetail?.sourceKey}${mVideoDetail?.tid}${mVideoDetail?.id}"
            Log.e("123", "saveHistory uniqueId === $uniqueId")

            val currPosition = getPlayer()?.currentPosition ?: 0L
            val currTimePercent = String.format(
                Locale.getDefault(), getString(R.string.str_player_time_percent),
                PlayerUtils.stringForTime(currPosition.toInt()), PlayerUtils.stringForTime(
                    (getPlayer()?.duration ?: 0).toInt()
                )
            )
            saveHistory(
                VideoHistory(
                    uniqueId = uniqueId,
                    sourceKey = sourceKey,
                    tid = mVideoDetail?.tid,
                    vid = mVideoDetail?.id,
                    sourceName = SourceDBUtils.searchName(key = mVideoDetail?.sourceKey),
                    position = currentListPosition,
                    playUrl = currentUrl,
                    progress = getPlayer()?.currentPosition ?: 0L,
                    timePercent = if (currPosition < 3) "" else currTimePercent,
                    name = mVideoDetail?.name
                )
            )
        }
    }

}