package com.zy.client

import ando.player.utils.ProgressManagerImpl
import android.app.Application
import com.dueeeke.videoplayer.BuildConfig
import com.dueeeke.videoplayer.ijk.IjkPlayerFactory
import com.dueeeke.videoplayer.player.VideoViewConfig
import com.dueeeke.videoplayer.player.VideoViewManager
import com.lzy.okgo.OkGo
import com.lzy.okgo.cache.CacheEntity
import com.lzy.okgo.cache.CacheMode
import com.zy.client.http.ConfigManager
import org.litepal.LitePal

/**
 * @author javakam
 * @date 2020/6/8 16:42
 */
class App : Application() {

    companion object {
        lateinit var instance: Application
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        LitePal.initialize(this)

        ConfigManager.sourceConfigs      //读取下视频源的配置

        OkGo.getInstance().init(this)
            //建议设置OkHttpClient，不设置将使用默认的
            //.setOkHttpClient( OkHttpClient.Builder().build())
            //全局统一缓存模式，默认不使用缓存，可以不传
            .setCacheMode(CacheMode.FIRST_CACHE_THEN_REQUEST)
            //全局统一缓存时间，默认永不过期，可以不传
            .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)
            //全局统一超时重连次数，默认为三次，那么最差的情况会请求4次(一次原始请求，三次重连请求)，不需要可以设置为0
            .retryCount = 2
        //.addCommonHeaders(headers)     //全局公共头
        //.addCommonParams(params)       //全局公共参数

        //播放器使用 Exo
//        PlayerFactory.setPlayManager(Exo2PlayerManager::class.java)
        //exo的缓存
//        CacheFactory.setCacheManager(ExoPlayerCacheManager::class.java)

        //播放器配置，注意：此为全局配置，按需开启
        //播放器配置，注意：此为全局配置，按需开启
        VideoViewManager.setConfig(
            VideoViewConfig.newBuilder()
                .setLogEnabled(BuildConfig.DEBUG)
                .setPlayerFactory(IjkPlayerFactory.create())
                //.setPlayerFactory(ExoMediaPlayerFactory.create())
                //.setRenderViewFactory(SurfaceRenderViewFactory.create())
                //.setEnableOrientation(true)
                //.setEnableAudioFocus(false)
                //.setScreenScaleType(VideoView.SCREEN_SCALE_MATCH_PARENT)
                //.setAdaptCutout(false)
                //.setPlayOnMobileNetwork(true)
                .setProgressManager(ProgressManagerImpl())
                .build()
        )
    }
}