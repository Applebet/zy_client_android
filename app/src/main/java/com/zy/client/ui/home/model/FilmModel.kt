package com.zy.client.ui.home.model

/**
 * @author javakam
 * @date 2020/6/9 0:15
 */
data class FilmModel(
    val filmModelItemList: ArrayList<FilmModelItem>,
    val update: Int,
    val total: Int
)

data class FilmModelItem(
    val site: String,
    val name: String,
    val type: String,
    val time: String,
    val detail: String
)
