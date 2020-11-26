package com.zy.client.common

import android.os.Environment
import com.zy.client.App
import com.zy.client.R

/**
 * Title:
 * <p>
 * Description:
 * </p>
 * @author javakam
 * @date 2020/11/11  15:52
 */

const val SOURCE_KEY = "source_key"
const val ID = "id"
const val TV_BEAN = "tv"

const val SP_OPEN_FL = "sp_open_fl"
const val OPEN_FL = true

const val HOME_LIST_TID_NEW = "new"
const val HOME_SPAN_COUNT = 3
const val VIDEO_VIEW_HEIGHT = R.dimen.dp_210

//截图保存路径
fun getScreenShotPath(): String =
    "${Environment.DIRECTORY_PICTURES}/${App.instance.getString(R.string.app_name)}"