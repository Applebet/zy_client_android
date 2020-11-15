package com.zy.client.database

import com.zy.client.utils.thread.ThreadUtils
import com.zy.client.utils.thread.CustomTask
import org.litepal.LitePal
import java.util.*

/**
 * @author javakam
 *
 * @date 2020/9/12 20:55
 * @desc 收藏的数据库操作工具类
 */
object CollectDBUtils {

    private fun save(collectModel: CollectModel): Boolean {
        if (collectModel.uniqueKey.isNullOrBlank() || collectModel.videoId.isNullOrBlank() || collectModel.sourceKey.isNullOrBlank()) {
            return false
        }
        if (collectModel.isSaved) return true
        //val videoId = collectModel.videoId
        //val c=LitePal.where("videoId = ?", videoId).findFirst(collectModel::class.java)
        return collectModel.save()
    }

    private fun searchAll(): ArrayList<CollectModel>? {
        return LitePal.findAll(CollectModel::class.java) as? ArrayList<CollectModel>
    }

    fun search(uniqueKey: String?): CollectModel? {
        if (uniqueKey.isNullOrBlank()) return null
        return LitePal.where("uniqueKey = ?", uniqueKey).findFirst(CollectModel::class.java)
    }

    fun saveAsync(collectModel: CollectModel, callback: ((Boolean) -> Unit)? = null) {
        ThreadUtils.executeByCached(CustomTask({
            save(collectModel)
        }, {
            callback?.invoke(it ?: false)
        }))
    }

    fun searchAllAsync(callback: ((ArrayList<CollectModel>?) -> Unit)?) {
        ThreadUtils.executeByCached(CustomTask({
            searchAll()
        }, {
            callback?.invoke(it)
        }))
    }

    fun searchAsync(uniqueKey: String?, callback: ((CollectModel?) -> Unit)?) {
        ThreadUtils.executeByCached(CustomTask<CollectModel?>({
            search(uniqueKey)
        }, {
            callback?.invoke(it)
        }))
    }

    fun deleteAll(): Boolean {
        return LitePal.deleteAll(CollectModel::class.java) > 0
    }

    fun delete(uniqueKey: String?): Boolean {
        if (uniqueKey.isNullOrBlank()) return false

        return LitePal.where("uniqueKey = ?", uniqueKey).findFirst(CollectModel::class.java)
            ?.delete() ?: 0 > 0
    }
}
