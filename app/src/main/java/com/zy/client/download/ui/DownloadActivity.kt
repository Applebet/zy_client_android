package com.zy.client.download.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.arialyy.annotations.Download
import com.arialyy.aria.core.Aria
import com.arialyy.aria.core.common.AbsEntity
import com.arialyy.aria.core.task.DownloadTask
import com.zy.client.R
import com.zy.client.base.BaseActivity
import com.zy.client.download.DownTaskController
import com.zy.client.download.DownTaskManager
import com.zy.client.download.PeerIndex
import com.zy.client.download.ProgressLayout
import com.zy.client.utils.ext.visible
import com.zy.client.views.TitleView
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


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

    //
    private var isOnlyOne: Boolean = true
    private var isOnlyOneUrl: String? = null
    private var taskController: DownTaskController? = null

    companion object {
        fun openThis(activity: BaseActivity, isOnlyOne: Boolean, isOnlyOneUrl: String?) {
            val intent = Intent(activity, DownloadActivity::class.java)
            intent.putExtra("isOne", isOnlyOne)
            intent.putExtra("isOneUrl", isOnlyOneUrl)
            activity.startActivity(intent)
        }
    }

    override fun getLayoutId(): Int = R.layout.activity_download

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        isOnlyOne = intent.getBooleanExtra("isOne", true)
        isOnlyOneUrl = intent.getStringExtra("isOneUrl")

        mTitleView = findViewById(R.id.titleView)
        mProgressLayout = findViewById(R.id.progress_down)
        mTitleView.setTitle("下载")

        if (!isOnlyOneUrl.isNullOrBlank()) {
            taskController = DownTaskController(isOnlyOneUrl ?: "", mProgressLayout)
            DownTaskManager.setMaxSpeed(0)
        }

        if (isOnlyOne) {
            findViewById<LinearLayout>(R.id.ll_progress_container).visible()
            proceedOnlyOne()
        } else {
            findViewById<FrameLayout>(R.id.fl_container).visible()
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
                taskController?.mTaskId = DownTaskManager.startTask(isOnlyOneUrl, null)
                //DownloadService.mDownTaskComposite
            }

            override fun stop(v: View?, entity: AbsEntity?) {
                Log.d("123", "setBtListener stop")
                DownTaskManager.stopTask(entity?.id)
            }

            override fun resume(v: View?, entity: AbsEntity?) {
                Log.d("123", "setBtListener resume")
                DownTaskManager.resumeTask(entity?.id)
            }

            override fun cancel(v: View?, entity: AbsEntity?) {
                Log.d("123", "setBtListener cancel")
                DownTaskManager.cancelTask(entity?.id, true)
                taskController?.mTaskId = -1
            }
        })
    }

    /**
     * 注册的类中至少有一个被@Download或@Upload或@DownloadGroup注解的方法（原则是：哪个地方有注解就在哪个地方进行注册）
     * https://aria.laoyuyu.me/aria_doc/other/annotaion_invalid.html
     *
     * 如果当前类中没有方法被`@Download`或`@Upload`或`@DownloadGroup`注解，那么就会提示没有Aria的注解方法
     */
    @Download.onTaskComplete
    fun onComplete(task: DownloadTask?) {
        Log.d("123", "DownloadActivity complete")
    }

    override fun onDestroy() {
        taskController?.unRegister()
        super.onDestroy()
    }

}