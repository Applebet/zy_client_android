package com.zy.client.ui.search

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.zy.client.common.BaseLoadMoreAdapter
import com.zy.client.http.ConfigManager
import com.zy.client.utils.ext.textOrDefault
import com.zy.client.http.sources.BaseSource
import com.zy.client.bean.entity.SearchResultData
import com.zy.client.base.BaseListFragment
import com.zy.client.ui.detail.DetailActivity

/**
 * @author javakam
 *
 * @date 2020/9/7 21:19
 * @desc 搜索结果页
 */

const val SOURCE_KEY = "source_key"
const val SEARCH_WORD = "search_word"

const val SEARCH_RESULT = "search_result"

class SearchResultFragment : BaseListFragment<SearchResultData, BaseViewHolder>() {

    private lateinit var source: BaseSource
    private lateinit var searchWord: String

    companion object {
        fun instance(sourceKey: String, searchWord: String): SearchResultFragment {
            return SearchResultFragment().apply {
                arguments = bundleOf(SOURCE_KEY to sourceKey, SEARCH_WORD to searchWord)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        source = ConfigManager.generateSource(arguments?.getString(SOURCE_KEY).textOrDefault())
        searchWord = arguments?.getString(SEARCH_WORD).textOrDefault()
    }

    override fun getListAdapter(): BaseLoadMoreAdapter<SearchResultData, BaseViewHolder> {
        return SearchResultAdapter().apply {
            setOnItemClickListener { adapter, view, position ->
                DetailActivity.jump(
                    requireActivity(),
                    source.key,
                    data[position].id.textOrDefault()
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