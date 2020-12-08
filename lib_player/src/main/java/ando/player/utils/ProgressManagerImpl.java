package ando.player.utils;

import android.text.TextUtils;

import androidx.collection.LruCache;

import com.dueeeke.videoplayer.player.ProgressManager;

/**
 * @author javakam
 */
public class ProgressManagerImpl extends ProgressManager {

    //保存100条记录
    private static final LruCache<Integer, Long> CACHE = new LruCache<>(100);

    @Override
    public void saveProgress(String url, long progress) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        if (progress == 0) {
            clearSavedProgressByUrl(url);
            return;
        }
        CACHE.put(url.hashCode(), progress);
    }

    @Override
    public long getSavedProgress(String url) {
        if (TextUtils.isEmpty(url)) {
            return 0;
        }
        Long pro = CACHE.get(url.hashCode());
        if (pro == null) {
            return 0;
        }
        return pro;
    }

    public void clearAllSavedProgress() {
        CACHE.evictAll();
    }

    public void clearSavedProgressByUrl(String url) {
        CACHE.remove(url.hashCode());
    }

}