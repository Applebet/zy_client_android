package com.zy.client.download

import android.util.Log
import com.arialyy.annotations.Download
import com.arialyy.annotations.M3U8.*
import com.arialyy.aria.core.Aria
import com.arialyy.aria.core.download.DownloadEntity
import com.arialyy.aria.core.task.DownloadTask
import com.arialyy.aria.util.CommonUtil
import com.zy.client.App
import com.zy.client.R
import com.zy.client.download.DownTaskManager.DOWN_PATH_DEFAULT
import com.zy.client.utils.ext.toastLong
import java.io.File

/**
 * Title: DownTaskController
 * <p>
 * Description:
 * </p>
 * @author javakam
 * @date 2020/12/1  10:19
 */
class DownTaskController(fileName: String, val url: String, private val progressLayout: ProgressLayout? = null) {

    private var mUrl: String? = null
    private var mFilePath: String? = null

    var mCurrEntity: DownloadEntity? = null
    var mTaskId: Long = -1L

    init {
        Aria.download(this).register()

        mCurrEntity = Aria.download(this).getFirstDownloadEntity(url)
        if (mCurrEntity == null) {
            mCurrEntity = DownloadEntity()
            mCurrEntity?.url = url
            val path = if (fileName.isBlank()) DOWN_PATH_DEFAULT else fileName
            mCurrEntity?.filePath = path
            mCurrEntity?.fileName = File(path).name
        }

        getView()?.setInfo(mCurrEntity)
        mTaskId = mCurrEntity?.id ?: 0L
        mUrl = mCurrEntity?.url
        mFilePath = mCurrEntity?.filePath
    }

    private fun getView(): ProgressLayout? = progressLayout

    fun unRegister() {
        Aria.download(this).unRegister()
    }

    @onPeerStart
    fun onPeerStart(m3u8Url: String?, peerPath: String?, peerIndex: Int) {
        //ALog.d(TAG, "peer create, path: " + peerPath + ", index: " + peerIndex);
    }

    @onPeerComplete
    fun onPeerComplete(m3u8Url: String?, peerPath: String?, peerIndex: Int) {
        //ALog.d(TAG, "peer complete, path: " + peerPath + ", index: " + peerIndex);
        //mVideoFragment.addPlayer(peerIndex, peerPath);
    }

    @onPeerFail
    fun onPeerFail(m3u8Url: String?, peerPath: String?, peerIndex: Int) {
        //ALog.d(TAG, "peer fail, path: " + peerPath + ", index: " + peerIndex);
    }

    @Download.onWait
    fun onWait(task: DownloadTask) {
        if (task.key == mUrl) {
            Log.d("123", "wait ==> " + task.downloadEntity.fileName)
        }
    }

    @Download.onPre
    fun onPre(task: DownloadTask) {
        if (task.key == mUrl) {
            Log.d("123", "pre")
            getView()?.setInfo(task.entity)
        }
    }

    @Download.onTaskStart
    fun taskStart(task: DownloadTask) {
        if (task.key == mUrl) {
            Log.d("123", "taskStart isComplete = " + task.isComplete + ", state = " + task.state)
            //getBinding().seekBar.setMax(task.entity.m3U8Entity.peerNum)
            getView()?.setInfo(task.entity)
        }
    }

    @Download.onTaskPre
    fun onTaskPre(task: DownloadTask) {
        if (task.key == mUrl) {
            Log.d("123", "pre ${task.entity}")
            getView()?.setInfo(task.entity)
        }
    }

    @Download.onTaskRunning
    fun onTaskRunning(task: DownloadTask) {
        if (task.key == mUrl) {
            Log.d(
                    "123",
                    "onTaskRunning m3u8 void running, p = " + task.percent + ", speed  = " + task.convertSpeed
            )
            //如果你打开了速度单位转换配置，将可以通过以下方法获取带单位的下载速度，如：1 mb/s   task.convertSpeed
            //如果你有自己的单位格式，可以通过以下方法获取原始byte长度  val speed = task.speed
            getView()?.setInfo(task.entity)
        }
    }

    @Download.onTaskResume
    fun taskResume(task: DownloadTask) {
        if (task.key == mUrl) {
            Log.d("123", "m3u8 vod resume")
            getView()?.setInfo(task.entity)
        }
    }

    @Download.onTaskStop
    fun taskStop(task: DownloadTask) {
        if (task.key == mUrl) {
            Log.d("123", "stop")
            getView()?.setInfo(task.entity)
        }
    }

    @Download.onTaskCancel
    fun taskCancel(task: DownloadTask) {
        if (task.key == mUrl) {
            Log.d("123", "cancel")
            getView()?.setInfo(task.entity)
        }
    }

    @Download.onTaskFail
    fun taskFail(task: DownloadTask?, e: Exception?) {
        if (task != null && task.key == mUrl) {
            App.instance.toastLong(R.string.download_fail)

            Log.d("123", "fail")
            getView()?.setInfo(task.entity)
        }
    }

    @Download.onTaskComplete
    fun taskComplete(task: DownloadTask) {
        if (task.key == mUrl) {
            App.instance.toastLong(R.string.download_success)

            Log.d("123", "md5: " + CommonUtil.getFileMD5(File(task.filePath)))
            getView()?.setInfo(task.entity)
        }
    }

}