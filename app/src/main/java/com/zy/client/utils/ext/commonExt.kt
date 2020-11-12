package com.zy.client.utils.ext

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.View
import androidx.core.view.isVisible
import com.zy.client.App
import java.util.*

/**
 * Title: 扩展函数
 * <p>
 * Description:
 * </p>
 * @author javakam
 * @date 2020/11/11  15:53
 */

fun View?.visible() {
    this?.run {
        if (!isVisible) visibility = View.VISIBLE
    }
}

fun View?.invisible() {
    this?.run {
        if (isVisible) visibility = View.INVISIBLE
    }
}

fun View?.gone() {
    this?.run {
        if (isVisible) visibility = View.GONE
    }
}

fun String?.noNull(default: String? = ""): String {
    return if (isNullOrBlank()) default ?: "" else this
}

/**
 * 可以在应用内播放的地址
 */
fun String?.isVideoUrl(): Boolean {
    return (this ?: "").toLowerCase(Locale.ROOT).run {
        endsWith(".m3u8")
                || endsWith(".mp4")
                || endsWith(".flv")
                || endsWith(".avi")
                || endsWith(".rm")
                || endsWith(".rmvb")
                || endsWith(".wmv")
    }
}

fun String.copyToClipBoard() {
    val cm: ClipboardManager? =
        App.instance.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager?
    if (cm != null) {
        cm.setPrimaryClip(ClipData.newPlainText(null, this))//参数一：标签，可为空，参数二：要复制到剪贴板的文本
        if (cm.hasPrimaryClip()) {
            cm.primaryClip?.getItemAt(0)?.text
        }
    }
}
