package com.zy.client.database

import org.litepal.annotation.Column
import org.litepal.crud.LitePalSupport

/**
 * @author javakam
 *
 * @date 2020/9/12 21:56
 * @desc 收藏的数据库类
 */

class CollectModel : LitePalSupport() {

    //id+sourceKey
    @Column(unique = true, nullable = false)
    var uniqueKey:String?=null

    @Column(unique = true, nullable = false)
    var videoId: String? = null

    @Column(nullable = false)
    var sourceKey: String? = null
    var sourceName: String? = null

    var name: String? = null
}