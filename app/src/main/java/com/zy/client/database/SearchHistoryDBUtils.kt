package com.zy.client.database

import android.util.Log
import com.zy.client.utils.thread.CustomTask
import com.zy.client.utils.thread.ThreadUtils
import org.litepal.LitePal
import java.util.*

/**
 * 搜索历史的数据库操作
 *
 * @author javakam
 */
object SearchHistoryDBUtils {

    private fun save(searchWord: String?): Boolean {
        if (searchWord.isNullOrBlank()) return false
        val historyModel =
            LitePal.where("searchWord = ?", searchWord).findFirst(SearchHistoryModel::class.java)

        if (historyModel != null && historyModel.isSaved) return true

        val searchHistoryDBModel = SearchHistoryModel()
        searchHistoryDBModel.searchWord = searchWord
        searchHistoryDBModel.updateData = Date()
        return searchHistoryDBModel.save()
    }

    private fun searchAll(): ArrayList<SearchHistoryModel>? {
        val list = LitePal.where("searchWord not null").order("updateData")
            .find(SearchHistoryModel::class.java)
        list?.reverse()
        return list as? ArrayList<SearchHistoryModel>?
    }

    fun saveAsync(searchWord: String, callback: ((Boolean) -> Unit)? = null) {
        ThreadUtils.executeByCpu(CustomTask({
            save(searchWord)
        }, {
            callback?.invoke(it ?: false)
        }))
    }

    fun searchAllAsync(callback: ((ArrayList<SearchHistoryModel>?) -> Unit)?) {
        ThreadUtils.executeByCpu(CustomTask({
            searchAll()
        }, {
            callback?.invoke(it)
        }))
    }

    fun deleteAll(): Boolean {
        return LitePal.deleteAll(SearchHistoryModel::class.java) > 0
    }
}