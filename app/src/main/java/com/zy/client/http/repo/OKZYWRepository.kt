package com.zy.client.http.repo

import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import com.zy.client.bean.DownloadData
import com.zy.client.bean.HomeData
import com.zy.client.bean.VideoDetail
import com.zy.client.bean.VideoSource
import com.zy.client.common.HOME_LIST_TID_NEW
import com.zy.client.http.NetSourceParser.parseDetailData
import com.zy.client.http.NetSourceParser.parseDownloadData
import com.zy.client.http.NetSourceParser.parseHomeChannelData
import com.zy.client.http.NetSourceParser.parseHomeData
import com.zy.client.http.NetSourceParser.parseNewVideo

/**
 * @author javakam
 *
 * @date 2020/9/2 21:47
 * @desc OK 资源网
 */

class OKZYWRepository(
    val baseUrl: String = "http://cj.okzy.tv/inc/api.php",
    val downloadBaseUrl: String = "http://cj.okzy.tv/inc/apidown.php",
    val name: String = "OK 资源网",
    val key: String = "okzy"
) : IRepository {

    override fun requestHomeData(callback: (t: HomeData?) -> Unit) {
        OkGo.get<String>(baseUrl)
            .tag(key)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>?) {
                    try {
                        callback.invoke(parseHomeData(response?.body()))
                    } catch (e: Exception) {
                    }
                }

                override fun onError(response: Response<String>?) {
                    super.onError(response)
                    try {
                        callback.invoke(null)
                    } catch (e: Exception) {
                    }
                }
            })
    }

    override fun requestHomeChannelData(
        page: Int,
        tid: String,
        callback: (t: List<VideoSource>?) -> Unit
    ) {
        OkGo.get<String>(if (tid == HOME_LIST_TID_NEW) "$baseUrl?ac=videolist&pg=$page" else "$baseUrl?ac=videolist&t=$tid&pg=$page")
            .tag(key)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>?) {
                    try {
                        callback.invoke(parseHomeChannelData(response?.body()))
                    } catch (e: Exception) {
                    }
                }

                override fun onError(response: Response<String>?) {
                    super.onError(response)
                    try {
                        callback.invoke(null)
                    } catch (e: Exception) {
                    }
                }
            })
    }

    override fun requestSearchData(
        searchWord: String,
        page: Int,
        callback: (t: List<VideoSource>?) -> Unit
    ) {
        OkGo.get<String>("$baseUrl?wd=$searchWord&pg=$page")
            .tag(key)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>?) {
                    try {
                        callback.invoke(parseNewVideo(response?.body()))
                    } catch (e: Exception) {
                    }
                }

                override fun onError(response: Response<String>?) {
                    super.onError(response)
                    try {
                        callback.invoke(null)
                    } catch (e: Exception) {
                    }
                }
            })
    }

    override fun requestDetailData(id: String, callback: (t: VideoDetail?) -> Unit) {
        OkGo.get<String>("$baseUrl?ac=videolist&ids=$id")
            .tag(key)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>?) {
                    try {
                        callback.invoke(parseDetailData(key, response?.body()))
                    } catch (e: Exception) {
                    }
                }

                override fun onError(response: Response<String>?) {
                    super.onError(response)
                    try {
                        callback.invoke(null)
                    } catch (e: Exception) {
                    }
                }
            })
    }

    override fun requestDownloadData(id: String, callback: (t: ArrayList<DownloadData>?) -> Unit) {
        OkGo.get<String>("$downloadBaseUrl?ac=videolist&ids=$id")
            .tag(key)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>?) {
                    try {
                        callback.invoke(parseDownloadData(response?.body()))
                    } catch (e: Exception) {
                    }
                }

                override fun onError(response: Response<String>?) {
                    super.onError(response)
                    try {
                        callback.invoke(null)
                    } catch (e: Exception) {
                    }
                }
            })
    }

}