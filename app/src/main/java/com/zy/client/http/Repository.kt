package com.zy.client.http

import com.zy.client.bean.DownloadData
import com.zy.client.bean.HomeData
import com.zy.client.bean.VideoDetail
import com.zy.client.bean.VideoSource
import org.litepal.annotation.Column
import org.litepal.crud.LitePalSupport
import java.io.Serializable

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
    fun requestHomeData(callback: (t: HomeData?) -> Unit)

    /**
     * 频道列表
     */
    fun requestHomeChannelData(page: Int, tid: String, callback: (t: List<VideoSource>?) -> Unit)

    /**
     * 搜索
     */
    fun requestSearchData(searchWord: String, page: Int, callback: (t: List<VideoSource>?) -> Unit)

    /**
     * 视频详情
     */
    fun requestDetailData(id: String, callback: (t: VideoDetail?) -> Unit)

    /**
     * 下载列表
     */
    fun requestDownloadData(id: String, callback: (t: ArrayList<DownloadData>?) -> Unit)

}