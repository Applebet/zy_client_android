package com.zy.client.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.zy.client.R
import com.zy.client.common.BaseLoadMoreAdapter
import kotlinx.android.synthetic.main.base_list_fragment.view.*

/**
 * @author javakam
 *
 * @date 2020/9/14 0:07
 * @desc 通用列表视图基类
 */
abstract class BaseNormalListView<T, H : BaseViewHolder> @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private var curPage = 1
    private val listAdapter: BaseQuickAdapter<T, H> by lazy {
        getListAdapter().apply {
            loadMoreModule.run {
                isAutoLoadMore = true
                setOnLoadMoreListener {
                    curPage++
                    requestData()
                }
            }
        }
    }

    abstract fun getListAdapter(): BaseLoadMoreAdapter<T, H>

    abstract fun getListLayoutManager(): RecyclerView.LayoutManager

    init {
        View.inflate(context, R.layout.base_normal_list_view, this)

        rvList.run {
            adapter = listAdapter
            layoutManager = getListLayoutManager().apply {
                if (this is GridLayoutManager) {
                    this.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                        override fun getSpanSize(position: Int): Int {
                            if (position == listAdapter.loadMoreModule.loadMoreViewPosition) {
                                return spanCount
                            }
                            return 1
                        }

                    }
                }
            }
        }
        statusView.failRetryClickListener = {
            initData()
        }
    }

    private fun initData() {
        statusView.setLoadingStatus()
        curPage = 1
        listAdapter.loadMoreModule.isEnableLoadMore = true
        requestData()
    }

    private fun requestData() {
        loadData(curPage) {
            try {
                if (!isAttachedToWindow) return@loadData
                if (it != null) {
                    if (it.isNotEmpty()) {
                        if (curPage == 1) {
                            listAdapter.setNewData(null)
                            statusView.setSuccessStatus()
                        } else {
                            listAdapter.loadMoreModule.loadMoreComplete()
                        }
                        listAdapter.addData(it)
                    } else {
                        if (curPage == 1) {
                            statusView.setEmptyStatus()
                        } else {
                            listAdapter.loadMoreModule.isEnableLoadMore = false
                        }
                    }
                } else {
                    if (curPage == 1) {
                        statusView.setFailStatus()
                    } else {
                        if (curPage > 1) curPage-- else curPage = 1
                        listAdapter.loadMoreModule.loadMoreFail()
                    }
                }
            } catch (e: Exception) {
            }
        }
    }

    abstract fun loadData(page: Int, callback: (list: ArrayList<T>?) -> Unit)
}
