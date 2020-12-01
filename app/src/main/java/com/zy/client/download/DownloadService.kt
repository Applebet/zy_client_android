package com.zy.client.download

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.arialyy.aria.core.task.DownloadTask
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future

/**
 * Title: DownloadService
 * <p>
 * Description:
 * </p>
 * @author javakam
 * @date 2020/12/1  10:19
 */
class DownloadService : Service() {

    companion object{
        val mDownTaskComposite = HashMap<String, DownloadTask>()
    }

    override fun onBind(intent: Intent?): IBinder {
        Log.w("123", "DownloadService onBind")
        return Binder()
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.w("123", "DownloadService onUnbind")
        return super.onUnbind(intent)
    }

    override fun onCreate() {
        Log.w("123", "DownloadService onCreate")
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.w("123", "DownloadService onStartCommand")
//        BackgroundExecutor.submit {
//
//        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.w("123", "DownloadService onDestroy")
    }

    internal object BackgroundExecutor {
        private val maxSize = 2 * Runtime.getRuntime().availableProcessors()
        var executor: ExecutorService =
            Executors.newScheduledThreadPool(maxSize)

        fun <T> submit(task: () -> T): Future<T> = executor.submit(task)
    }


}