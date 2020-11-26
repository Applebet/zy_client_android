package com.zy.client.base

import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.zy.client.R
import com.zy.client.views.loader.LoadState
import com.zy.client.views.loader.Loader
import com.zy.client.views.loader.LoaderLayout

/**
 * 懒加载
 */
abstract class BaseLazyListFragment<T, H : BaseViewHolder> : BaseListFragment<T, H>(), ILazyLoad

/**
 * 列表类型的页面父类
 */
abstract class BaseListFragment<T, H : BaseViewHolder> : BaseFragment() {

    private lateinit var mTvTitle: TextView
    private lateinit var mStatusView: LoaderLayout
    private lateinit var mRvList: RecyclerView
    private lateinit var mSwipeRefresh: SwipeRefreshLayout
    private var mCurrPage: Int = 1
    private val mAdapter: BaseQuickAdapter<T, H> by lazy {
        getListAdapter().apply {
            loadMoreModule.run {
                isAutoLoadMore = true
                setOnLoadMoreListener {
                    mCurrPage++
                    requestData()
                }
            }
        }
    }

    override fun getLayoutId(): Int = R.layout.layout_title_list

    abstract fun getListAdapter(): BaseLoadMoreAdapter<T, H>

    abstract fun getListLayoutManager(): RecyclerView.LayoutManager

    override fun initView() {
        super.initView()
        mTvTitle = rootView.findViewById(R.id.tv_title)
        mStatusView = rootView.findViewById(R.id.statusView)
        mRvList = rootView.findViewById(R.id.rv_list)

        mSwipeRefresh = rootView.findViewById(R.id.swipe_refresh)
        mSwipeRefresh.setColorSchemeResources(R.color.color_main_theme)
        mSwipeRefresh.setOnRefreshListener { initData() }

        mRvList.run {
            adapter = mAdapter
            layoutManager = getListLayoutManager().apply {
                if (this is GridLayoutManager) {
                    this.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                        override fun getSpanSize(position: Int): Int {
                            return if (position == mAdapter.loadMoreModule.loadMoreViewPosition) spanCount
                            else 1
                        }
                    }
                }
            }
        }

        mStatusView.setOnReloadListener(object : Loader.OnReloadListener {
            override fun onReload() {
                initData()
            }
        })
    }

    override fun initData() {
        super.initData()
        mCurrPage = 1
        mAdapter.loadMoreModule.isEnableLoadMore = true
        requestData()
    }

    private fun requestData() {
        loadData(mCurrPage) {
            if (mSwipeRefresh.isRefreshing) mSwipeRefresh.isRefreshing = false
            try {
                if (!isAdded) return@loadData
                if (it != null) {
                    if (it.isNotEmpty()) {
                        if (mCurrPage == 1) {
                            mAdapter.setList(null)
                            mStatusView.setLoadState(LoadState.SUCCESS)
                        } else {
                            mAdapter.loadMoreModule.loadMoreComplete()
                        }
                        mAdapter.addData(it)
                    } else {
                        if (mCurrPage == 1) {
                            mStatusView.setLoadState(LoadState.EMPTY)
                        } else {
                            mAdapter.loadMoreModule.isEnableLoadMore = false
                        }
                    }
                } else {
                    if (mCurrPage == 1) {
                        mStatusView.setLoadState(LoadState.ERROR)
                    } else {
                        if (mCurrPage > 1) mCurrPage-- else mCurrPage = 1
                        mAdapter.loadMoreModule.loadMoreFail()
                    }
                }
            } catch (e: Exception) {
            }
        }
    }

    abstract fun loadData(page: Int, callback: (list: List<T>?) -> Unit)
}