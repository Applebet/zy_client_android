package com.zy.client.bean

import org.litepal.annotation.Column
import org.litepal.crud.LitePalSupport
import java.io.Serializable

/**
 * @author javakam
 */
data class HomeData(val videoList: ArrayList<VideoEntity>, val classifyList: ArrayList<Classify>)

//Channel Item
data class VideoEntity(
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
    //更新至08集
    val note: String? = "",
    //Tv Url
    val tvUrl: String? = "",
    //Tv group
    val group: String? = "",

    //渠道id  eg: zdzyw , okzyw
    val sourceKey: String? = ""
) : Serializable

//分类
data class Classify(
    //分类id
    val id: String?,
    //分类名
    val name: String?
)

//详情
data class VideoDetail(
    //视频id
    var id: String? = "",
    //分类id
    var tid: String? = "",
    //名字
    var name: String? = "",
    //类型
    var type: String? = "",
    //语言
    var lang: String? = "",
    //地区
    var area: String? = "",
    //图片
    var pic: String? = "",
    //上映年份
    var year: String? = "",
    //主演
    var actor: String? = "",
    //导演
    var director: String? = "",
    //简介
    var des: String? = "",
    //播放列表
    var videoList: List<Video>? = emptyList(),

    //所属视频源的key
    var sourceKey: String?
) : Serializable

data class Video(
    val name: String?,
    val playUrl: String?,
) : Serializable

data class VideoHistory(
    //避免和 id 字段冲突
    @Column(unique = true)
    var _id: Long = 0L,

    //注: uniqueId = sourceKey + tid + id
    var uniqueId: String? = "",
    var vid: String? = "",
    var tid: String? = "",       //分类id
    var sourceKey: String? = "", //所属视频源的key
    var sourceName: String? = "", //所属视频源的 name

    var position: Int = 0,  //第n集
    var progress: Long = 0, //进度
    var timePercent: String? = "", // eg: 12:32/1:22:32

    var name: String? = "",
    var playUrl: String? = ""
) : LitePalSupport() {
    override fun toString(): String {
        return "$name \n $sourceKey \n $playUrl"
    }
}

///////////////////////////////////////////////

/**
 * 电视节目单
 *
 * https://api.cntv.cn/epg/getEpgInfoByChannelNew?c=cctv1&serviceId=tvcctv&d=20201207&cb=t HTTP/1.1
 */
data class Cctv(
    val isLive: String? = null,
    val liveSt: Long? = 0L,
    val channelName: String? = null,
    val list: List<CctvItem>? = null
)

data class CctvItem(
    val eventId: String? = null,
    val columnBackvideourl: String? = null,
    val showTime: String? = null,
    val length: Long? = 0L,
    val startTime: Long? = 0L,
    val endTime: Long? = 0L,
    val eventType: String? = null,
    val title: String? = null
)