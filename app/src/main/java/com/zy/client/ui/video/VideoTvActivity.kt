package com.zy.client.ui.video

import android.graphics.Rect
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.zy.client.R
import com.zy.client.base.BaseMediaActivity
import com.zy.client.bean.Cctv
import com.zy.client.bean.CctvItem
import com.zy.client.bean.VideoEntity
import com.zy.client.common.TV_BEAN
import com.zy.client.database.SourceModel
import com.zy.client.http.CommonRequest
import com.zy.client.http.ConfigManager
import com.zy.client.http.NetRepository

class VideoTvActivity : BaseMediaActivity() {

    private var mVideoEntity: VideoEntity? = null

    //
    private lateinit var mTvName: TextView
    private lateinit var mTvGroup: TextView
    private lateinit var mVideoContainer: FrameLayout

    //
    private lateinit var mRvMenuList: RecyclerView
    private var liveSt: Long = 0L
    private var adapter: TvMenuAdapter? = null

    private var mTimer = object : CountDownTimer(Long.MAX_VALUE, 5000L) {
        override fun onTick(millisUntilFinished: Long) {
            Log.i("123", "VideoTvActivity onTick")
            adapter?.notifyDataSetChanged()
        }

        override fun onFinish() {
        }
    }

    override fun getLayoutId(): Int = R.layout.activity_video_tv

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        mVideoContainer = findViewById(R.id.playerContainer)
        mTvName = findViewById(R.id.tv_name)
        mTvGroup = findViewById(R.id.tv_group)
        mRvMenuList = findViewById(R.id.rv_tv_detail)
        mRvMenuList.setHasFixedSize(true)
        mRvMenuList.itemAnimator = null
        mRvMenuList.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                super.getItemOffsets(outRect, view, parent, state)
                outRect.set(0, 1, 0, 1)
            }
        })

        //初始化视频控制
        videoController = VideoController()
        videoController?.init(this, true)

        //小窗情况下 缓存请求数据 Bundle
        val sourceModel = intent.getSerializableExtra(TV_BEAN) as? SourceModel
        if (sourceModel != null) {
            mVideoEntity = VideoEntity(
                name = sourceModel.name,
                tvUrl = sourceModel.url,
                group = sourceModel.group
            )
        }
        if (mVideoEntity == null) {
            mVideoEntity = videoController?.getPipCacheData()
        } else {//重置
            videoController?.setPipCacheData(mVideoEntity)
        }

        //详情
        val tvId = ConfigManager.parseTvMenu(mVideoEntity?.tvUrl)
        val mRepo = NetRepository(CommonRequest())
        mRepo.getCCTVMenu(tvId) {
            it?.apply { initTvDetail(this) }
        }

        videoController?.setRecoverActivity(VideoTvActivity::class.java)
        mVideoContainer.addView(videoController?.getPlayer())

        mTimer.start()
    }

    override fun initData() {
        super.initData()
        videoController?.startPlay(mVideoEntity?.tvUrl, mVideoEntity?.name)
        mVideoEntity?.apply {
            mTvName.text = name
            mTvGroup.text = group
        }
    }

    private fun initTvDetail(detail: Cctv) {
        liveSt = detail.liveSt ?: 0L
        adapter = TvMenuAdapter()
        mRvMenuList.adapter = adapter
        detail.list?.let {
            adapter?.addData(newData = it)
        }
    }

    inner class TvMenuAdapter :
        BaseQuickAdapter<CctvItem, BaseViewHolder>(R.layout.item_iptv_menu) {

        override fun convert(holder: BaseViewHolder, item: CctvItem) {
            holder.setText(R.id.tv_tv_time, item.showTime)
            holder.setText(R.id.tv_tv_name, item.title)
            holder.setVisible(R.id.tv_tv_status, liveSt == item.startTime ?: 0L)
        }
    }

    override fun onDestroy() {
        mTimer.cancel()
        mTimer.onFinish()
        super.onDestroy()
    }

}