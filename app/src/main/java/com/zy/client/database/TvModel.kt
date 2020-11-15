package com.zy.client.database

import org.litepal.annotation.Column
import org.litepal.crud.LitePalSupport
import java.io.Serializable

data class TvModel(
    val name: String = "",
    @Column(unique = true, nullable = false)
    val id: Int = 0,
    val isActive: Boolean = false,
    val url: String = "",
    val group: String = ""
) : LitePalSupport(), Serializable