package com.zy.client.ui.home

import android.graphics.Rect
import android.os.Bundle
import android.view.View
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
import com.zy.client.http.repo.CommonRepository
import com.zy.client.base.BaseLazyListFragment
import com.zy.client.bean.VideoSource
import com.zy.client.common.AppRouter
import com.zy.client.common.HOME_LIST_TID_NEW
import com.zy.client.common.HOME_SPAN_COUNT
import com.zy.client.utils.Utils
import com.zy.client.utils.ext.loadImage

/**
 * 频道列表
 *
 * @author javakam
 */
class HomeListFragment : BaseLazyListFragment<VideoSource, BaseViewHolder>() {

    private lateinit var source: CommonRepository
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

    override fun getListAdapter(): BaseLoadMoreAdapter<VideoSource, BaseViewHolder> {
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
        return if (isNew())
            LinearLayoutManager(requireActivity())
        else GridLayoutManager(
            requireActivity(),
            HOME_SPAN_COUNT,
            RecyclerView.VERTICAL,
            false
        )
    }

    override fun loadData(page: Int, callback: (list: List<VideoSource>?) -> Unit) {
        source.requestHomeChannelData(page, tid) {
            callback.invoke(it)
        }
    }

    private fun isNew() = (tid == HOME_LIST_TID_NEW)

    //首页频道的适配器
    inner class HomeChannelAdapter :
        BaseLoadMoreAdapter<VideoSource, BaseViewHolder>(
            if (isNew()) R.layout.item_home_channel
            else R.layout.item_home_channel_grid
        ) {
        override fun convert(holder: BaseViewHolder, item: VideoSource) {
            if (!isNew()) {
                loadImage(holder.getView(R.id.ivPiv), item.pic)
            }
            holder.setText(
                R.id.tvTitle,
                "${item.name.noNull("--")} \n ${item.type}  ${item.updateTime}"
            )
        }
    }

}