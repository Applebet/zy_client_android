package ando.player.service;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.arialyy.annotations.Download;
import com.arialyy.aria.core.Aria;
import com.arialyy.aria.core.common.FtpOption;
import com.arialyy.aria.core.download.DownloadEntity;
import com.arialyy.aria.core.task.DownloadTask;

public class AnyRunnModule {

    private String TAG = "AnyRunnModule";
    private Context mContext;
    private String mUrl;
    private DownloadEntity mEntity;

    public AnyRunnModule(Context context) {
        Aria.download(this).register();
        mContext = context;
    }

    @Download.onWait
    void onWait(DownloadTask task) {
        Log.d(TAG, "wait ==> " + task.getDownloadEntity().getFileName());
    }

    @Download.onPre
    protected void onPre(DownloadTask task) {
        Log.d(TAG, "onPre");
    }

    @Download.onTaskStart
    void taskStart(DownloadTask task) {
        Log.d(TAG, "onPreStart");
    }

    @Download.onTaskRunning
    protected void running(DownloadTask task) {
        Log.d(TAG, "running；Percent = " + task.getPercent());
    }

    @Download.onTaskResume
    void taskResume(DownloadTask task) {
        Log.d(TAG, "resume");
    }

    @Download.onTaskStop
    void taskStop(DownloadTask task) {
        Log.d(TAG, "stop");
    }

    @Download.onTaskCancel
    void taskCancel(DownloadTask task) {
        Log.d(TAG, "cancel");
    }

    @Download.onTaskFail
    void taskFail(DownloadTask task) {
        Log.d(TAG, "fail");
    }

    @Download.onTaskComplete
    void taskComplete(DownloadTask task) {
//    L.d(TAG, "path ==> " + task.getDownloadEntity().getFilePath());
//    L.d(TAG, "md5Code ==> " + CommonUtil.getFileMD5(new File(task.getFilePath())));
    }

    public void start(String url) {
        mUrl = url;
        if (mEntity == null) {
            mEntity = Aria.download(this).getFirstDownloadEntity(url);
        }
        if (!AppUtil.chekEntityValid(mEntity)) {
            String path = Environment.getExternalStorageDirectory().getPath() + "/mmm2.mp4";
            Aria.download(this)
                    .load(url)
                    .setFilePath(path)
                    .resetState()
                    .create();
        } else {
            Aria.download(this).load(mEntity.getId()).resume();
        }
    }

    public void startFtp(String url) {
        mUrl = url;

        if (mEntity == null) {
            mEntity = Aria.download(this).getFirstDownloadEntity(url);
        }
        if (!AppUtil.chekEntityValid(mEntity)) {
            Aria.download(this)
                    .loadFtp(url)
                    .setFilePath(Environment.getExternalStorageDirectory().getPath() + "/Download/")
                    .option(getFtpOption())
                    .create();
        } else {
            Aria.download(this).load(mEntity.getId()).resume();
        }
    }

    @SuppressLint("SdCardPath")
    private FtpOption getFtpOption() {
        FtpOption option = new FtpOption();
        option.setStorePath("/mnt/sdcard/Download/server.crt")
                .setAlias("www.laoyuyu.me")
                .setStorePass("123456");
        return option;
    }

    public void stop(String url) {
        if (AppUtil.chekEntityValid(mEntity)) {
            Aria.download(this).load(mEntity.getId()).stop();
        }
    }

    public void cancel(String url) {
        if (AppUtil.chekEntityValid(mEntity)) {
            Aria.download(this).load(mEntity.getId()).cancel();
        }
    }

    public void unRegister() {
        Aria.download(this).unRegister();
    }

}