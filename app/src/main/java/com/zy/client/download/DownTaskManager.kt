package com.zy.client.download

import android.util.Log
import com.arialyy.aria.core.Aria
import com.arialyy.aria.core.download.DownloadEntity
import com.arialyy.aria.core.download.DownloadReceiver
import com.arialyy.aria.core.download.M3U8Entity
import com.arialyy.aria.core.download.m3u8.M3U8VodOption
import com.arialyy.aria.core.processor.IBandWidthUrlConverter
import com.arialyy.aria.core.processor.IKeyUrlConverter
import com.arialyy.aria.core.processor.ITsMergeHandler
import com.arialyy.aria.core.processor.IVodTsUrlConverter
import com.zy.client.App
import com.zy.client.utils.ext.isVideoUrl
import java.io.File
import java.net.MalformedURLException
import java.net.URL

/**
 * Title: DownTaskManager
 * <p>
 * Description:
 * </p>
 * @author javakam
 * @date 2020/12/1  10:19
 */
object DownTaskManager {
    const val M3U8_URL_KEY = "M3U8_URL_KEY"
    const val M3U8_PATH_KEY = "M3U8_PATH_KEY"

    val DOWN_PATH_BASE= App.instance.getExternalFilesDir(null)
    val DOWN_PATH_DEFAULT = "$DOWN_PATH_BASE/video.ts"

    /**
     * 更新文件保存路径
     *
     * @param newFilePath 文件保存路径
     */
    fun updateFilePath(downloadEntity: DownloadEntity, newFilePath: String?) {
        if (newFilePath.isNullOrBlank()) {
            Log.e("123", "文件保存路径为空")
            return
        }
        val temp = File(newFilePath)
        //AppUtil.setConfigValue(context, M3U8_PATH_KEY, filePath)
        downloadEntity.fileName = temp.name
        downloadEntity.filePath = newFilePath
    }

    /**
     * 更新url
     */
    fun uploadUrl(downloadEntity: DownloadEntity, url: String?) {
        if (url.isNullOrBlank()) {
            Log.e("123", "下载地址为空")
            return
        }
        //AppUtil.setConfigValue(context, M3U8_URL_KEY, url)
        downloadEntity.url = url
    }

    /**
     * https://aria.laoyuyu.me/aria_doc/api/m3u8_params.html
     */
    fun getM3U8Option(): M3U8VodOption {
        val option = M3U8VodOption()
        //option.setBandWidth(200000)
        //option.generateIndexFile()
        option.merge(true)
        //option.setMergeHandler(TsMergeHandler())
        option.setUseDefConvert(false)
        //option.setKeyUrlConverter(new KeyUrlConverter());
        option.setVodTsUrlConvert(VodTsUrlConverter())
        option.setBandWidthUrlConverter(BandWidthUrlConverter())
        return option
    }

    fun getM3U8Option2(): M3U8VodOption {
        val option = M3U8VodOption()
        //option.setBandWidth(200000)
        //option.generateIndexFile()
        option.merge(true)
        //option.setMergeHandler(TsMergeHandler())
        option.setUseDefConvert(false)
        //option.setKeyUrlConverter(new KeyUrlConverter());
        option.setVodTsUrlConvert(VodTsUrlConverter2())
        option.setBandWidthUrlConverter(BandWidthUrlConverter2())
        return option
    }

    /**
     * @param maxSpeed 单位kb  0表示不限速
     */
    fun setMaxSpeed(maxSpeed: Int) {
        Aria.get(App.instance).downloadConfig.maxSpeed = maxSpeed
    }

    fun getAria(): DownloadReceiver = Aria.download(this)

    fun getAria(observer: Any): DownloadReceiver = Aria.download(observer)

    /**
     * 开始
     *
     * @return  taskId : Long
     */
    fun startTask(url: String?, filePath: String?): Long {
        if (url.isNullOrBlank() || !url.isVideoUrl()) return -1
        return getAria()
                .load(url)
                .setFilePath(if (filePath.isNullOrBlank()) DOWN_PATH_DEFAULT else filePath)
                .ignoreFilePathOccupy()
                .m3u8VodOption(getM3U8Option())
                .create()
    }

    fun startTask(option: M3U8VodOption, url: String?, filePath: String?): Long {
        if (url.isNullOrBlank() || !url.isVideoUrl()) return -1
        return getAria()
                .load(url)
                .setFilePath(if (filePath.isNullOrBlank()) DOWN_PATH_DEFAULT else filePath)
                .ignoreFilePathOccupy()
                .m3u8VodOption(option)
                .create()
    }

