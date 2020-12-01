package com.zy.client.download

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.arialyy.annotations.Download
import com.arialyy.annotations.M3U8.*
import com.arialyy.aria.core.Aria
import com.arialyy.aria.core.download.DownloadEntity
import com.arialyy.aria.core.task.DownloadTask
import com.arialyy.aria.util.CommonUtil
import com.zy.client.App
import com.zy.client.R
import java.io.File

/**
 * Title: DownTaskController
 * <p>
 * Description:
 * </p>
 * @author javakam
 * @date 2020/12/1  10:19
 */
class DownTaskController(val url: String, private val progressLayout: ProgressLayout? = null) {

    private var mContext: Context = App.instance
    private var mCurrDownloadEntity: DownloadEntity? = null
    private var mUrl: String? = null
    private var mFilePath: String? = null
    var mTaskId: Long = 0

    init {
        mCurrDownloadEntity = Aria.download(App.instance).getFirstDownloadEntity(url)?.apply {
            getView()?.setInfo(this)
        }
        mTaskId = mCurrDownloadEntity?.id ?: 0L
        mUrl = mCurrDownloadEntity?.url
        mFilePath = mCurrDownloadEntity?.filePath
    }

    private fun getView(): ProgressLayout? = progressLayout

    fun register() {
        Aria.download(this).register()
    }

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
            Log.d("123", "isComplete = " + task.isComplete + ", state = " + task.state)
            //getBinding().seekBar.setMax(task.entity.m3U8Entity.peerNum)
            getView()?.setInfo(task.entity)
        }
    }

    @Download.onTaskRunning
    fun running(task: DownloadTask) {
        if (task.key == mUrl) {
            Log.d(
                "123",
                "m3u8 void running, p = " + task.percent + ", speed  = " + task.convertSpeed
            )
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
            Toast.makeText(
                mContext, mContext.getString(R.string.download_fail),
                Toast.LENGTH_SHORT
            )
                .show()
            Log.d("123", "fail")
            getView()?.setInfo(task.entity)
        }
    }

    @Download.onTaskComplete
    fun taskComplete(task: DownloadTask) {
        if (task.key == mUrl) {
            Toast.makeText(
                mContext, mContext.getString(R.string.download_success),
                Toast.LENGTH_SHORT
            ).show()
            Log.d("123", "md5: " + CommonUtil.getFileMD5(File(task.filePath)))
            getView()?.setInfo(task.entity)
        }
    }


}