package com.zy.client.download.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.arialyy.annotations.Download
import com.arialyy.aria.core.common.AbsEntity
import com.arialyy.aria.core.download.DownloadEntity
import com.arialyy.aria.core.inf.IEntity.STATE_CANCEL
import com.arialyy.aria.core.inf.IEntity.STATE_FAIL
import com.arialyy.aria.core.task.DownloadTask
import com.arialyy.aria.util.CommonUtil
import com.zy.client.R
import com.zy.client.base.BaseActivity
import com.zy.client.bean.VideoDetail
import com.zy.client.download.DownTaskController
import com.zy.client.download.DownTaskManager
import com.zy.client.download.DownTaskManager.DOWN_PATH_BASE
import com.zy.client.download.DownTaskManager.DOWN_PATH_DEFAULT
import com.zy.client.download.ProgressLayout
import com.zy.client.download.db.DownRecordDBUtils
import com.zy.client.download.db.DownRecordModel
import com.zy.client.download.db.RecordVideoModel
import com.zy.client.utils.ext.*
import com.zy.client.views.TitleView
import java.io.File


/**
 * Title: DownloadActivity
 * <p>
 * Description:
 * </p>
 * @author javakam
 * @date 2020/12/1  14:45
 */
class DownloadActivity : BaseActivity() {

    private lateinit var mTitleView: TitleView
    private lateinit var mProgressLayout: ProgressLayout
    private lateinit var mContainerOne: ViewGroup
    private lateinit var mContainerList: ViewGroup

    //
    private lateinit var mVideoDetail: VideoDetail
    private lateinit var uniqueId: String
    private var isOnlyOne: Boolean = true
    private var isOnlyOneUrl: String? = null
    private var isOnlyOnePath: String? = null

    //
    private var taskController: DownTaskController? = null
    private var isDownFailedReasonBandWidth = false

    companion object {
        fun openThis(activity: BaseActivity, videoDetail: VideoDetail) {
            val intent = Intent(activity, DownloadActivity::class.java)
            intent.putExtra("detail", videoDetail)
            intent.putExtra("uniqueId", "${videoDetail.sourceKey}${videoDetail.tid}${videoDetail.id}")
            intent.putExtra("isOne", videoDetail.videoList?.size == 1)
//            val testUrl = "https://vod3.buycar5.cn/20201118/ttum6IRH/index.m3u8"
//            intent.putExtra("isOneUrl", testUrl)
            intent.putExtra("isOneUrl", videoDetail.videoList?.get(0)?.playUrl.noNull())
            activity.startActivity(intent)
        }
    }

    override fun getLayoutId(): Int = R.layout.activity_download

    override fun initView(savedInstanceState: Bundle?) {
        DownTaskManager.getAria(this).register()
        super.initView(savedInstanceState)
        mVideoDetail = intent.getSerializableExtra("detail") as VideoDetail
        uniqueId = intent.getStringExtra("uniqueId") ?: return
        isOnlyOne = intent.getBooleanExtra("isOne", true)
        isOnlyOneUrl = intent.getStringExtra("isOneUrl")

        mTitleView = findViewById(R.id.titleView)
        mProgressLayout = findViewById(R.id.progress_down)
        mContainerOne = findViewById(R.id.ll_container)
        mContainerList = findViewById(R.id.fl_container)
        mTitleView.setTitle("下载")

        if (!isOnlyOneUrl.isNullOrBlank()) {
            isOnlyOnePath = "$DOWN_PATH_BASE/${mVideoDetail.name}"
            taskController = DownTaskController(isOnlyOnePath ?: DOWN_PATH_DEFAULT, isOnlyOneUrl ?: "", mProgressLayout)
            DownTaskManager.setMaxSpeed(0)
            updateDownEntity(taskController?.mCurrEntity)
        }

        switchOneOrList(isOnlyOne)
        if (isOnlyOne) {
            val tvRight = mTitleView.getRightText()
            tvRight.visible()
            tvRight.noShake {
                switchOneOrList(false)
                tvRight.gone()
            }
        }
    }

