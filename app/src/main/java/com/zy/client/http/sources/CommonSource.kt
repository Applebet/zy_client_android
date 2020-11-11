package com.zy.client.http.sources

import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import com.zy.client.bean.entity.*

/**
 * @author javakam
 *
 * @date 2020/9/13 9:51
 * @desc 通用的解析视频源
 */
class CommonSource(
    override val key: String,
    override val name: String,
    override val baseUrl: String,
    override val downloadBaseUrl: String
) : BaseSource() {

    override fun requestHomeData(callback: (t: HomeData?) -> Unit) {
        OkGo.get<String>(baseUrl)
            .tag(key)
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
        callback: (t: ArrayList<HomeChannelData>?) -> Unit
    ) {
        OkGo.get<String>(if (tid == "new") "$baseUrl?ac=videolist&pg=$page" else "$baseUrl?ac=videolist&t=$tid&pg=$page")
            .tag(key)
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
        callback: (t: ArrayList<SearchResultData>?) -> Unit
    ) {
        OkGo.get<String>("$baseUrl?wd=$searchWord&pg=$page")
            .tag(key)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>?) {
                    try {
                        callback.invoke(parseSearchResultData(response?.body()))
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

    override fun requestDetailData(id: String, callback: (t: DetailData?) -> Unit) {
        OkGo.get<String>("$baseUrl?ac=videolist&ids=$id")
            .tag(key)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>?) {
                    try {
                        callback.invoke(parseDetailData(key, response?.body()))
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
        OkGo.get<String>("$downloadBaseUrl?ac=videolist&ids=$id")
            .tag(key)
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