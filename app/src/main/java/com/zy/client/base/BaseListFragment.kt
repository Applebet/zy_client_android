package com.zy.client.base

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import com.zy.client.R
import com.zy.client.views.TitleView
import com.zy.client.views.loader.LoadState
import com.zy.client.views.loader.Loader
import com.zy.client.views.loader.LoaderLayout


/**
 * 加载更多
 */
abstract class BaseLoadMoreAdapter<T, H : BaseViewHolder>(
    layoutResId: Int,
    data: MutableList<T>? = null
) : BaseQuickAdapter<T, H>(layoutResId, data), LoadMoreModule

/**
 * 懒加载
 */
abstract class BaseLazyListFragment<T, H : BaseViewHolder> : BaseListFragment<T, H>(), ILazyLoad

/**
 * 列表类型的页面父类
 */
abstract class BaseListFragment<T, H : BaseViewHolder> : BaseFragment() {

    private lateinit var mRvList: RecyclerView
    protected lateinit var mTitleView: TitleView
    protected lateinit var mStatusView: LoaderLayout
    protected lateinit var mSwipeRefresh: SmartRefreshLayout

    //
    private var mCurrPage: Int = 1
    private val mAdapter: BaseQuickAdapter<T, H> by lazy {
        getListAdapter().apply {
            loadMoreModule.run {
                isAutoLoadMore = true
                setOnLoadMoreListener {
                    if (mSwipeRefresh.isRefreshing) {
                        loadMoreComplete()
                        return@setOnLoadMoreListener
                    }
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
        mTitleView = rootView.findViewById(R.id.titleView)
        mStatusView = rootView.findViewById(R.id.statusView)
        mRvList = rootView.findViewById(R.id.rv_list)

        mSwipeRefresh = rootView.findViewById(R.id.swipe_refresh)
        mSwipeRefresh.setOnRefreshListener(object : OnRefreshListener {
            override fun onRefresh(refreshLayout: RefreshLayout) {
                if (getListAdapter().loadMoreModule.isLoading) {
                    mSwipeRefresh.finishRefresh()
                    return
                }
                initData()
            }
        })

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

        initRecycler(mRvList)
    }

    override fun initData() {
        super.initData()
        mCurrPage = 1
        mAdapter.loadMoreModule.isEnableLoadMore = true
        requestData()
    }

    private fun requestData() {
        loadData(mCurrPage) {
            if (mSwipeRefresh.isRefreshing) mSwipeRefresh.finishRefresh()
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

    override fun refreshData() {
        super.refreshData()
        if (!isVisible || mSwipeRefresh.isRefreshing) {
            return
        }

        val adapter = getListAdapter()
        if (adapter.loadMoreModule.isLoading) {
            adapter.loadMoreModule.loadMoreEnd(true)
        }

        mSwipeRefresh.autoRefresh()
        //Log.w("123", "双击刷新 111 ${mSwipeRefresh.isRefreshing}")
        //toastShort("更新最新数据中...")
    }

    open fun initRecycler(recyclerView: RecyclerView) {}

    abstract fun loadData(page: Int, callback: (list: List<T>?) -> Unit)
}