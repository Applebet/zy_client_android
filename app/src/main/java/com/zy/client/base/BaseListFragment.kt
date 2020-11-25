package com.zy.client.base

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.zy.client.R
import com.zy.client.views.loader.LoadState
import com.zy.client.views.loader.Loader
import kotlinx.android.synthetic.main.layout_com_title_list.*

/**
 * 懒加载
 */
abstract class BaseLazyListFragment<T, H : BaseViewHolder> : BaseListFragment<T, H>(), ILazyLoad

/**
 * 列表类型的页面父类
 */
abstract class BaseListFragment<T, H : BaseViewHolder> : BaseFragment() {

    private lateinit var mSwipeRefresh: SwipeRefreshLayout
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

    override fun getLayoutId(): Int = R.layout.layout_com_title_list

    abstract fun getListAdapter(): BaseLoadMoreAdapter<T, H>

    abstract fun getListLayoutManager(): RecyclerView.LayoutManager

    override fun initView() {
        super.initView()
        mSwipeRefresh = rootView.findViewById(R.id.swipe_refresh)
        mSwipeRefresh.setColorSchemeResources(R.color.color_main_theme)
        mSwipeRefresh.setOnRefreshListener {
            initData()
        }

        rvList.run {
            adapter = listAdapter
            layoutManager = getListLayoutManager().apply {
                if (this is GridLayoutManager) {
                    this.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                        override fun getSpanSize(position: Int): Int {
                            return if (position == listAdapter.loadMoreModule.loadMoreViewPosition) spanCount
                            else 1
                        }
                    }
                }
            }
        }

        statusView.setOnReloadListener(object : Loader.OnReloadListener {
            override fun onReload() {
                initData()
            }
        })
    }

    override fun initData() {
        super.initData()
        curPage = 1
        listAdapter.loadMoreModule.isEnableLoadMore = true
        requestData()
    }

    private fun requestData() {
        loadData(curPage) {
            if (mSwipeRefresh.isRefreshing) mSwipeRefresh.isRefreshing = false
            try {
                if (!isAdded) return@loadData
                if (it != null) {
                    if (it.isNotEmpty()) {
                        if (curPage == 1) {
                            listAdapter.setList(null)
                            statusView.setLoadState(LoadState.SUCCESS)
                        } else {
                            listAdapter.loadMoreModule.loadMoreComplete()
                        }
                        listAdapter.addData(it)
                    } else {
                        if (curPage == 1) {
                            statusView.setLoadState(LoadState.EMPTY)
                        } else {
                            listAdapter.loadMoreModule.isEnableLoadMore = false
                        }
                    }
                } else {
                    if (curPage == 1) {
                        statusView.setLoadState(LoadState.ERROR)
                    } else {
                        if (curPage > 1) curPage-- else curPage = 1
                        listAdapter.loadMoreModule.loadMoreFail()
                    }
                }
            } catch (e: Exception) {
            }
        }
    }

    abstract fun loadData(page: Int, callback: (list: List<T>?) -> Unit)
}