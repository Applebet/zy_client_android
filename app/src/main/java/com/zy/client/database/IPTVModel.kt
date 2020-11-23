package com.zy.client.database

import org.litepal.annotation.Column
import org.litepal.crud.LitePalSupport
import java.io.Serializable

data class IPTVModel(
    @Column(unique = true, nullable = false)
    val id: Int = 0,
    @Column(unique = false, nullable = false)
    val name: String = "",
    val isActive: Boolean = false,
    @Column(unique = false, nullable = false)
    val url: String = "",
    val group: String = ""
) : LitePalSupport(), Serializable