    /**
     * 暂停
     */
    fun stopTask(taskId: Long?) {
        getAria().load(taskId ?: -1).stop()
    }

    /**
     * 继续
     */
    fun resumeTask(taskId: Long?) {
        getAria()
                .load(taskId ?: -1)
                .m3u8VodOption(getM3U8Option())
                .resume()
    }

    fun resumeTask(option: M3U8VodOption, taskId: Long?) {
        getAria()
                .load(taskId ?: -1)
                .m3u8VodOption(option)
                .resume()
    }

    /**
     * 删除
     *
     * @param removeFile {@code true} 不仅删除任务数据库记录，还会删除已经完成的文件
     * {@code false}如果任务已经完成，只删除任务数据库记录，
     */
    fun cancelTask(taskId: Long?, removeFile: Boolean) {
        getAria().load(taskId ?: -1).cancel(removeFile)
    }

    internal class VodTsUrlConverter : IVodTsUrlConverter {
        override fun convert(m3u8Url: String, tsUrls: List<String>?): List<String>? {
            //val uri = Uri.parse(m3u8Url)
            //String parentUrl = "http://devimages.apple.com/iphone/samples/bipbop/gear1/";
            //String parentUrl = "http://youku.cdn7-okzy.com/20200123/16815_fbe419ed/1000k/hls/";
            //String parentUrl = "http://" + uri.getHost() + "/gear1/";
            //int index = m3u8Url.lastIndexOf("/");
            //String parentUrl = m3u8Url.substring(0, index + 1);
            //String parentUrl = "https://v1.szjal.cn/20190819/Ql6UD1od/";
            //String parentUrl = "http://" + uri.getHost() + "/";

            if (tsUrls?.get(0)?.startsWith("http") == true) return tsUrls
            val index = m3u8Url.lastIndexOf("/")
            val parentUrl = m3u8Url.substring(0, index + 1)
            return tsUrls?.map {
                Log.w("123", "VodTsUrlConverter ${parentUrl + it}")
                parentUrl + it
            }
        }
    }

    internal class VodTsUrlConverter2 : IVodTsUrlConverter {
        override fun convert(m3u8Url: String, tsUrls: List<String>?): List<String>? {
            if (tsUrls?.get(0)?.startsWith("http") == true) return tsUrls
            val parentUrl = m3u8Url.replace(URL(m3u8Url).path, "")
            return tsUrls?.map {
                Log.w("123", "VodTsUrlConverter2 ${parentUrl + it}")
                parentUrl + it
            }
        }
    }

    internal class TsMergeHandler : ITsMergeHandler {
        override fun merge(m3U8Entity: M3U8Entity?, tsPath: List<String>): Boolean {
            Log.d("123", "TsMergeHandler 合并TS....")
            return false
        }
    }

    internal class BandWidthUrlConverter : IBandWidthUrlConverter {
        override fun convert(m3u8Url: String, bandWidthUrl: String): String {
            Log.w("123", "BandWidthUrlConverter .... $m3u8Url")
            /*
            情形一:保留/20201203/8160_e8fdefb1
            原始:http://iqiyi.cdn27-okzy.com/20201203/8160_e8fdefb1/index.m3u8
            /1000k/hls/index.m3u8
            错误:http://iqiyi.cdn27-okzy.com/index.m3u8
            正确:http://iqiyi.cdn27-okzy.com/20201203/8160_e8fdefb1/1000k/hls/index.m3u8

            情形二:移除/20201118/ttum6IRH
            原始:https://vod3.buycar5.cn/20201118/ttum6IRH/index.m3u8
            /20201118/ttum6IRH/1000kb/hls/index.m3u8
            正确:https://vod3.buycar5.cn//20201118/ttum6IRH/1000kb/hls/index.m3u8
             */
            val index = m3u8Url.lastIndexOf("/")
            return m3u8Url.substring(0, index + 1) + bandWidthUrl
        }
    }

    internal class BandWidthUrlConverter2 : IBandWidthUrlConverter {
        override fun convert(m3u8Url: String, bandWidthUrl: String): String {
            Log.w("123", "BandWidthUrlConverter2 .... $m3u8Url")
            return try {
                m3u8Url.replace(URL(m3u8Url).path, "")
            } catch (e: MalformedURLException) {
                e.printStackTrace()
                val index = m3u8Url.lastIndexOf("/")
                return m3u8Url.substring(0, index + 1)
            }.plus("/$bandWidthUrl")
        }
    }

    internal class KeyUrlConverter : IKeyUrlConverter {
        override fun convert(m3u8Url: String, tsListUrl: String, keyUrl: String): String? {
            Log.d("TAG", "convertUrl....")
            return null
        }
    }

}