package com.zy.client.http

import android.util.Log
import com.lzy.okgo.OkGo
import com.zy.client.bean.*
import com.zy.client.utils.Utils
import com.zy.client.utils.ext.isVideoUrl
import org.json.JSONArray
import org.json.JSONObject

object NetSourceParser {

    //以下为解析数据
    fun parseHomeData(data: String?): HomeData? {
        try {
            if (data == null) return null
            val jsonObject = Utils.xmlToJson(data)?.toJson()
            jsonObject?.getJSONObject("rss")?.run {
                val videoList = ArrayList<VideoSource>()
                val video = getJSONObject("list").get("video")
                try {
                    if (video is JSONObject) {
                        videoList.add(
                            VideoSource(
                                updateTime = video.getString("last"),
                                id = video.getString("id"),
                                tid = video.getString("tid"),
                                name = video.getString("name"),
                                type = video.getString("type")
                            )
                        )
                    } else if (video is JSONArray) {
                        for (i in 0 until video.length()) {
                            val json = video.getJSONObject(i)
                            videoList.add(
                                VideoSource(
                                    updateTime = json.getString("last"),
                                    id = json.getString("id"),
                                    tid = json.getString("tid"),
                                    name = json.getString("name"),
                                    type = json.getString("type")
                                )
                            )
                        }
                    }
                } catch (e: Exception) {
                }
                val classifyList = ArrayList<Classify>()
                try {
                    val classList = getJSONObject("class").getJSONArray("ty")
                    for (i in 0 until classList.length()) {
                        val json = classList.getJSONObject(i)
                        val content = json.getString("content")
                        if (!content.isNullOrBlank()) {
                            classifyList.add(
                                Classify(
                                    json.getString("id"),
                                    json.getString("content")
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

    fun parseHomeChannelData(data: String?): ArrayList<VideoSource>? {
        try {
            if (data == null) return arrayListOf()
            val jsonObject = Utils.xmlToJson(data)?.toJson()
            val videoList = ArrayList<VideoSource>()
            val videos = jsonObject?.getJSONObject("rss")?.getJSONObject("list")?.optJSONArray("video") ?: return arrayListOf()
            for (i in 0 until videos.length()) {
                val json = videos.getJSONObject(i)
                videoList.add(
                    VideoSource(
                        id = json.getString("id"),
                        name = json.getString("name"),
                        pic = json.getString("pic")
                    )
                )
            }
            return videoList
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return arrayListOf()
    }

    fun parseNewVideo(data: String?): ArrayList<VideoSource>? {
        try {
            if (data == null) return arrayListOf()
            val jsonObject = Utils.xmlToJson(data)?.toJson()
            val videoList = ArrayList<VideoSource>()
            val video = jsonObject?.getJSONObject("rss")?.getJSONObject("list")?.opt("video")
            video?.apply {
                if (video is JSONObject) {
                    videoList.add(
                        VideoSource(
                            id = video.getString("id"),
                            name = video.getString("name"),
                            type = video.getString("type")
                        )
                    )
                } else if (video is JSONArray) {
                    for (i in 0 until video.length()) {
                        val json = video.getJSONObject(i)
                        videoList.add(
                            VideoSource(
                                id = json.getString("id"),
                                name = json.getString("name"),
                                type = json.getString("type")
                            )
                        )
                    }
                }
            }
            return videoList
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return arrayListOf()
    }

    fun parseDetailData(sourceKey: String, data: String?): VideoDetail? {
        try {
            if (data == null) return null
            val jsonObject = Utils.xmlToJson(data)?.toJson()
            Log.e("123", "parseDetailData = ${jsonObject.toString()}")
            val videoInfo =
                jsonObject?.optJSONObject("rss")?.optJSONObject("list")?.optJSONObject("video") ?: return null
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
                    val list = dd.optJSONObject(i)?.getString("content")?.split("#")
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
                videoInfo.getString("id"),
                videoInfo.getString("tid"),
                videoInfo.getString("name"),
                videoInfo.getString("type"),
                videoInfo.getString("lang"),
                videoInfo.getString("area"),
                videoInfo.getString("pic"),
                videoInfo.getString("year"),
                videoInfo.getString("actor"),
                videoInfo.getString("director"),
                videoInfo.getString("des"),
                videoList,
                sourceKey
            )

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun parseDownloadData(data: String?): ArrayList<DownloadData>? {
        try {
            if (data == null) return null
            val jsonObject = Utils.xmlToJson(data)?.toJson()
            val video =
                jsonObject?.optJSONObject("rss")?.optJSONObject("list")?.optJSONObject("video")

            return video?.let { v ->
                v.getJSONObject("dl").getJSONObject("dd").getString("content").split("#")
                    .map {
                        val split = it.split("$")
                        if (split.size >= 2) {
                            DownloadData(split[0], split[1])
                        } else {
                            DownloadData(split[0], split[0])
                        }
                    }.toMutableList() as ArrayList<DownloadData>? ?: arrayListOf()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return arrayListOf()
    }

    fun cancelAll(key: Any) {
        OkGo.cancelTag(OkGo.getInstance().okHttpClient, key)
    }

}