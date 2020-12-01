package com.zy.client.download.db

import com.zy.client.utils.thread.CustomTask
import com.zy.client.utils.thread.ThreadUtils
import org.litepal.LitePal

/**
 * Title: DownRecordDBUtils
 * <p>
 * Description:
 * </p>
 * @author javakam
 * @date 2020/12/1  15:36
 */
class DownRecordDBUtils {
    private fun save(record: DownRecordModel?): Boolean {
        if (record == null || record.uniqueId?.isBlank() == true) {
            return false
        }

        //if (record.isSaved) return true
        LitePal.where(" uniqueId = ? ", record.uniqueId).findFirst(DownRecordModel::class.java)
            ?.delete()
        return record.save()
    }

    private fun searchAll(): List<DownRecordModel>? {
        return LitePal.findAll(DownRecordModel::class.java)
    }

    private fun search(uniqueId: String?): DownRecordModel? {
        if (uniqueId.isNullOrBlank()) return null
        return LitePal.where(" uniqueId = ? ", uniqueId)
            .findFirst(DownRecordModel::class.java)
    }

    fun isExit(): Boolean = LitePal.isExist(DownRecordModel::class.java)

    fun count(): Int {
        return LitePal.count(DownRecordModel::class.java)
    }

    fun saveAllAsync(histories: List<DownRecordModel>, callback: ((Boolean) -> Unit)? = null) {
        ThreadUtils.executeByCached(CustomTask({
            LitePal.saveAll(histories)
        }, {
            callback?.invoke(it ?: false)
        }))
    }

    fun saveAsync(record: DownRecordModel, callback: ((Boolean) -> Unit)? = null) {
        ThreadUtils.executeByCached(CustomTask({
            save(record)
        }, {
            callback?.invoke(it ?: false)
        }))
    }

    fun searchAllAsync(callback: ((List<DownRecordModel>?) -> Unit)?) {
        ThreadUtils.executeByCached(CustomTask({
            searchAll()
        }, {
            callback?.invoke(it)
        }))
    }

    fun searchAsync(
        uniqueId: String?,
        callback: ((DownRecordModel?) -> Unit)?
    ) {
        ThreadUtils.executeByCached(CustomTask<DownRecordModel?>({
            search(uniqueId)
        }, {
            callback?.invoke(it)
        }))
    }

    fun deleteAll(): Boolean {
        return LitePal.deleteAll(DownRecordModel::class.java) > 0
    }

    fun delete(uniqueId: String?): Boolean {
        if (uniqueId.isNullOrBlank()) return false
        return LitePal.where(" uniqueId = ? ", uniqueId)
            .findFirst(DownRecordModel::class.java)
            ?.delete() ?: 0 > 0
    }
    
    
}