package com.zy.client.bean.entity

/**
 * @author javakam
 * @date 2020/6/10 10:47
 */

data class VideoEntity(
    val infoList: ArrayList<VideoInfo>,
    val update: Int,
    val total: Int
)

data class VideoInfo(
    val site: String,
    val name: String,
    val type: String,
    val time: String,
    val detail: String
)

////////////详情

data class VideoDetailEntity(
    val site: String,
    val title: String,
    val desc: String,
    val m3u8List: ArrayList<VideoDetailInfo>,
    val mp4List: ArrayList<VideoDetailInfo>
)

data class VideoDetailInfo(
    val videoName: String,  //真实剧名
    val name: String,       //第x集
    val videoUrl: String
)