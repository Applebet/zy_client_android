package com.zy.client.http

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import com.zy.client.bean.*
import com.zy.client.common.HOME_LIST_TID_NEW
import com.zy.client.http.NetSourceParser.parseChannelList
import com.zy.client.http.NetSourceParser.parseHomeData
import com.zy.client.http.NetSourceParser.parseSearch
import com.zy.client.http.NetSourceParser.parseVideoDetail
import com.zy.client.utils.Utils

/**
 * 通用的解析视频源
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

    /**
     *  https://api.cntv.cn/epg/getEpgInfoByChannelNew
     *  ?c=cctv1 & serviceId=tvcctv & d=20201207 & cb=t HTTP/1.1
     */
    override fun getCCTVMenu(tvId: String, callback: (t: Cctv?) -> Unit) {
        val key = "tv_$tvId"
        OkGo.get<String>("https://api.cntv.cn/epg/getEpgInfoByChannelNew?c=${tvId}&serviceId=tvcctv&d=${Utils.getToday()}&cb=t HTTP/1.1")
            .tag(key)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>?) {
                    try {
                        val body: String = response?.body() ?: ""
                        if (body.isBlank()) {
                            callback.invoke(null)
                            return
                        }
                        val realBody = body.substring(body.indexOf("{"), body.length - 2)
                        val jsonData: JsonObject = JsonParser.parseString(realBody).asJsonObject
                        val jsonCCTV: JsonObject? =
                            jsonData.get("data")?.asJsonObject?.get(tvId)?.asJsonObject
                        if (jsonCCTV == null) {
                            callback.invoke(null)
                        } else {
                            callback.invoke(Gson().fromJson(jsonCCTV, Cctv::class.java))
                        }
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