package com.zy.client.ui.detail

import android.content.res.Configuration
import com.zy.client.utils.ext.ToastUtils
import com.zy.client.R
import com.zy.client.utils.ext.noNull
import com.zy.client.http.NetLoader
import com.zy.client.base.BaseFragment
import com.zy.client.ui.detail.controller.VideoController
import com.zy.client.ui.detail.model.FilmDetailModel
import com.zy.client.ui.detail.model.FilmItemInfo
import com.zy.client.ui.detail.view.DetailFilmDownloadDialog
import com.zy.client.utils.Utils
import com.wuhenzhizao.titlebar.widget.CommonTitleBar
import com.zy.client.http.CommonCallback
import kotlinx.android.synthetic.main.fragment_film_detail.*


/**
 * @author javakam
 * @date 2020/6/9 22:49
 */

class FilmDetailFragment : BaseFragment() {
    private val videoController by lazy { VideoController() }

    private val key by lazy { activity?.intent?.getStringExtra(KEY) }
    private val detailUrl by lazy { activity?.intent?.getStringExtra(DETAIL_URL) }

    private var m3u8List: ArrayList<FilmItemInfo>? = null
    private var mp4List: ArrayList<FilmItemInfo>? = null

    companion object {
        const val KEY = "key"
        const val DETAIL_URL = "detail_url"
    }

    override fun getLayoutId(): Int = R.layout.fragment_film_detail

    override fun initTitleBar(titleBar: CommonTitleBar?) {
    }

    override fun initView() {
        super.initView()
        statusView.failRetryClickListener = {
            initData()
        }

        //初始化视频控制
        videoController.init(requireActivity(), videoPlayer)
    }

    override fun initListener() {
        super.initListener()
        //选集
        spinner.setOnSpinnerItemSelectedListener { parent, view, position, id ->
            videoController.play(m3u8List?.get(position))
        }
        //网页播放
        ivWebPlay.setOnClickListener {
            Utils.openBrowser(
                requireActivity(),
                "http://zyplayer.fun/player/player.html?url=${videoController.curFilmItemInfo?.videoUrl.noNull()}&title=${videoController.curFilmItemInfo?.name}"
            )
        }
        //收藏
        ivCollect.setOnClickListener {
            ToastUtils.showShort("暂时不支持收藏")
        }
        //分享
        ivShare.setOnClickListener {
            ToastUtils.showShort("暂时不支持分享")
        }
        //下载
        ivDownload.setOnClickListener {
            DetailFilmDownloadDialog().show(mp4List, childFragmentManager)
        }
    }

    override fun initData() {
        super.initData()

        if (key.isNullOrBlank() || detailUrl.isNullOrBlank()) {
            statusView.setFailStatus()
            return
        }

        statusView.setLoadingStatus()

        NetLoader.filmDetailGet(key!!, detailUrl!!, object : CommonCallback<FilmDetailModel?> {
            override fun onResult(t: FilmDetailModel?) {
                t?.let {
                    setData(t)
                    statusView.setSuccessStatus()
                } ?: let {
                    statusView.setFailStatus()
                }
            }
        })
    }

    private fun setData(t: FilmDetailModel) {
        //视频
        m3u8List = t.m3u8List
        mp4List = t.mp4List

        if (m3u8List.isNullOrEmpty()) {
            return
        }

        videoController.run {
            //进来播放第一个
            play(m3u8List!![0])
        }

        //选集
        if (m3u8List!!.size == 1) {
            spinner.run {
                text = m3u8List!![0].name
                hideArrow()
                isClickable = false
            }
        } else {
            spinner.attachDataSource(m3u8List!!.map { it.name })
        }

        //底下数据
        tvName.text = t.title
        tvDesc.text = t.desc
    }


    override fun onPause() {
        super.onPause()
        videoController.onPause()
    }

    override fun onResume() {
        super.onResume()
        videoController.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        videoController.onDestroy()
    }

    override fun onBackPressed(): Boolean {
        return videoController.onBackPressed()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        videoController.onConfigurationChanged(newConfig)
    }

}