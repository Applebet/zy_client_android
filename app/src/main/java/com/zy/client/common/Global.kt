package com.zy.client.common

import android.content.Context
import android.os.Environment
import com.zy.client.App
import com.zy.client.R

/**
 * Title: Global
 * <p>
 * Description:
 * </p>
 * @author javakam
 * @date 2020/11/11  15:52
 */

const val SOURCE_KEY = "source_key"
const val ID = "id"
const val TV_BEAN = "tv"

//eg: "${BROWSER_URL}${mVideo?.playUrl?.noNull()}"
const val BROWSER_URL = "http://zyplayer.fun/player/player.html?url="

const val SP_NET_SOURCE_KEY = "ShareConfig"
const val SP_HEALTHY_LIFE = "HealthyLife"

const val HOME_LIST_TID_NEW = "new"
const val HOME_SPAN_COUNT = 3
const val VIDEO_VIEW_HEIGHT = R.dimen.dp_210

//截图保存路径
fun getScreenShotPath(): String =
    "${Environment.DIRECTORY_PICTURES}/${App.instance.getString(R.string.app_name)}"

//健康生活
fun filterHealthyLife(s: String): Boolean {
    return (s.contains("福利")
            || s.contains("伦理")
            || s.contains("倫")
            || s.contains("写真")
            || s.contains("VIP", true)
            || s.contains("街拍"))
}

fun switchHealthLife(open: Boolean) {
    App.instance.getSharedPreferences(SP_HEALTHY_LIFE, Context.MODE_PRIVATE).apply {
        edit().putBoolean("healthy_life", open).apply()
    }
}

fun isHealthLife(): Boolean {
    App.instance.getSharedPreferences(SP_HEALTHY_LIFE, Context.MODE_PRIVATE).apply {
        return getBoolean("healthy_life", false)
    }
}