    private fun switchOneOrList(isOne: Boolean) {
        if (isOne) {
            mContainerList.gone()
            mContainerOne.visible()
            proceedOnlyOne()
        } else {
            mContainerOne.gone()
            mContainerList.visible()
            supportFragmentManager.beginTransaction()
                    .apply { replace(R.id.fl_container, DownloadFragment.instance()) }
                    .commitAllowingStateLoss()
        }
    }

    private fun proceedOnlyOne() {
        mProgressLayout.setProgressControlListener(object :
                ProgressLayout.OnProgressLayoutBtListener {
            override fun create(v: View?, entity: AbsEntity?) {
                Log.d("123", "setBtListener create")
                taskController?.mTaskId = DownTaskManager.startTask(isOnlyOneUrl, isOnlyOnePath)
                //DownloadService.mDownTaskComposite
            }

            override fun stop(v: View?, entity: AbsEntity?) {
                Log.d("123", "setBtListener stop")
                DownTaskManager.stopTask(entity?.id)
            }

            override fun resume(v: View?, entity: AbsEntity?) {
                Log.d("123", "setBtListener resume")
                if (isDownFailedReasonBandWidth) {
                    DownTaskManager.resumeTask(DownTaskManager.getM3U8Option2(), entity?.id)
                } else {
                    DownTaskManager.resumeTask(entity?.id)
                }
            }

            override fun cancel(v: View?, entity: AbsEntity?) {
                Log.d("123", "setBtListener cancel")
                DownTaskManager.cancelTask(entity?.id, true)
                taskController?.mTaskId = -1
            }
        })
    }

    @Synchronized
    private fun updateDownTask(task: DownloadTask?) {
        if (task == null) return
        val downEntity = task.entity
        mProgressLayout.setInfo(downEntity)

        //下载失败 or 删除任务 直接移除本地记录
        if (task.state == STATE_FAIL || task.state == STATE_CANCEL) {
            //DownTaskManager.cancelTask(downEntity?.id, true)
            DownRecordDBUtils.delete(uniqueId)
            if (!isDownFailedReasonBandWidth) {
                isDownFailedReasonBandWidth = true
                Log.d("123", "情形二 getM3U8Option2 ")
                //DownTaskManager.startTask(DownTaskManager.getM3U8Option2(), isOnlyOneUrl, isOnlyOnePath)
                DownTaskManager.resumeTask(DownTaskManager.getM3U8Option2(), taskId = downEntity.id)
                return
            }
        }

        val isInTask = DownTaskManager.getAria().taskExists(isOnlyOneUrl)
        Log.e("123", "DownloadActivity updateDownTask $isInTask  ")
        //val downEntity = DownTaskManager.getAria().getFirstDownloadEntity(isOnlyOneUrl)
        updateDownEntity(downEntity)

    }

