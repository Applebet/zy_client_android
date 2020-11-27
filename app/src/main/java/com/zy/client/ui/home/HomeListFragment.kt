package com.zy.client.ui.home

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.zy.client.R
import com.zy.client.base.BaseLoadMoreAdapter
import com.zy.client.http.ConfigManager
import com.zy.client.views.GridDividerItemDecoration
import com.zy.client.utils.ext.noNull
import com.zy.client.http.NetRepository
import com.zy.client.base.BaseLazyListFragment
import com.zy.client.bean.VideoEntity
import com.zy.client.common.AppRouter
import com.zy.client.common.HOME_LIST_TID_NEW
import com.zy.client.common.HOME_SPAN_COUNT
import com.zy.client.utils.Utils
import com.zy.client.utils.ext.loadImage
import com.zy.client.utils.ext.visibleOrGone

/**
 * 频道列表
 *
 * @author javakam
 */
class HomeListFragment : BaseLazyListFragment<VideoEntity, BaseViewHolder>() {

    private lateinit var source: NetRepository
    private lateinit var tid: String

    companion object {
        fun instance(tid: String): HomeListFragment {
            return HomeListFragment().apply {
                arguments = bundleOf("tid" to tid)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        source = ConfigManager.curUseSourceConfig()
        tid = arguments?.getString("tid").noNull()
    }


    override fun initView() {
        super.initView()

        if (isNew()) {
            mRvList.addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    super.getItemOffsets(outRect, view, parent, state)
                    outRect.set(10, 10, 10, 10)
                }
            })
        } else {
            mRvList.addItemDecoration(
                GridDividerItemDecoration(
                    Utils.dp2px(12.0f),
                    true
                )
            )
        }
    }

    override fun getListAdapter(): BaseLoadMoreAdapter<VideoEntity, BaseViewHolder> {
        return HomeChannelAdapter().apply {
            setOnItemClickListener { _, _, position ->
                AppRouter.toVideoDetailActivity(
                    baseActivity,
                    source.req.key,
                    data[position].id.noNull()
                )
            }
        }
    }

    override fun getListLayoutManager(): RecyclerView.LayoutManager {
        return if (isNew()) LinearLayoutManager(requireActivity())
        else GridLayoutManager(requireActivity(), HOME_SPAN_COUNT, RecyclerView.VERTICAL, false)
    }

    override fun loadData(page: Int, callback: (list: List<VideoEntity>?) -> Unit) {
        source.getChannelList(page, tid) {
            callback.invoke(it)
        }
    }

    private fun isNew() = (tid == HOME_LIST_TID_NEW)

    //首页频道的适配器
    inner class HomeChannelAdapter :
        BaseLoadMoreAdapter<VideoEntity, BaseViewHolder>(
            if (isNew()) R.layout.item_home_channel
            else R.layout.item_home_channel_grid
        ) {
        override fun convert(holder: BaseViewHolder, item: VideoEntity) {
            holder.setText(R.id.tvTitle, item.name.noNull("--"))
            if (isNew()) {
                holder.setVisible(R.id.iv_hot, Utils.isToday(item.updateTime))
                holder.setText(R.id.tvTime, item.updateTime.noNull())
                holder.setText(R.id.tvProgressName, item.note.noNull("--"))
                holder.setText(R.id.tvTypeName, item.type.noNull("其它影片"))
            } else {
                loadImage(holder.getView(R.id.ivPiv), item.pic)
            }
        }
    }

}