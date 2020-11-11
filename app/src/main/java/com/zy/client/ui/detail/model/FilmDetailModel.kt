package com.zy.client.ui.detail.model

/**
 * @author javakam
 * @date 2020/6/10 10:47
 */

data class FilmDetailModel(
    val site: String,
    val title: String,
    val desc: String,
    val m3u8List: ArrayList<FilmItemInfo>,
    val mp4List: ArrayList<FilmItemInfo>
)

data class FilmItemInfo(
    //真实剧名
    val videoName: String,
    //第x集
    val name: String,
    val videoUrl: String
)