package com.zy.client.database

import com.blankj.utilcode.util.ThreadUtils
import com.zy.client.common.Task
import org.litepal.LitePal
import java.util.*

/**
 * @author javakam
 *
 * @date 2020/9/12 20:55
 * @desc 收藏的数据库操作工具类
 */

object CollectDBUtils {
    fun saveAsync(collectModel: CollectModel, callback: ((Boolean) -> Unit)? = null) {

        ThreadUtils.executeByCached(Task<Boolean>({
            save(collectModel)
        }, {
            callback?.invoke(it ?: false)
        }))
    }


    fun save(collectModel: CollectModel): Boolean {
        if (collectModel.uniqueKey.isNullOrBlank() || collectModel.videoId.isNullOrBlank() || collectModel.sourceKey.isNullOrBlank()) {
            return false
        }
        val videoId = collectModel.videoId
        LitePal.where("videoId = ?", videoId).findFirst(collectModel::class.java)?.delete()
        return collectModel.save()
    }

    fun searchAll(): ArrayList<CollectModel>? {
        return LitePal.findAll(CollectModel::class.java) as? ArrayList<CollectModel>
    }

    fun searchAllAsync(callback: ((ArrayList<CollectModel>?) -> Unit)?) {
        ThreadUtils.executeByCached(Task<ArrayList<CollectModel>?>({
            searchAll()
        }, {
            callback?.invoke(it)
        }))
    }

    fun search(uniqueKey: String?): CollectModel? {
        if (uniqueKey.isNullOrBlank()) return null
        return LitePal.where("uniqueKey = ?", uniqueKey).findFirst(CollectModel::class.java)
    }

    fun searchAsync(uniqueKey: String?, callback: ((CollectModel?) -> Unit)?) {

        ThreadUtils.executeByCached(Task<CollectModel?>({
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
