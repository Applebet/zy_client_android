package com.zy.client.ui.search

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.zy.client.common.BaseLoadMoreAdapter
import com.zy.client.http.ConfigManager
import com.zy.client.utils.ext.noNull
import com.zy.client.http.sources.BaseSource
import com.zy.client.bean.entity.SearchResultData
import com.zy.client.base.BaseListFragment
import com.zy.client.common.AppRouter

/**
 * @author javakam
 *
 * @date 2020/9/7 21:19
 * @desc 搜索结果页
 */
const val SEARCH_RESULT = "search_result"

class SearchResultFragment : BaseListFragment<SearchResultData, BaseViewHolder>() {

    private lateinit var source: BaseSource
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

    override fun getListAdapter(): BaseLoadMoreAdapter<SearchResultData, BaseViewHolder> {
        return SearchResultAdapter().apply {
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
        return LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    }

    override fun loadData(page: Int, callback: (list: ArrayList<SearchResultData>?) -> Unit) {
        if (searchWord.isBlank()) {
            callback.invoke(arrayListOf())
        } else {
            source.requestSearchData(searchWord, page) {
                callback.invoke(it)
            }
        }
    }

}