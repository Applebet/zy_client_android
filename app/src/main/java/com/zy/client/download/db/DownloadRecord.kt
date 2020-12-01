package com.zy.client.download.db

import org.litepal.crud.LitePalSupport

/**
 * Title: DownloadRecordModel
 * <p>
 * Description:
 * </p>
 * @author javakam
 * @date 2020/12/1  15:25
 */
data class DownRecordModel(
    //注: uniqueId = sourceKey + tid + id
    var uniqueId: String? = "",

    //更新时间
    val updateTime: String? = "",
    //更新至08集
    val note: String? = "",
    //Tv Url
    val tvUrl: String? = "",
    //Tv group
    val group: String? = "",

    //视频id
    var vid: String? = "",
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
    var videoList: List<RecordVideoModel>? = emptyList(),

    //渠道id  eg: zdzyw , okzyw
    val sourceKey: String? = ""
) : LitePalSupport()

data class RecordVideoModel(
    val name: String?,
    val playUrl: String?,
) : LitePalSupport()