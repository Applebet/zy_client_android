package com.zy.client.database

import com.zy.client.utils.thread.ThreadUtils
import com.zy.client.utils.thread.CustomTask
import org.litepal.LitePal

object SourceDBUtils {

    fun isIPTVExit(): Boolean = LitePal.isExist(SourceModel::class.java)

    fun saveAllAsync(sourceModels: List<SourceModel>, callback: ((Boolean) -> Unit)? = null) {
        ThreadUtils.executeByCached(CustomTask({
            LitePal.saveAll(sourceModels)
        }, {
            callback?.invoke(it ?: false)
        }))
    }

    fun searchAllSites(): List<SourceModel>? {
        return LitePal.where(" tid == ? ", "-1").find(SourceModel::class.java)
    }

    fun searchAllTv(): List<SourceModel>? {
        return LitePal.where(" sid == ? ", "-1").find(SourceModel::class.java)
    }

    fun searchName(key: String?): String? {
        if (key.isNullOrBlank()) {
            return null
        }
        return LitePal.where(" key = ? ", key).findFirst(SourceModel::class.java)?.name
    }

    fun searchGroupAsync(group: String?, callback: ((List<SourceModel>?) -> Unit)?) {
        if (group.isNullOrBlank()) {
            callback?.invoke(null)
            return
        }
        ThreadUtils.executeByCached(CustomTask<List<SourceModel>?>({
            LitePal.where(" group = ? ", group).find(SourceModel::class.java)
        }, {
            callback?.invoke(it)
        }))
    }

}