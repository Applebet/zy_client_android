package com.zy.client.download.ui

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.zy.client.R
import com.zy.client.base.BaseListFragment
import com.zy.client.base.BaseLoadMoreAdapter
import com.zy.client.download.DownTaskManager
import com.zy.client.download.ProgressLayout
import com.zy.client.download.db.DownRecordDBUtils
import com.zy.client.download.db.DownRecordModel

/**
 * Title: DownloadFragment
 * <p>
 * Description:
 * </p>
 * @author javakam
 * @date 2020/12/1  15:01
 */
class DownloadFragment private constructor() : BaseListFragment<DownRecordModel, BaseViewHolder>() {

    companion object {
        fun instance(): DownloadFragment {
            return DownloadFragment().apply {
                //arguments = bundleOf("tid" to tid)
            }
        }
    }

    override fun initView() {
        super.initView()
        getListAdapter().loadMoreModule.isEnableLoadMore = false
    }

    override fun getListAdapter(): BaseLoadMoreAdapter<DownRecordModel, BaseViewHolder> {
        return DownloadFragmentAdapter()
    }

    override fun getListLayoutManager(): RecyclerView.LayoutManager {
        return LinearLayoutManager(requireActivity())
    }

    override fun loadData(page: Int, callback: (list: List<DownRecordModel>?) -> Unit) {
        if (page == 1) {
            DownRecordDBUtils.searchAllAsync {
                callback.invoke(it)
            }
        } else {
            callback.invoke(emptyList())
        }
    }

    inner class DownloadFragmentAdapter :
            BaseLoadMoreAdapter<DownRecordModel, BaseViewHolder>(R.layout.item_download) {

        override fun convert(holder: BaseViewHolder, item: DownRecordModel) {
            holder.setText(R.id.tvName, item.name)
            val downEntity = DownTaskManager.getAria().load(item.downTaskId)?.entity
            holder.getView<ProgressLayout>(R.id.progress_down).setInfo(downEntity)

        }
    }

}