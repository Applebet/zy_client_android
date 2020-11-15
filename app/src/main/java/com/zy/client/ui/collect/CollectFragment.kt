package com.zy.client.ui.collect

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.lxj.xpopup.XPopup
import com.zy.client.R
import com.zy.client.common.BaseLoadMoreAdapter
import com.zy.client.utils.ext.noNull
import com.zy.client.utils.ext.visible
import com.zy.client.bean.event.CollectEvent
import com.zy.client.base.BaseListFragment
import com.zy.client.database.CollectModel
import com.zy.client.database.CollectDBUtils
import com.wuhenzhizao.titlebar.widget.CommonTitleBar
import com.zy.client.common.AppRouter
import com.zy.client.utils.ext.ToastUtils
import com.zy.client.utils.ext.toastShort
import com.zy.client.views.loader.LoadState
import kotlinx.android.synthetic.main.layout_com_title_list.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Title: 收藏
 * <p>
 * Description: 收藏
 * </p>
 * @author javakam
 * @date 2020/11/12 14:33
 */
class CollectFragment : BaseListFragment<CollectModel, BaseViewHolder>() {

    override fun initTitleBar(titleBar: CommonTitleBar?) {
        titleBar?.run {
            visible()
            centerTextView.text = "收藏"
        }
    }

    override fun getListAdapter(): BaseLoadMoreAdapter<CollectModel, BaseViewHolder> {
        return CollectAdapter().apply {
            setOnItemClickListener { _, _, position ->
                AppRouter.toVideoDetailActivity(
                    baseActivity,
                    data[position].sourceKey.noNull(),
                    data[position].videoId.noNull()
                )
            }
            val headerView = View.inflate(requireActivity(), R.layout.layout_collect_head, null)
            this.addHeaderView(headerView)
            //删除全部
            headerView.findViewById<View>(R.id.ivDeleteAll).setOnClickListener {
                if (data.isNullOrEmpty()) {
                    toastShort("没有数据")
                    return@setOnClickListener
                }

                XPopup.Builder(context)
                    .asConfirm("", "确认删除全部吗") {
                        try {
                            val delete = CollectDBUtils.deleteAll()
                            if (delete) {
                                setNewInstance(null)
                                if (data.isEmpty()) {
                                    statusView.setLoadState(LoadState.EMPTY)
                                }
                            } else {
                                ToastUtils.showShort("删除失败")
                            }
                        } catch (e: Exception) {
                            ToastUtils.showShort("删除失败")
                        }
                    }.show()
            }

        }
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

            holder.getView<View>(R.id.ivDelete).setOnClickListener {
                XPopup.Builder(context)
                    .asConfirm("", "确认删除吗") {
                        try {
                            val delete = CollectDBUtils.delete(item.uniqueKey)
                            if (delete) {
                                remove(item)
                                if (data.isEmpty()) {
                                    statusView.setLoadState(LoadState.EMPTY)
                                }
                            } else {
                                ToastUtils.showShort("删除失败")
                            }
                        } catch (e: Exception) {
                            ToastUtils.showShort("删除失败")
                        }
                    }
                    .show()
            }
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