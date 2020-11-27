package com.zy.client.http

import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import com.zy.client.bean.*
import com.zy.client.common.HOME_LIST_TID_NEW
import com.zy.client.http.NetSourceParser.parseVideoDetail
import com.zy.client.http.NetSourceParser.parseChannelList
import com.zy.client.http.NetSourceParser.parseHomeData
import com.zy.client.http.NetSourceParser.parseSearch

/**
 * 通用的解析视频源
 *
 * @author javakam
 *
 * @date 2020/9/2 21:17
 */
class NetRepository(val req: CommonRequest) : IRepository {

    override fun getHomeData(callback: (t: HomeData?) -> Unit) {
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

    override fun getChannelList(
        page: Int,
        tid: String,
        callback: (t: List<VideoEntity>?) -> Unit
    ) {
        OkGo.get<String>(
            if (tid == HOME_LIST_TID_NEW) "${req.baseUrl}?pg=$page"
            else "${req.baseUrl}?ac=videolist&t=$tid&pg=$page"
        )
            .tag(req.key)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>?) {
                    try {
                        callback.invoke(parseChannelList(response?.body()))
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

    override fun search(
        searchWord: String,
        page: Int,
        callback: (t: List<VideoEntity>?) -> Unit
    ) {
        OkGo.get<String>("${req.baseUrl}?wd=$searchWord&pg=$page")
            .tag(req.key)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>?) {
                    try {
                        callback.invoke(parseSearch(response?.body()))
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

    override fun getVideoDetail(id: String, callback: (t: VideoDetail?) -> Unit) {
        OkGo.get<String>("${req.baseUrl}?ac=videolist&ids=$id")
            .tag(req.key)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>?) {
                    try {
                        callback.invoke(parseVideoDetail(req.key, response?.body()))
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