    @Synchronized
    private fun updateDownEntity(downEntity: DownloadEntity?) {
        DownRecordDBUtils.searchAsync(uniqueId) { model ->

            //本地记录和Aria下载记录不同步,两边全删重下
//            if ((isInTask && model == null) || (!isInTask && model != null)) {
//                DownTaskManager.cancelTask(downEntity?.id, true)
//                DownRecordDBUtils.delete(uniqueId)
//            }
//            if (!isInTask && model == null) {

            if (model == null) {
                val videos = mVideoDetail.videoList?.map { v ->
                    RecordVideoModel(name = v.name, playUrl = v.playUrl)
                }

                val record = DownRecordModel(
                        //注: uniqueId = sourceKey + tid + id
                        uniqueId = uniqueId,
                        sourceKey = mVideoDetail.sourceKey,
                        tid = mVideoDetail.tid,
                        vid = mVideoDetail.id,
                        name = mVideoDetail.name,
                        type = mVideoDetail.type,
                        lang = mVideoDetail.lang,
                        area = mVideoDetail.area,
                        pic = mVideoDetail.pic,
                        year = mVideoDetail.year,
                        actor = mVideoDetail.actor,
                        director = mVideoDetail.director,
                        des = mVideoDetail.des,
                        videoList = videos,
                        //
                        isDownFailedReasonBandWidth = isDownFailedReasonBandWidth,
                        downTime = System.currentTimeMillis(),
                        downTaskId = downEntity?.id ?: -1,
                        downTaskKey = downEntity?.key ?: ""
                )
                DownRecordDBUtils.saveAsync(record) {
                    Log.e("123", "DownloadActivity saveAsync 保存 $it")
                }
            } else {
                if (downEntity != null && (downEntity.id != -1L) && downEntity.key.isNotBlank()) {
                    model.isDownFailedReasonBandWidth = isDownFailedReasonBandWidth
                    model.downTaskId = downEntity.id
                    model.downTaskKey = downEntity.key
                    model.downTime = System.currentTimeMillis()
                    //downEntity.isComplete
                    DownRecordDBUtils.saveAsync(model) {
                        Log.e("123", "DownloadActivity saveAsync 更新 $it")
                    }
                }
            }
        }
    }

    @Download.onPre
    fun taskPre(task: DownloadTask) {
        if (task.key == isOnlyOneUrl) {
            Log.e("123", "DownloadActivity taskPre  $task ___ ${task.entity} ___ ${task.entity?.id}")
            updateDownTask(task)
        }
    }

    @Download.onTaskStart
    fun taskStart(task: DownloadTask) {
        if (task.key == isOnlyOneUrl) {
            Log.e("123", "DownloadActivity taskStart isComplete = " + task.isComplete + ", state = " + task.state
                    + " $task ___ ${task.entity} ___ ${task.entity?.id}")
            updateDownTask(task)
        }
    }

    @Download.onTaskStop
    fun taskStop(task: DownloadTask) {
        if (task.key == isOnlyOneUrl) {
            Log.d("123", "DownloadActivity taskStop  $task ___ ${task.entity} ___ ${task.entity?.id}")
            updateDownTask(task)
        }
    }

    @Download.onTaskCancel
    fun taskCancel(task: DownloadTask) {
        if (task.key == isOnlyOneUrl) {
            Log.d("123", "DownloadActivity taskCancel $task ___ ${task.entity} ___ ${task.entity?.id}")
            updateDownTask(task)
        }
    }

    @Download.onTaskFail
    fun taskFail(task: DownloadTask?, e: Exception?) {
        if (task != null && task.key == isOnlyOneUrl) {
            toastLong(R.string.download_fail)
            Log.d("123", "DownloadActivity taskFail  $task ___ ${task.entity} ___ ${task.entity?.id}")
            updateDownTask(task)
        }
    }


    /**
     * 注册的类中至少有一个被@Download或@Upload或@DownloadGroup注解的方法（原则是：哪个地方有注解就在哪个地方进行注册）
     * https://aria.laoyuyu.me/aria_doc/other/annotaion_invalid.html
     *
     * 如果当前类中没有方法被`@Download`或`@Upload`或`@DownloadGroup`注解，那么就会提示没有Aria的注解方法
     */
    @Download.onTaskComplete
    fun taskComplete(task: DownloadTask) {
        if (task.key == isOnlyOneUrl) {
            toastLong(R.string.download_success)
            Log.d("123", "DownloadActivity taskComplete md5: " + CommonUtil.getFileMD5(File(task.filePath))
                    + "  ___ $task ___ ${task.entity} ___ ${task.entity?.id}")
            updateDownTask(task)
        }
    }

    override fun onDestroy() {
        DownTaskManager.getAria(this).unRegister()
        taskController?.unRegister()
        super.onDestroy()
    }

}