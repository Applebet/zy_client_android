package com.zy.client.ui.search

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.zy.client.R
import com.zy.client.base.BaseLoadMoreAdapter
import com.zy.client.http.ConfigManager
import com.zy.client.utils.ext.noNull
import com.zy.client.http.NetRepository
import com.zy.client.bean.VideoEntity
import com.zy.client.base.BaseListFragment
import com.zy.client.common.AppRouter

/**
 * 搜索结果页
 */
class SearchResultFragment : BaseListFragment<VideoEntity, BaseViewHolder>() {

    private lateinit var source: NetRepository
    private lateinit var searchWord: String

    companion object {
        fun instance(sourceKey: String, searchWord: String): SearchResultFragment {
            return SearchResultFragment().apply {
                arguments = bundleOf("source_key" to sourceKey, "search_word" to searchWord)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        source = ConfigManager.generateSource(arguments?.getString("source_key").noNull())
        searchWord = arguments?.getString("search_word").noNull()
    }

    override fun getListAdapter(): BaseLoadMoreAdapter<VideoEntity, BaseViewHolder> {
        return SearchResultAdapter().apply {
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
        return LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    }

    override fun loadData(page: Int, callback: (list: List<VideoEntity>?) -> Unit) {
        if (searchWord.isBlank()) {
            callback.invoke(arrayListOf())
        } else {
            source.search(searchWord, page) {
                callback.invoke(it)
            }
        }
    }

    //搜索结果适配器
    inner class SearchResultAdapter :
        BaseLoadMoreAdapter<VideoEntity, BaseViewHolder>(R.layout.item_search_result) {
        override fun convert(holder: BaseViewHolder, item: VideoEntity) {
            holder.setText(R.id.tvName, item.name.noNull("--"))
            holder.setText(R.id.tvType, item.type.noNull("--"))
        }
    }

}