package com.zy.client.database

import com.blankj.utilcode.util.ThreadUtils
import com.zy.client.common.Task
import org.litepal.LitePal
import java.util.*

/**
 * @author javakam
 *
 * @date 2020/9/12 20:55
 * @desc 搜索历史的数据库操作
 */
object SearchHistoryDBUtils {
    fun saveAsync(searchWord: String, callback: ((Boolean) -> Unit)? = null) {
        ThreadUtils.executeByCached(Task<Boolean>({
            save(searchWord)
        }, {
            callback?.invoke(it ?: false)
        }))
    }


    fun save(searchWord: String): Boolean {
        if (searchWord.isBlank()) {
            return false
        }
        LitePal.where("searchWord = ?", searchWord).findFirst(SearchHistoryModel::class.java)
            ?.delete()
        val searchHistoryDBModel = SearchHistoryModel()
        searchHistoryDBModel.searchWord = searchWord
        searchHistoryDBModel.updateData = Date()
        return searchHistoryDBModel.save()
    }

    fun searchAll(): ArrayList<SearchHistoryModel>? {
        val list = LitePal.where("searchWord not null").order("updateData")
            .find(SearchHistoryModel::class.java)
        list?.reverse()
        return list as? ArrayList<SearchHistoryModel>?
    }

    fun searchAllAsync(callback: ((ArrayList<SearchHistoryModel>?) -> Unit)?) {
        ThreadUtils.executeByCached(Task<ArrayList<SearchHistoryModel>?>({
            searchAll()
        }, {
            callback?.invoke(it)
        }))

    }

    fun deleteAll(): Boolean {
        return LitePal.deleteAll(SearchHistoryModel::class.java) > 0
    }
}