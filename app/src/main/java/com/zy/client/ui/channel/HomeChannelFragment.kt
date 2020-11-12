package com.zy.client.ui.channel

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.zy.client.common.BaseLoadMoreAdapter
import com.zy.client.http.ConfigManager
import com.zy.client.common.GridSpaceItemDecoration
import com.zy.client.utils.ext.noNull
import com.zy.client.http.sources.BaseSource
import com.zy.client.bean.entity.HomeChannelData
import com.zy.client.base.BaseLazyListFragment
import com.zy.client.common.AppRouter
import com.zy.client.utils.Utils
import kotlinx.android.synthetic.main.layout_com_title_list.*

/**
 * @author javakam
 *
 * @date 2020/9/2 23:31
 * @desc 首页频道页
 */
class HomeChannelFragment : BaseLazyListFragment<HomeChannelData, BaseViewHolder>() {

    private lateinit var source: BaseSource
    private lateinit var tid: String

    companion object {
        fun instance(tid: String): HomeChannelFragment {
            return HomeChannelFragment().apply {
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
            GridSpaceItemDecoration(Utils.dp2px(12.0f), true)
        )
    }

    override fun getListAdapter(): BaseLoadMoreAdapter<HomeChannelData, BaseViewHolder> {
        return HomeChannelAdapter().apply {
            setOnItemClickListener { _, _, position ->
                AppRouter.toDetailActivity(
                    baseActivity,
                    source.key,
                    data[position].id.noNull()
                )
            }
        }
    }

    override fun getListLayoutManager(): RecyclerView.LayoutManager {
        return GridLayoutManager(requireActivity(), 2, RecyclerView.VERTICAL, false)
    }

    override fun loadData(page: Int, callback: (list: ArrayList<HomeChannelData>?) -> Unit) {
        source.requestHomeChannelData(page, tid) {
            callback.invoke(it)
        }
    }

}