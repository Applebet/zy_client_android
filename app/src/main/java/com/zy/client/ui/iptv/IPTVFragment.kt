package com.zy.client.ui.iptv

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.wuhenzhizao.titlebar.widget.CommonTitleBar
import com.zy.client.R
import com.zy.client.base.BaseListFragment
import com.zy.client.common.AppRouter
import com.zy.client.common.BaseLoadMoreAdapter
import com.zy.client.database.TvDBUtils
import com.zy.client.database.TvModel
import com.zy.client.utils.ext.visible

class IPTVFragment : BaseListFragment<TvModel, BaseViewHolder>() {

    override fun initTitleBar(titleBar: CommonTitleBar?) {
        titleBar?.run {
            visible()
            centerTextView.text = "电视"
        }
    }

    override fun getListAdapter(): BaseLoadMoreAdapter<TvModel, BaseViewHolder> {
        return IPTVListAdapter().apply {
            setOnItemClickListener { _, _, position ->
                AppRouter.toTvActivity(baseActivity, data[position])
            }
        }
    }

    override fun getListLayoutManager(): RecyclerView.LayoutManager {
        return LinearLayoutManager(requireActivity())
    }

    override fun loadData(page: Int, callback: (list: ArrayList<TvModel>?) -> Unit) {
        if (page == 1) {
            TvDBUtils.searchAllAsync {
                callback.invoke(it)
            }
        } else {
            callback.invoke(arrayListOf())
        }
    }

    inner class IPTVListAdapter :
        BaseLoadMoreAdapter<TvModel, BaseViewHolder>(R.layout.item_iptv) {

        override fun convert(holder: BaseViewHolder, item: TvModel) {
            holder.setText(R.id.tvName, item.name)
            holder.setText(R.id.tvSourceName, item.group)
        }
    }
}