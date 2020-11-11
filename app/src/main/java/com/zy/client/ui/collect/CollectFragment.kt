package com.zy.client.ui.collect

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ToastUtils
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.lxj.xpopup.XPopup
import com.zy.client.R
import com.zy.client.common.BaseLoadMoreAdapter
import com.zy.client.utils.ext.textOrDefault
import com.zy.client.utils.ext.visible
import com.zy.client.bean.event.CollectEvent
import com.zy.client.base.BaseListFragment
import com.zy.client.database.CollectModel
import com.zy.client.database.CollectDBUtils
import com.zy.client.ui.detail.DetailActivity
import com.wuhenzhizao.titlebar.widget.CommonTitleBar
import kotlinx.android.synthetic.main.base_list_fragment.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class CollectFragment : BaseListFragment<CollectModel, BaseViewHolder>() {

    override fun initTitleBar(titleBar: CommonTitleBar?) {
        titleBar?.run {
            visible()
            centerTextView.text = "收藏"
        }
    }

    override fun getListAdapter(): BaseLoadMoreAdapter<CollectModel, BaseViewHolder> {
        return CollectAdapter().apply {
            setOnItemClickListener { adapter, view, position ->
                DetailActivity.jump(
                    requireActivity(),
                    data[position].sourceKey.textOrDefault(),
                    data[position].videoId.textOrDefault()
                )
            }
            flHead.addView(View.inflate(requireActivity(), R.layout.collect_head, null).apply {
                //删除全部
                findViewById<View>(R.id.ivDeleteAll).setOnClickListener {
                    XPopup.Builder(context)
                        .asConfirm("", "确认删除全部吗") {
                            try {
                                val delete = CollectDBUtils.deleteAll()
                                if (delete) {
                                    setNewInstance(null)
                                    if (data.isEmpty()) {
                                        statusView.setEmptyStatus()
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
            })
        }
    }

    override fun getListLayoutManager(): RecyclerView.LayoutManager {
        return LinearLayoutManager(requireActivity())
    }

    override fun loadData(page: Int, callback: (list: ArrayList<CollectModel>?) -> Unit) {
        if (page == 1) {
            CollectDBUtils.searchAllAsync {
                callback.invoke(it)
            }
        } else {
            callback.invoke(arrayListOf())
        }
    }

    inner class CollectAdapter :
        BaseLoadMoreAdapter<CollectModel, BaseViewHolder>(
            R.layout.collect_item_layout
        ) {
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
                                    statusView.setEmptyStatus()
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