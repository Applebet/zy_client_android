package com.zy.client.ui.home

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.GridLayoutManager
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
import com.zy.client.common.HOME_SPAN_COUNT
import com.zy.client.utils.Utils
import com.zy.client.utils.ext.loadImage
import kotlinx.android.synthetic.main.layout_com_title_list.*

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
        rvList.addItemDecoration(
            GridDividerItemDecoration(
                Utils.dp2px(12.0f),
                true
            )
        )
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
        return GridLayoutManager(requireActivity(), HOME_SPAN_COUNT, RecyclerView.VERTICAL, false)
    }

    override fun loadData(page: Int, callback: (list: List<VideoSource>?) -> Unit) {
        source.requestHomeChannelData(page, tid) {
            callback.invoke(it)
        }
    }

    //首页频道的适配器
    inner class HomeChannelAdapter : BaseLoadMoreAdapter<VideoSource, BaseViewHolder>(R.layout.item_home_channel) {
        override fun convert(holder: BaseViewHolder, item: VideoSource) {
            holder.setText(R.id.tvTitle, item.name.noNull("--"))
            loadImage(holder.getView(R.id.ivPiv), item.pic)
        }
    }

}