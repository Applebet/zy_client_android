package com.zy.client.ui

import android.graphics.Rect
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.wuhenzhizao.titlebar.widget.CommonTitleBar
import com.zy.client.R
import com.zy.client.base.BaseActivity
import com.zy.client.bean.VideoHistory
import com.zy.client.database.HistoryDBUtils

/**
 * Title:
 *
 * Description:
 *
 * @author javakam
 * @date 2020/11/23  17:11
 */
class HistoryActivity : BaseActivity() {

    private lateinit var titleBar: CommonTitleBar
    private lateinit var rvHistory: RecyclerView

    override fun getLayoutId(): Int {
        return R.layout.activity_history
    }

    override fun initView() {
        super.initView()

        titleBar = findViewById(R.id.title_bar)
        rvHistory = findViewById(R.id.rv_video_history)

        titleBar.run {
            centerTextView.text = "播放记录"
        }

        rvHistory.setHasFixedSize(true)
        rvHistory.itemAnimator = null
        rvHistory.layoutManager = LinearLayoutManager(this)
        val padding: Int = resources.getDimensionPixelSize(R.dimen.dp_1)
        rvHistory.addItemDecoration(object : ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                super.getItemOffsets(outRect, view, parent, state)
                outRect.set(0, padding, 0, padding)
            }
        })
        val adapter = HistoryListAdapter()
        rvHistory.adapter = adapter

        HistoryDBUtils.searchAllAsync {
            it?.let {
                Log.e("123", "历史记录 : ${it.size}")
                adapter.setData(it)
            }
        }
    }

    internal class HistoryListAdapter : RecyclerView.Adapter<HistoryListAdapter.HistoryHolder>() {

        var mData: List<VideoHistory>? = null

        fun setData(data: List<VideoHistory>) {
            this.mData = data
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
                holder.tvHistory.text = toString()
            }
        }

        override fun getItemCount(): Int =
            if (mData?.isNullOrEmpty() == true) 0 else mData?.size ?: 0

        class HistoryHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var tvHistory: TextView = itemView.findViewById(R.id.tv_history)
        }
    }


}