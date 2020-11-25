package com.zy.client.ui

import android.graphics.Rect
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.guanaj.easyswipemenulibrary.EasySwipeMenuLayout
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.zy.client.R
import com.zy.client.base.BaseActivity
import com.zy.client.bean.VideoHistory
import com.zy.client.common.AppRouter
import com.zy.client.database.HistoryDBUtils
import com.zy.client.utils.ext.copyToClipBoard
import com.zy.client.utils.ext.toastShort
import com.zy.client.views.loader.LoadState
import com.zy.client.views.loader.LoaderLayout


/**
 * Title:
 *
 * Description:
 *
 * @author javakam
 * @date 2020/11/23  17:11
 */
class HistoryActivity : BaseActivity() {

    private lateinit var ivBack: ImageView
    private lateinit var tvTitle: TextView
    private lateinit var ivDelete: ImageView
    private lateinit var statusView: LoaderLayout
    private lateinit var rvHistory: RecyclerView
    private var adapter: HistoryListAdapter? = null
    private var tipDialog: BasePopupView? = null

    override fun getLayoutId(): Int {
        return R.layout.activity_history
    }

    override fun initView() {
        super.initView()
        ivBack = findViewById(R.id.iv_history_back)
        tvTitle = findViewById(R.id.tv_history_title)
        ivDelete = findViewById(R.id.iv_history_delete)
        statusView = findViewById(R.id.statusView)
        rvHistory = findViewById(R.id.rv_video_history)

        tvTitle.text = "播放记录"
        ivBack.setOnClickListener { finish() }
        ivDelete.setOnClickListener {
            if (HistoryDBUtils.count() == 0) {
                toastShort("播放记录为空!")
                return@setOnClickListener
            }
            tipDialog = XPopup.Builder(this)
                .asConfirm("清空全部播放记录?", "") {
                    HistoryDBUtils.deleteAll().apply {
                        toastShort(if (this) "全部记录已清空" else "清除失败!")
                        findHistory()
                    }
                }.show()
        }

        rvHistory.setHasFixedSize(true)
        rvHistory.itemAnimator = null
        rvHistory.layoutManager = LinearLayoutManager(this)
        rvHistory.addItemDecoration(object : ItemDecoration() {
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

        adapter = HistoryListAdapter()
        rvHistory.adapter = adapter
        adapter?.setCallBack {
            findHistory()
        }
    }

    override fun onResume() {
        super.onResume()
        findHistory()
    }

    private fun findHistory() {
        HistoryDBUtils.searchAllAsync {
            //Log.e("123", "历史记录 : ${it?.size}")
            if (it.isNullOrEmpty()) {
                statusView.setLoadState(LoadState.EMPTY)
            } else {
                statusView.setLoadState(LoadState.SUCCESS)
            }
            adapter?.setData(it)
        }
    }

    override fun onDestroy() {
        tipDialog?.dismiss()
        tipDialog = null
        super.onDestroy()
    }

    internal class HistoryListAdapter : RecyclerView.Adapter<HistoryListAdapter.HistoryHolder>() {

        private var mData: List<VideoHistory>? = null
        private var mCallBack: (() -> Unit)? = null

        fun setData(data: List<VideoHistory>?) {
            this.mData = data
            notifyDataSetChanged()
        }

        fun setCallBack(callback: () -> Unit) {
            this.mCallBack = callback
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryHolder {
            return HistoryHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_video_history, parent, false)
            )
        }

        override fun onBindViewHolder(holder: HistoryHolder, position: Int) {
            val entity: VideoHistory? = mData?.get(position)
            entity?.apply {
                holder.tvName.text = name
                holder.tvUrl.text = playUrl
                holder.tvTimePercent.text = timePercent
                holder.tvSource.text = sourceName

                holder.swipeContent.setOnClickListener {
                    Log.e("123", "setOnClickListener...")
                    val activity = (holder.itemView.context as BaseActivity)
                    AppRouter.toVideoDetailActivity(activity, sourceKey ?: "", vid ?: "")
                }
                holder.swipeContent.setOnLongClickListener {
                    if (playUrl?.isNotBlank() == true) {
                        playUrl?.copyToClipBoard()
                        holder.itemView.context.toastShort("视频地址已复制")
                    }
                    true
                }
            }
            holder.swipeMenuDelete.setOnClickListener {
                HistoryDBUtils.delete(entity?.uniqueId).apply {
                    if (this) mCallBack?.invoke()
                }
            }
        }

        override fun getItemCount(): Int =
            if (mData?.isNullOrEmpty() == true) 0 else mData?.size ?: 0

        class HistoryHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var tvName: TextView = itemView.findViewById(R.id.tv_history)
            var tvUrl: TextView = itemView.findViewById(R.id.tv_history_url)
            var tvTimePercent: TextView = itemView.findViewById(R.id.tv_history_time_percent)
            var tvSource: TextView = itemView.findViewById(R.id.tv_history_source)
            var swipeMenu: EasySwipeMenuLayout = itemView.findViewById(R.id.swipe_menu)
            var swipeContent: ConstraintLayout = itemView.findViewById(R.id.content)
            var swipeMenuDelete: TextView = itemView.findViewById(R.id.right)
        }
    }


}