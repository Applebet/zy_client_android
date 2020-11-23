package com.zy.client.database

import com.zy.client.utils.thread.ThreadUtils
import com.zy.client.utils.thread.CustomTask
import org.litepal.LitePal

object ConfigDBUtils {

    //Source
    //------------------------------------------------


    //IPTV
    //------------------------------------------------

    fun isIPTVExit(): Boolean = LitePal.isExist(IPTVModel::class.java)

    fun saveAllAsync(tvModels: List<IPTVModel>, callback: ((Boolean) -> Unit)? = null) {
        ThreadUtils.executeByCached(CustomTask({
            LitePal.saveAll(tvModels)
        }, {
            callback?.invoke(it ?: false)
        }))
    }

    fun searchGroupAsync(group: String?, callback: ((List<IPTVModel>?) -> Unit)?) {
        if (group.isNullOrBlank()) {
            callback?.invoke(null)
            return
        }
        ThreadUtils.executeByCached(CustomTask<List<IPTVModel>?>({
            LitePal.where("group = ?", group).find(IPTVModel::class.java)
        }, {
            callback?.invoke(it)
        }))
    }

}