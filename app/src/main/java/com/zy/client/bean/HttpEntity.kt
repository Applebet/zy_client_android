package com.zy.client.bean

/**
 * @author javakam
 * @date 2020/6/10 10:47
 */

data class HomeData(val videoList: ArrayList<VideoSource>, val classifyList: ArrayList<Classify>)
data class DownloadData(val name: String, val downloadUrl: String)

//Channel Item
data class VideoSource(
    //视频id
    val id: String? = "",
    //视频类型，国产剧，战争片
    val type: String? = "",
    //分类id
    val tid: String? = "",
    //名字
    val name: String? = "",
    //更新时间
    val updateTime: String? = "",
    //图片
    val pic: String? = "",

    //渠道id  eg: zdzyw , okzyw
    val sourceKey: String?= ""
)

//分类
data class Classify(
    //分类id
    val id: String?,
    //分类名
    val name: String?
)

////////////详情

data class VideoDetail(
    //视频id
    val id: String?,
    //分类id
    val tid: String?,
    //名字
    val name: String?,
    //类型
    val type: String?,
    //语言
    val lang: String?,
    //地区
    val area: String?,
    //图片
    val pic: String?,
    //上映年份
    val year: String?,
    //主演
    val actor: String?,
    //导演
    val director: String?,
    //简介
    val des: String?,
    //播放列表
    val videoList: ArrayList<Video>?,
    //所属视频源的key
    val sourceKey: String?
)

data class Video(
    val name: String?,
    val playUrl: String?
)
