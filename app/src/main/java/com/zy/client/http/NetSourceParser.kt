package com.zy.client.http

import android.util.Log
import com.lzy.okgo.OkGo
import com.zy.client.bean.*
import com.zy.client.utils.Utils
import com.zy.client.utils.ext.isVideoUrl
import org.json.JSONArray
import org.json.JSONObject

object NetSourceParser {

    private fun createVideoEntity(video: JSONObject): VideoEntity {
        return VideoEntity(
            updateTime = video.optString("last"),
            id = video.optString("id"),
            tid = video.optString("tid"),
            name = video.optString("name"),
            type = video.optString("type"),
            note = video.optString("note"),
            pic = video.optString("pic")
        )
    }

    fun parseHomeData(data: String?): HomeData? {
        try {
            if (data == null) return null
            val jsonObject = Utils.xmlToJson(data)?.toJson()
            jsonObject?.optJSONObject("rss")?.run {
                val videoList = ArrayList<VideoEntity>()
                val video = getJSONObject("list").get("video")
                try {
                    if (video is JSONObject) videoList.add(createVideoEntity(video))
                    else if (video is JSONArray) {
                        for (i in 0 until video.length()) {
                            videoList.add(createVideoEntity(video.getJSONObject(i)))
                        }
                    }
                } catch (e: Exception) {
                }
                val classifyList = ArrayList<Classify>()
                try {
                    val classList = getJSONObject("class").getJSONArray("ty")
                    for (i in 0 until classList.length()) {
                        val json = classList.getJSONObject(i)
                        val content = json.optString("content")
                        if (!content.isNullOrBlank()) {
                            classifyList.add(
                                Classify(
                                    json.optString("id"),
                                    json.optString("content")
                                )
                            )
                        }
                    }
                } catch (e: Exception) {
                }
                return HomeData(videoList, classifyList)
            }
        } catch (e: Exception) {
        }
        return null
    }

    fun parseChannelList(data: String?): ArrayList<VideoEntity>? {
        try {
            if (data?.isBlank() == true) return null
            val jsonObject = Utils.xmlToJson(data)?.toJson()
            val videoList = ArrayList<VideoEntity>()
            val videos =
                jsonObject?.getJSONObject("rss")?.getJSONObject("list")?.optJSONArray("video")
                    ?: return arrayListOf()
            for (i in 0 until videos.length()) {
                videoList.add(createVideoEntity(videos.getJSONObject(i)))
            }
            return videoList
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun parseSearch(data: String?): ArrayList<VideoEntity>? {
        try {
            if (data?.isBlank() == true) return null
            val jsonObject = Utils.xmlToJson(data)?.toJson()
            val videoList = ArrayList<VideoEntity>()
            val video = jsonObject?.getJSONObject("rss")?.getJSONObject("list")?.opt("video")
            video?.apply {
                if (video is JSONObject) videoList.add(createVideoEntity(video))
                else if (video is JSONArray) {
                    for (i in 0 until video.length()) {
                        videoList.add(createVideoEntity(video.getJSONObject(i)))
                    }
                }
            }
            return videoList
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun parseVideoDetail(sourceKey: String, data: String?): VideoDetail? {
        try {
            if (data == null) return null
            val jsonObject = Utils.xmlToJson(data)?.toJson()
            Log.e("123", "parseDetailData = ${jsonObject.toString()}")
            val videoInfo =
                jsonObject?.optJSONObject("rss")?.optJSONObject("list")?.optJSONObject("video")
                    ?: return null
            val dd = videoInfo.getJSONObject("dl").get("dd")
            var videoList: ArrayList<Video>? = null
            if (dd is JSONObject) {
                videoList = dd.getString(("content")).split("#")
                    .map {
                        val split = it.split("$")
                        if (split.size >= 2) {
                            Video(split[0], split[1])
                        } else {
                            Video(split[0], split[0])
                        }
                    }.toMutableList() as ArrayList<Video>? ?: arrayListOf()
            } else if (dd is JSONArray) {
                for (i in 0 until dd.length()) {
                    val list = dd.optJSONObject(i)?.optString("content")?.split("#")
                        ?.map {
                            val split = it.split("$")
                            if (split.size >= 2) {
                                Video(split[0], split[1])
                            } else {
                                Video(split[0], split[0])
                            }
                        }?.toMutableList() as ArrayList<Video>? ?: arrayListOf()
                    if (list.size > 0) {
                        videoList = list
                        if (list[0].playUrl.isVideoUrl()) {
                            //优先获取应用内播放的资源
                            break
                        }
                    }
                }
            }
            return VideoDetail(
                id = videoInfo.optString("id"),
                tid = videoInfo.optString("tid"),
                name = videoInfo.optString("name"),
                type = videoInfo.optString("type"),
                lang = videoInfo.optString("lang"),
                area = videoInfo.optString("area"),
                pic = videoInfo.optString("pic"),
                year = videoInfo.optString("year"),
                actor = videoInfo.optString("actor"),
                director = videoInfo.optString("director"),
                des = videoInfo.optString("des"),
                videoList = videoList,
                sourceKey = sourceKey
            )

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun cancelAll(key: Any) {
        OkGo.cancelTag(OkGo.getInstance().okHttpClient, key)
    }

}