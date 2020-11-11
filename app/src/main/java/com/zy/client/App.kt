package com.zy.client

import android.app.Application
import com.lzy.okgo.OkGo
import com.shuyu.gsyvideoplayer.cache.CacheFactory
import com.shuyu.gsyvideoplayer.player.PlayerFactory
import com.zy.client.http.ConfigManager
import org.litepal.LitePal
import tv.danmaku.ijk.media.exo2.Exo2PlayerManager
import tv.danmaku.ijk.media.exo2.ExoPlayerCacheManager

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


        ConfigManager.sourceConfigs  //读取下视频源的配置
        //OkGo初始化
        OkGo.getInstance().init(this)              //必须调用初始化
//      .setOkHttpClient(
//          OkHttpClient.Builder().build()              //建议设置OkHttpClient，不设置将使用默认的
//      )
//      .setCacheMode(CacheMode.NO_CACHE)               //全局统一缓存模式，默认不使用缓存，可以不传
//      .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)   //全局统一缓存时间，默认永不过期，可以不传
//      .setRetryCount(3)                               //全局统一超时重连次数，默认为三次，那么最差的情况会请求4次(一次原始请求，三次重连请求)，不需要可以设置为0
//      .addCommonHeaders(headers)                      //全局公共头
//      .addCommonParams(params);                       //全局公共参数


        //播放器使用exo模式
        PlayerFactory.setPlayManager(Exo2PlayerManager::class.java)
        //exo的缓存
        CacheFactory.setCacheManager(ExoPlayerCacheManager::class.java)
    }
}