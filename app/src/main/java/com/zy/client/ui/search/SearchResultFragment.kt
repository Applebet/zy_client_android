package com.zy.client.ui.search

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.zy.client.R
import com.zy.client.common.BaseLoadMoreAdapter
import com.zy.client.http.ConfigManager
import com.zy.client.utils.ext.noNull
import com.zy.client.http.repo.CommonRepository
import com.zy.client.bean.VideoSource
import com.zy.client.base.BaseListFragment
import com.zy.client.common.AppRouter

/**
 * @author javakam
 *
 * @date 2020/9/7 21:19
 * @desc 搜索结果页
 */
class SearchResultFragment : BaseListFragment<VideoSource, BaseViewHolder>() {

    private lateinit var source: CommonRepository
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

    override fun getListAdapter(): BaseLoadMoreAdapter<VideoSource, BaseViewHolder> {
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

    override fun loadData(page: Int, callback: (list: ArrayList<VideoSource>?) -> Unit) {
        if (searchWord.isBlank()) {
            callback.invoke(arrayListOf())
        } else {
            source.requestSearchData(searchWord, page) {
                callback.invoke(it)
            }
        }
    }

    //搜索结果适配器
    inner class SearchResultAdapter : BaseLoadMoreAdapter<VideoSource, BaseViewHolder>(R.layout.item_search_result) {
        override fun convert(holder: BaseViewHolder, item: VideoSource) {
            holder.setText(R.id.tvName, item.name.noNull("--"))
            holder.setText(R.id.tvType, item.type.noNull("--"))
        }
    }

}