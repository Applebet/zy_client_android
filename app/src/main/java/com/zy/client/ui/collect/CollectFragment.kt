package com.zy.client.ui.collect

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.lxj.xpopup.XPopup
import com.zy.client.R
import com.zy.client.base.BaseLoadMoreAdapter
import com.zy.client.utils.ext.noNull
import com.zy.client.utils.ext.visible
import com.zy.client.bean.CollectEvent
import com.zy.client.base.BaseListFragment
import com.zy.client.database.CollectModel
import com.zy.client.database.CollectDBUtils
import com.zy.client.common.AppRouter
import com.zy.client.utils.ext.gone
import com.zy.client.utils.ext.toastShort
import com.zy.client.views.loader.LoadState
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * 收藏
 */
class CollectFragment : BaseListFragment<CollectModel, BaseViewHolder>() {

    override fun initView() {
        super.initView()
        mStatusView.setLoadState(LoadState.UNLOADED)
        mTitleView.visible()
        mTitleView.getLeftView().gone()
        mTitleView.setTitle("收藏")
        mTitleView.getRightImage().setOnClickListener {
            if (mAdapter.data.isNullOrEmpty()) {
                toastShort("没有数据")
                return@setOnClickListener
            }
            unCollectAll()
        }

        mRvList.setBackgroundResource(R.color.color_container_bg)
        mRvList.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                super.getItemOffsets(outRect, view, parent, state)
                outRect.set(0, 1, 0, 1)
            }
        })
    }

    override fun getListAdapter(): BaseLoadMoreAdapter<CollectModel, BaseViewHolder> {
        return CollectAdapter()
    }

    override fun getListLayoutManager(): RecyclerView.LayoutManager {
        return LinearLayoutManager(requireActivity())
    }

    override fun loadData(page: Int, callback: (list: List<CollectModel>?) -> Unit) {
        if (page == 1) {
            CollectDBUtils.searchAllAsync {
                callback.invoke(it)
            }
        } else {
            callback.invoke(arrayListOf())
        }
    }

    inner class CollectAdapter :
        BaseLoadMoreAdapter<CollectModel, BaseViewHolder>(R.layout.item_collect) {

        override fun convert(holder: BaseViewHolder, item: CollectModel) {
            holder.setText(R.id.tvName, item.name)
            holder.setText(R.id.tvSourceName, item.sourceName)
            holder.getView<View>(R.id.content).setOnClickListener {
                AppRouter.toVideoDetailActivity(
                    baseActivity,
                    item.sourceKey.noNull(),
                    item.videoId.noNull()
                )
            }
            holder.getView<View>(R.id.right).setOnClickListener {
                unCollect(item)
            }
        }
    }

    private fun unCollectAll() {
        XPopup.Builder(context)
            .asConfirm("确定删除全部收藏 ?", "") {
                try {
                    val delete = CollectDBUtils.deleteAll()
                    if (delete) {
                        mAdapter.setNewInstance(null)
                        if (mAdapter.data.isEmpty()) {
                            mStatusView.setLoadState(LoadState.EMPTY)
                        }
                    } else toastShort("删除失败")
                } catch (e: Exception) {
                    toastShort("删除失败")
                }
            }.show()
    }

    private fun unCollect(model: CollectModel) {
        try {
            val delete = CollectDBUtils.delete(model.uniqueKey)
            if (delete) {
                mAdapter.remove(model)
                if (mAdapter.data.isEmpty()) {
                    mStatusView.setLoadState(LoadState.EMPTY)
                }
            } else toastShort("删除失败")
        } catch (e: Exception) {
            toastShort("删除失败")
        }
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onMessageEvent(event: CollectEvent) {
        initData()
    }

}