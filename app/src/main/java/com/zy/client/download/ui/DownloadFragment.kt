package com.zy.client.download.ui

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arialyy.annotations.Download
import com.arialyy.aria.core.common.AbsEntity
import com.arialyy.aria.core.task.DownloadTask
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.zy.client.R
import com.zy.client.base.BaseListFragment
import com.zy.client.base.BaseLoadMoreAdapter
import com.zy.client.download.DownTaskManager
import com.zy.client.download.ProgressLayout
import com.zy.client.download.db.DownRecordDBUtils
import com.zy.client.download.db.DownRecordModel
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

/**
 * Title: DownloadFragment
 * <p>
 * Description:
 * </p>
 * @author javakam
 * @date 2020/12/1  15:01
 */
class DownloadFragment private constructor() : BaseListFragment<DownRecordModel, BaseViewHolder>() {

    companion object {
        fun instance(): DownloadFragment {
            return DownloadFragment().apply {
                //arguments = bundleOf("tid" to tid)
            }
        }
    }

    private lateinit var schedule: ScheduledFuture<*>

    @Download.onTaskRunning
    fun onTaskRunning(task: DownloadTask) {
        Log.d("123", "DownloadFragment onTaskRunning $task")
    }

    override fun initView() {
        DownTaskManager.getAria(this).register()
        super.initView()
        mAdapter.loadMoreModule.isEnableLoadMore = false
        schedule = Executors.newScheduledThreadPool(2).schedule({
            if (isAdded && !isDetached) {
                mAdapter.notifyDataSetChanged()
            }
        }, 1500, TimeUnit.MILLISECONDS)
    }

    override fun onDestroyView() {
        if (!schedule.isCancelled || !schedule.isDone) {
            schedule.cancel(true)
        }
        DownTaskManager.getAria(this).unRegister()
        super.onDestroyView()
    }

    override fun getListAdapter(): BaseLoadMoreAdapter<DownRecordModel, BaseViewHolder> {
        return DownloadFragmentAdapter()
    }

    override fun getListLayoutManager(): RecyclerView.LayoutManager {
        return LinearLayoutManager(requireActivity())
    }

    override fun loadData(page: Int, callback: (list: List<DownRecordModel>?) -> Unit) {
        if (page == 1) {
            DownRecordDBUtils.searchAllAsync {
                Log.e("123", "DownloadFragment ${it?.size}")
                callback.invoke(it)
            }
        } else {
            callback.invoke(emptyList())
        }
    }

    inner class DownloadFragmentAdapter :
            BaseLoadMoreAdapter<DownRecordModel, BaseViewHolder>(R.layout.item_download) {

        override fun convert(holder: BaseViewHolder, item: DownRecordModel) {
            holder.setText(R.id.tvName, item.name)
            val downEntity = DownTaskManager.getAria().load(item.downTaskId)?.entity
            Log.e("123", "DownloadFragment $downEntity  ${downEntity?.id}")
            val pl = holder.getView<ProgressLayout>(R.id.progress_down)
            pl.setInfo(downEntity)
            pl.setProgressControlListener(object : ProgressLayout.OnProgressLayoutBtListener {
                override fun create(v: View?, entity: AbsEntity?) {
                    Log.d("123", "DownloadFragment setBtListener create")
                    DownTaskManager.startTask(downEntity?.url, downEntity?.filePath)
                    //DownloadService.mDownTaskComposite
                }

                override fun stop(v: View?, entity: AbsEntity?) {
                    Log.d("123", "DownloadFragment setBtListener stop")
                    DownTaskManager.stopTask(entity?.id)
                }

                override fun resume(v: View?, entity: AbsEntity?) {
                    Log.d("123", "DownloadFragment setBtListener resume")
                    if (item.isDownFailedReasonBandWidth) {
                        DownTaskManager.resumeTask(DownTaskManager.getM3U8Option2(), entity?.id)
                    } else {
                        DownTaskManager.resumeTask(entity?.id)
                    }
                }

                override fun cancel(v: View?, entity: AbsEntity?) {
                    Log.d("123", "DownloadFragment setBtListener cancel")
                    DownTaskManager.cancelTask(entity?.id, true)
                    //taskController?.mTaskId = -1
                    //downEntity?.id=-1
                }
            })
        }
    }

}