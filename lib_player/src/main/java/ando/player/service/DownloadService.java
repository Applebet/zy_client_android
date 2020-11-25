package ando.player.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.arialyy.annotations.Download;
import com.arialyy.aria.core.Aria;
import com.arialyy.aria.core.task.DownloadTask;

public class DownloadService extends Service {

    private static final String DOWNLOAD_URL = "https://dl.hdslb.com/mobile/latest/iBiliPlayer-bili.apk";
    private DownloadNotification mNotify;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mNotify = new DownloadNotification(getApplicationContext());
        Aria.download(this).register();
        Aria.download(this)
                .load(DOWNLOAD_URL)
                .setFilePath(getApplicationContext().getExternalFilesDir(null).getPath() + "/service_task.apk")
                .create();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Aria.download(this).unRegister();
    }

    @Download.onNoSupportBreakPoint
    public void onNoSupportBreakPoint(DownloadTask task) {
        toastShort("该下载链接不支持断点");
    }

    @Download.onTaskStart
    public void onTaskStart(DownloadTask task) {
        toastShort(task.getDownloadEntity().getFileName() + "，开始下载");
    }

    @Download.onTaskStop
    public void onTaskStop(DownloadTask task) {
        toastShort(task.getDownloadEntity().getFileName() + "，停止下载");
    }

    @Download.onTaskCancel
    public void onTaskCancel(DownloadTask task) {
        toastShort(task.getDownloadEntity().getFileName() + "，取消下载");
    }

    @Download.onTaskFail
    public void onTaskFail(DownloadTask task) {
        toastShort(task.getDownloadEntity().getFileName() + "，下载失败");
    }

    @Download.onTaskComplete
    public void onTaskComplete(DownloadTask task) {
        toastShort(task.getDownloadEntity().getFileName() + "，下载完成");
        mNotify.upload(100);
    }

    @Download.onTaskRunning
    public void onTaskRunning(DownloadTask task) {
        long len = task.getFileSize();
        int p = (int) (task.getCurrentProgress() * 100 / len);
        mNotify.upload(p);
    }

    private void toastShort(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

}