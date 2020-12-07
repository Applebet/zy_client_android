package com.zy.client.http

import android.content.Context
import android.content.SharedPreferences
import com.zy.client.App
import com.zy.client.bean.Classify
import com.zy.client.common.SP_NET_SOURCE_KEY
import com.zy.client.database.SourceModel
import com.zy.client.utils.Utils
import com.zy.client.utils.ext.noNull
import org.json.JSONArray

/**
 * Title: 网络资源
 * <p>
 * Description:
 * </p>
 * @author javakam
 * @date 2020/11/11  15:50
 */

data class SourceConfig(val key: String, val name: String, val generate: () -> NetRepository) {
    fun generateSource(): NetRepository {
        return generate.invoke()
    }
}

object ConfigManager {

    private const val DATA_VIDEO = "source.json"
    private const val DATA_IPTV_IVI = "iptv_ivi.json"

    /**
     * 读取视频和TV源配置
     */
    fun getSources(): LinkedHashMap<String, MutableList<SourceModel>> {
        sourceConfigs
        sourceSiteConfigs.putAll(sourceTvConfigs)
        return sourceSiteConfigs
    }

    val sourceConfigs: LinkedHashMap<String, SourceConfig> by lazy {
        val configJson = Utils.readAssetsData(DATA_VIDEO)
        val configArray = JSONArray(configJson)
        val configMap = LinkedHashMap<String, SourceConfig>()
        for (i in 0 until configArray.length()) {
            val config = configArray.optJSONObject(i)
            val sid = config.optInt("sid")
            val key = config.optString("key")
            val name = config.optString("name")
            val api = config.optString("api")
            val download = config.optString("download")
            if (config != null && !key.isNullOrBlank() && !name.isNullOrBlank() && !api.isNullOrBlank()) {
                configMap[key] = SourceConfig(key, name) {
                    NetRepository(CommonRequest(key, name, api, download))
                }

                if (sourceSiteConfigs[key] == null) {
                    sourceSiteConfigs[key] = mutableListOf()
                }
                sourceSiteConfigs[key]?.add(
                    SourceModel(
                        sid = sid,
                        tid = -1,
                        key = key,
                        name = name,
                        api = api,
                        download = download
                    )
                )
            }
        }
        configMap
    }

    private val sourceSiteConfigs = LinkedHashMap<String, MutableList<SourceModel>>()

    private val sourceTvConfigs: LinkedHashMap<String, MutableList<SourceModel>> by lazy {
        val configJsonTv = Utils.readAssetsData(DATA_IPTV_IVI)
        val configArrayTv = JSONArray(configJsonTv)
        val configMapTv = LinkedHashMap<String, MutableList<SourceModel>>()
        for (i in 0 until configArrayTv.length()) {
            val config = configArrayTv.optJSONObject(i)
            val tid = config.optInt("tid")
            val name = config.optString("name")
            val url = config.optString("url")
            val group = config.optString("group").noNull("其他")
            val isActive = config.getBoolean("isActive")
            if (config != null && group.isNotBlank() && !name.isNullOrBlank() && !url.isNullOrBlank()) {
                if (configMapTv[group] == null) {
                    configMapTv[group] = mutableListOf()
                }
                configMapTv[group]?.add(
                    SourceModel(
                        tid = tid,
                        sid = -1,
                        name = name,
                        url = url,
                        group = group,
                        isActive = isActive
                    )
                )
            }
        }
        configMapTv
    }

    //IPTV 所有分类
    fun getIPTVGroups(): List<Classify> {
        var index = 0
        return sourceTvConfigs.keys.filter { it.isNotBlank() }.map {
            Classify((index++).toString(), it)
        }
    }

    //http://ivi.bupt.edu.cn/hls/cctv12hd.m3u8  -> cctv12
    //特殊情形 cctv5phd -> cctv5plus
    fun parseTvMenu(tvUrl: String?): String {
        if (tvUrl.isNullOrBlank()) return ""
        try {
            var url = tvUrl
            url = url.substringBeforeLast(".")
            url = url.substring(url.lastIndexOf("/") + 1)
                .replace("hd", "", true)
            println("tv url = $url")

            if (url.equals("cctv5p", true)) {
                url = "cctv5plus"
            }
            return url
        } catch (e: Exception) {
        }
        return ""
    }

    /**
     * 根据key获取相应的source
     */
    fun generateSource(key: String?): NetRepository {
        return sourceConfigs[key]?.generateSource() ?: NetRepository(CommonRequest())
    }

    //保存上一次选中的源地址
    //------------------------------------------------

    private val sp: SharedPreferences by lazy {
        App.instance.getSharedPreferences(SP_NET_SOURCE_KEY, Context.MODE_PRIVATE)
    }

    private const val defaultSrcKey = "okzy"

    /**
     * 获取当前选择的源
     */
    fun curUseSourceConfig(): NetRepository {
        return generateSource(sp.getString("srcKey", defaultSrcKey))
    }

    /**
     * 保存当前选择的源
     */
    fun saveCurUseSourceConfig(sourceKey: String?) {
        if (sourceKey?.isNotBlank() == true && sourceConfigs.containsKey(sourceKey)) {
            sp.edit().putString("srcKey", sourceKey).apply()
        }
    }

}