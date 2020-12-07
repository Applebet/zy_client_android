package com.zy.client.http

import com.zy.client.bean.Cctv
import com.zy.client.bean.HomeData
import com.zy.client.bean.VideoDetail
import com.zy.client.bean.VideoEntity

data class CommonRequest(
    val key: String = "",
    val name: String = "",
    val baseUrl: String = "",
    val downloadBaseUrl: String = ""
)

interface IRepository {

    /**
     * 首页
     */
    fun getHomeData(callback: (t: HomeData?) -> Unit)

    /**
     * 分类对应视频列表
     */
    fun getChannelList(page: Int, tid: String, callback: (t: List<VideoEntity>?) -> Unit)

    /**
     * 搜索
     */
    fun search(searchWord: String, page: Int, callback: (t: List<VideoEntity>?) -> Unit)

    /**
     * 视频详情
     */
    fun getVideoDetail(id: String, callback: (t: VideoDetail?) -> Unit)

    /**
     * 电视预告
     */
    fun getCCTVMenu(tvId: String, callback: (t: Cctv?) -> Unit)

}