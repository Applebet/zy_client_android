package com.zy.client.http

import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import com.zy.client.ui.detail.model.FilmDetailModel
import com.zy.client.ui.home.model.FilmModel

/**
 * @author javakam
 * @date 2020/6/8 23:48
 */
const val TAG_FILM_GET = "tag_film_get"
const val TAG_FILM_DETAIL_GET = "tag_film_detail_get"

interface CommonCallback<T> {
    fun onResult(t: T)
}

object NetLoader {

    /**
     * 最新数据
     */
    fun filmGet(
        key: String,
        id: Int = 0,
        page: Int = 1,
        callback: CommonCallback<FilmModel?>
    ) {
        val configItem = ConfigManager.configMap[key]
        val url = if (id == 0) {
            configItem!!.new.replace("{page}", page.toString())
        } else {
            configItem!!.view.replace("{id}", id.toString())
                .replace("{page}", page.toString())
        }
        cancel(TAG_FILM_GET)
        OkGo.get<String>(url)
            .tag(TAG_FILM_GET)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>?) {
                    onNetworkResult(
                        callback,
                        DataParser.parseFilmGet(
                            key,
                            response?.body() ?: "",
                            configItem.type
                        )
                    )
                }

                override fun onError(response: Response<String>?) {
                    super.onError(response)
                    onNetworkResult(callback, null)
                }

                override fun onFinish() {

                }
            })
    }

    /**
     * 搜索数据
     */
    fun searchGet(
        key: String,
        keywords: String,
        page: Int = 1,
        callback: CommonCallback<FilmModel?>
    ) {
        val configItem = ConfigManager.configMap[key]
        if (configItem!!.search.isBlank()) {
            ToastUtils.showShort("该视频源不支持搜索")
            onNetworkResult(callback, null)
            return
        }
        val url = if (configItem.type == 0) {
            configItem.search.replace("{page}", page.toString()).replace("{keywords}", keywords)
        } else {
            configItem.search.replace("{keywords}", keywords)
        }
        cancel(TAG_FILM_GET)
        OkGo.get<String>(url)
            .tag(TAG_FILM_GET)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>?) {
                    onNetworkResult(
                        callback,
                        DataParser.parseSearchGet(
                            key,
                            response?.body() ?: "",
                            configItem.type
                        )
                    )
                }

                override fun onError(response: Response<String>?) {
                    super.onError(response)
                    onNetworkResult(callback, null)
                }

                override fun onFinish() {
                }
            })
    }

    /**
     * 详情页
     */
    fun filmDetailGet(
        key: String,
        detailUrl: String,
        callback: CommonCallback<FilmDetailModel?>
    ) {
        val configItem = ConfigManager.configMap[key]
        if (configItem == null) {
            callback.onResult(null)
            return
        }
        cancel(TAG_FILM_DETAIL_GET)
        OkGo.get<String>(detailUrl)
            .tag(TAG_FILM_DETAIL_GET)
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>?) {
                    onNetworkResult(
                        callback,
                        DataParser.parseDetailGet(
                            key,
                            response?.body() ?: "",
                            configItem.type
                        )
                    )
                }

                override fun onError(response: Response<String>?) {
                    super.onError(response)
                    callback.onResult(null)
                }

                override fun onFinish() {

                }
            })
    }

    private fun <T> onNetworkResult(callback: CommonCallback<T?>?, t: T?) {
        try {
            callback?.onResult(t)
        } catch (e: Exception) {
            e.printStackTrace()
            LogUtils.e(e.toString())
        }
    }

    private fun cancel(tag: Any) {
        OkGo.cancelTag(OkGo.getInstance().okHttpClient, tag)
    }
}