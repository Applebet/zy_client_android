package com.zy.client.http

import com.zy.client.http.repo.CommonRepository
import com.zy.client.http.repo.CommonRequest
import com.zy.client.database.TvModel
import com.zy.client.utils.SPUtils
import com.zy.client.utils.Utils
import org.json.JSONArray
import java.util.*
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

    val sourceConfigs: LinkedHashMap<String, SourceConfig> by lazy {
        val configJson = Utils.readAssetsData("source.json")
        val configArray = JSONArray(configJson)
        val configMap = LinkedHashMap<String, SourceConfig>()
        for (i in 0 until configArray.length()) {
            val config = configArray.getJSONObject(i)
            val key = config.getString("key")
            val name = config.getString("name")
            val api = config.getString("api")
            val download = config.getString("download")
            if (config != null && !key.isNullOrBlank() && !name.isNullOrBlank() && !api.isNullOrBlank()) {
                configMap[key] = SourceConfig(key, name) {
                    CommonRepository(CommonRequest(key, name, api, download))
                }
            }
        }
        configMap
    }

    val sourceTvConfigs: LinkedList<TvModel> by lazy {
        val configJson = Utils.readAssetsData("iptv.json")
        val configArray = JSONArray(configJson)
        val configList = LinkedList<TvModel>()
        for (i in 0 until configArray.length()) {
            val config = configArray.getJSONObject(i)
            val id = config.getInt("id")
            val name = config.getString("name")
            val url = config.getString("url")
            val group = config.getString("group")
            val isActive = config.getBoolean("isActive")
            if (config != null && !name.isNullOrBlank() && !url.isNullOrBlank()) {
                configList.add(i, TvModel(id = id, name = name, url = url, group = group, isActive = isActive))
            }
        }
        configList
    }

    /**
     * 根据key获取相应的source
     */
    fun generateSource(key: String?): CommonRepository {
        return sourceConfigs[key]?.generateSource() ?: CommonRepository(CommonRequest())
    }

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
        if (sourceConfigs.containsKey(sourceKey)) {
            SPUtils.get().put("curSourceKey", sourceKey)
        }
    }

}