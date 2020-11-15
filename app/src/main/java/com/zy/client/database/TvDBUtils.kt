package com.zy.client.database

import com.zy.client.utils.thread.ThreadUtils
import com.zy.client.utils.thread.CustomTask
import org.litepal.LitePal
import java.util.*

/**
 * @author javakam
 *
 * @date 2020/9/12 20:55
 * @desc IPTV DAO
 */
object TvDBUtils {

    private fun save(tvModel: TvModel): Boolean {
        if (tvModel.name.isBlank() || tvModel.url.isBlank()) {
            return false
        }
        if (tvModel.isSaved) return true
        //val videoId = tvModel.videoId
        //val c=LitePal.where("videoId = ?", videoId).findFirst(tvModel::class.java)
        return tvModel.save()
    }

    private fun searchAll(): List<TvModel>? {
        return LitePal.findAll(TvModel::class.java)
    }

    fun isExit(): Boolean = LitePal.isExist(TvModel::class.java)

    fun search(uniqueKey: String?): TvModel? {
        if (uniqueKey.isNullOrBlank()) return null
        return LitePal.where("uniqueKey = ?", uniqueKey).findFirst(TvModel::class.java)
    }

    fun saveAllAsync(tvModels: List<TvModel>, callback: ((Boolean) -> Unit)? = null) {
        ThreadUtils.executeByCached(CustomTask({
            LitePal.saveAll(tvModels)
        }, {
            callback?.invoke(it ?: false)
        }))
    }

    fun saveAsync(tvModel: TvModel, callback: ((Boolean) -> Unit)? = null) {
        ThreadUtils.executeByCached(CustomTask({
            save(tvModel)
        }, {
            callback?.invoke(it ?: false)
        }))
    }

    fun searchAllAsync(callback: ((List<TvModel>?) -> Unit)?) {
        ThreadUtils.executeByCached(CustomTask({
            searchAll()
        }, {
            callback?.invoke(it)
        }))
    }

    fun searchGroupAsync(group: String?, callback: ((List<TvModel>?) -> Unit)?) {
        if (group.isNullOrBlank()) {
            callback?.invoke(null)
            return
        }
        ThreadUtils.executeByCached(CustomTask<List<TvModel>?>({
            LitePal.where("group = ?", group).find(TvModel::class.java)
        }, {
            callback?.invoke(it)
        }))
    }

    fun searchAsync(uniqueKey: String?, callback: ((TvModel?) -> Unit)?) {
        ThreadUtils.executeByCached(CustomTask<TvModel?>({
            search(uniqueKey)
        }, {
            callback?.invoke(it)
        }))
    }

    fun deleteAll(): Boolean {
        return LitePal.deleteAll(TvModel::class.java) > 0
    }

    fun delete(uniqueKey: String?): Boolean {
        if (uniqueKey.isNullOrBlank()) return false

        return LitePal.where("uniqueKey = ?", uniqueKey).findFirst(TvModel::class.java)
            ?.delete() ?: 0 > 0
    }
}
