package com.zy.client.http

import com.zy.client.bean.Classify
import com.zy.client.http.repo.CommonRepository
import com.zy.client.http.repo.CommonRequest
import com.zy.client.database.SourceModel
import com.zy.client.utils.SPUtils
import com.zy.client.utils.Utils
import com.zy.client.utils.ext.noNull
import org.json.JSONArray
import kotlin.collections.LinkedHashMap

/**
 * Title: 网络资源
 * <p>
 * Description:
 * </p>
 * @author javakam
 * @date 2020/11/11  15:50
 */

data class SourceConfig(val key: String, val name: String, val generate: () -> CommonRepository) {
    fun generateSource(): CommonRepository {
        return generate.invoke()
    }
}

object ConfigManager {

    private const val DATA_VIDEO = "source.json"
    private const val DATA_IPTV = "iptv.json"

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
        //val sourceModelMap = LinkedHashMap<String, MutableList<SourceModel>>()
        for (i in 0 until configArray.length()) {
            val config = configArray.optJSONObject(i)
            val sid = config.getInt("sid")
            val key = config.getString("key")
            val name = config.getString("name")
            val api = config.getString("api")
            val download = config.getString("download")
            if (config != null && !key.isNullOrBlank() && !name.isNullOrBlank() && !api.isNullOrBlank()) {
                configMap[key] = SourceConfig(key, name) {
                    CommonRepository(CommonRequest(key, name, api, download))
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

    private  val sourceSiteConfigs = LinkedHashMap<String, MutableList<SourceModel>>()

    private  val sourceTvConfigs: LinkedHashMap<String, MutableList<SourceModel>> by lazy {
        val configJsonTv = Utils.readAssetsData(DATA_IPTV)
        val configArrayTv = JSONArray(configJsonTv)
        val configMapTv = LinkedHashMap<String, MutableList<SourceModel>>()
        for (i in 0 until configArrayTv.length()) {
            val config = configArrayTv.optJSONObject(i)
            val tid = config.getInt("tid")
            val name = config.getString("name")
            val url = config.getString("url")
            val group = config.getString("group").noNull("其他")
            val isActive = config.getBoolean("isActive")
            if (config != null && !name.isNullOrBlank() && !url.isNullOrBlank()) {
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
    fun getIPTVGroups(): List<Classify>? {
        var index = 0
        return sourceTvConfigs.keys.filter { it.isNotBlank() }.map {
            Classify((index++).toString(), it)
        }
    }

    /**
     * 根据key获取相应的source
     */
    fun generateSource(key: String?): CommonRepository {
        return sourceConfigs[key]?.generateSource() ?: CommonRepository(CommonRequest())
    }

    //源地址
    //------------------------------------------------

    private const val defaultSourceKey = "okzy"

    /**
     * 获取当前选择的源
     */
    fun curUseSourceConfig(): CommonRepository {
        return generateSource(SPUtils.get().getString("curSourceKey", defaultSourceKey))
    }

    /**
     * 保存当前选择的源
     */
    fun saveCurUseSourceConfig(sourceKey: String?) {
        if (sourceKey?.isNotBlank() == true && sourceConfigs.containsKey(sourceKey)) {
            SPUtils.get().put("curSourceKey", sourceKey)
        }
    }

}