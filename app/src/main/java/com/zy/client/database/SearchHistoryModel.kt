package com.zy.client.database

import org.litepal.annotation.Column
import org.litepal.crud.LitePalSupport
import java.util.*

/**
 * @desc 搜索历史的数据库类
 */
class SearchHistoryModel : LitePalSupport() {
    @Column(unique = true, defaultValue = "", nullable = false)
    var searchWord: String? = null
    var updateData: Date? = null
}