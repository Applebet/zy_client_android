package com.zy.client.http

import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import com.zy.client.bean.*
import com.zy.client.common.HOME_LIST_TID_NEW
import com.zy.client.http.NetSourceParser.parseDetailData
import com.zy.client.http.NetSourceParser.parseDownloadData
import com.zy.client.http.NetSourceParser.parseHomeChannelData
import com.zy.client.http.NetSourceParser.parseHomeData
import com.zy.client.http.NetSourceParser.parseNewVideo

/**
 * 通用的解析视频源
 *
 * @author javakam
 *
 * @date 2020/9/2 21:17
 */
class NetRepository(val req: CommonRequest) : IRepository {

    override fun requestHomeData(callback: (t: HomeData?) -> Unit) {
        OkGo.get<String>(req.baseUrl)
            .tag(req.key)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>?) {
                    try {
                        callback.invoke(parseHomeData(response?.body()))
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onError(response: Response<String>?) {
                    super.onError(response)
                    try {
                        callback.invoke(null)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            })
    }

    override fun requestHomeChannelData(
        page: Int,
        tid: String,
        callback: (t: List<VideoSource>?) -> Unit
    ) {
        OkGo.get<String>(
            if (tid == HOME_LIST_TID_NEW) "${req.baseUrl}?pg=$page"
            else "${req.baseUrl}?ac=videolist&t=$tid&pg=$page"
        )
            .tag(req.key)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>?) {
                    try {
                        callback.invoke(parseHomeChannelData(response?.body()))
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onError(response: Response<String>?) {
                    super.onError(response)
                    try {
                        callback.invoke(null)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            })
    }

    override fun requestSearchData(
        searchWord: String,
        page: Int,
        callback: (t: List<VideoSource>?) -> Unit
    ) {
        OkGo.get<String>("${req.baseUrl}?wd=$searchWord&pg=$page")
            .tag(req.key)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>?) {
                    try {
                        callback.invoke(parseNewVideo(response?.body()))
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onError(response: Response<String>?) {
                    super.onError(response)
                    try {
                        callback.invoke(null)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            })
    }

    override fun requestDetailData(id: String, callback: (t: VideoDetail?) -> Unit) {
        OkGo.get<String>("${req.baseUrl}?ac=videolist&ids=$id")
            .tag(req.key)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>?) {
                    try {
                        callback.invoke(parseDetailData(req.key, response?.body()))
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onError(response: Response<String>?) {
                    super.onError(response)
                    try {
                        callback.invoke(null)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            })
    }

    override fun requestDownloadData(id: String, callback: (t: ArrayList<DownloadData>?) -> Unit) {
        OkGo.get<String>("${req.downloadBaseUrl}?ac=videolist&ids=$id")
            .tag(req.key)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>?) {
                    try {
                        callback.invoke(parseDownloadData(response?.body()))
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onError(response: Response<String>?) {
                    super.onError(response)
                    try {
                        callback.invoke(null)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            })
    }

}