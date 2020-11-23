package com.zy.client.ui.iptv

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.zy.client.R
import com.zy.client.base.BaseListFragment
import com.zy.client.common.AppRouter
import com.zy.client.base.BaseLoadMoreAdapter
import com.zy.client.database.ConfigDBUtils
import com.zy.client.database.IPTVModel
import com.zy.client.utils.ext.noNull

class IPTVListFragment : BaseListFragment<IPTVModel, BaseViewHolder>() {

    //分类
    private lateinit var group: String

    companion object {
        fun instance(group: String): IPTVListFragment {
            return IPTVListFragment().apply {
                arguments = bundleOf("group" to group)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        group = arguments?.getString("group").noNull()
    }

    override fun getListAdapter(): BaseLoadMoreAdapter<IPTVModel, BaseViewHolder> {
        return IPTVListAdapter().apply {
            setOnItemClickListener { _, _, position ->
                AppRouter.toTvActivity(baseActivity, data[position])
            }
        }
    }

    override fun getListLayoutManager(): RecyclerView.LayoutManager {
        return LinearLayoutManager(requireActivity())
    }

    override fun loadData(page: Int, callback: (list: List<IPTVModel>?) -> Unit) {
        if (page == 1) {
            ConfigDBUtils.searchGroupAsync(group) {
                callback.invoke(it)
            }
        } else {
            callback.invoke(arrayListOf())
        }
    }

    inner class IPTVListAdapter :
        BaseLoadMoreAdapter<IPTVModel, BaseViewHolder>(R.layout.item_iptv) {

        override fun convert(holder: BaseViewHolder, item: IPTVModel) {
            holder.setText(R.id.tvName, item.name)
            holder.setText(R.id.tvSourceName, item.group)
        }
    }
}