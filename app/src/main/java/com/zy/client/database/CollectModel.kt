package com.zy.client.database

import org.litepal.annotation.Column
import org.litepal.crud.LitePalSupport

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