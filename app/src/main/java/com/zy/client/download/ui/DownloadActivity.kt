package com.zy.client.download.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
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
import com.zy.client.download.DownTaskManager.DOWN_PATH_DEFAULT
import com.zy.client.download.PeerIndex
import com.zy.client.download.ProgressLayout
import com.zy.client.download.db.DownRecordDBUtils
import com.zy.client.utils.ext.gone
import com.zy.client.utils.ext.noShake
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
    private lateinit var mContainerOne: ViewGroup
    private lateinit var mContainerList: ViewGroup

    //
    private var uniqueId: String? = null
    private var isOnlyOne: Boolean = true
    private var isOnlyOneName: String? = null
    private var isOnlyOneUrl: String? = null
    private var taskController: DownTaskController? = null

    companion object {
        fun openThis(activity: BaseActivity, isOnlyOne: Boolean, isOnlyOneUrl: String?, uniqueId: String, isOnlyOneName: String? = "") {
            val intent = Intent(activity, DownloadActivity::class.java)
            intent.putExtra("uniqueId", uniqueId)
            intent.putExtra("isOne", isOnlyOne)
            intent.putExtra("isOneUrl", isOnlyOneUrl)
            intent.putExtra("isOneName", isOnlyOneName)
            activity.startActivity(intent)
        }
    }

    override fun getLayoutId(): Int = R.layout.activity_download

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        isOnlyOne = intent.getBooleanExtra("isOne", true)
        uniqueId = intent.getStringExtra("uniqueId")
        isOnlyOneUrl = intent.getStringExtra("isOneUrl")
        isOnlyOneName = intent.getStringExtra("isOneName")

        mTitleView = findViewById(R.id.titleView)
        mProgressLayout = findViewById(R.id.progress_down)
        mContainerOne = findViewById(R.id.ll_container)
        mContainerList = findViewById(R.id.fl_container)
        mTitleView.setTitle("下载")

        if (!isOnlyOneUrl.isNullOrBlank()) {
            taskController = DownTaskController(isOnlyOneName ?: DOWN_PATH_DEFAULT, isOnlyOneUrl ?: "", mProgressLayout)
            DownTaskManager.setMaxSpeed(0)
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

    @Download.onTaskPre
    fun onTaskPre(task: DownloadTask) {
        if (task.key == isOnlyOneUrl) {
            Log.e("123", "DownloadActivity onTaskPre $task")
            mProgressLayout.setInfo(task.entity)

            val downEntity = task.entity
            //val downEntity = DownTaskManager.getAria().getFirstDownloadEntity(isOnlyOneUrl)
            DownRecordDBUtils.searchAsync(uniqueId) {
                if (it != null && downEntity != null && (downEntity.id != -1L) && downEntity.key.isNotBlank()) {
                    it.downTaskId = downEntity.id
                    it.downTaskKey = downEntity.key
                    DownRecordDBUtils.saveAsync(it) {
                    }
                }
            }
        }
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