package com.zy.client.database

import org.litepal.annotation.Column
import org.litepal.crud.LitePalSupport
import java.io.Serializable

data class SourceModel(
    @Column(unique = true, nullable = false)
    val id: Int = 0,

    val sid: Int = 0,     //source.json
    val tid: Int = 0,     //iptv.json
    val key: String = "",
    val name: String = "",
    val api: String = "",
    val download: String = "",
    val status: String = "",
    val isActive: Boolean = false,
    val url: String = "",
    val group: String = ""
) : LitePalSupport(), Serializable