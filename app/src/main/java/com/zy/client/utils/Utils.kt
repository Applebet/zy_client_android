package com.zy.client.utils

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import com.zy.client.App
import com.zy.client.utils.ext.ToastUtils
import fr.arnaudguyon.xmltojsonlib.XmlToJson
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

/**
 * @author javakam
 * @date 2020/6/10 22:37
 */
object Utils {
    /**
     * 将json数据变成字符串
     */
    fun readAssetsData(fileName: String): String {
        val sb = StringBuilder()
        var bf: BufferedReader? = null
        try {
            bf = BufferedReader(InputStreamReader(App.instance.assets.open(fileName)))
            var line: String?
            while (bf.readLine().also { line = it } != null) {
                sb.append(line)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                bf?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return sb.toString()
    }

    /**
     * 打开外部浏览器
     */
    fun openBrowser(context: Context, url: String) {
        val intent = Intent().apply {
            action = Intent.ACTION_VIEW
            data = Uri.parse(url)
        }
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(Intent.createChooser(intent, "请选择浏览器"))
        } else {
            ToastUtils.showShort("没有可用浏览器")
        }
    }

    /**
     * xml转json
     */
    fun xmlToJson(xmlString: String?): XmlToJson? {
        try {
            return XmlToJson.Builder(xmlString ?: return null).build()
        } catch (e: Exception) {
        }
        return null
    }

    /**
     * Return the navigation bar's height.
     *
     * @return the navigation bar's height
     */
    fun getNavBarHeight(): Int {
        val res = Resources.getSystem()
        val resourceId = res.getIdentifier("navigation_bar_height", "dimen", "android")
        return if (resourceId != 0) res.getDimensionPixelSize(resourceId) else 0
    }

    /**
     * Return the status bar's height.
     *
     * @return the status bar's height
     */
    fun getStatusBarHeight(): Int {
        val resources = Resources.getSystem()
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        return resources.getDimensionPixelSize(resourceId)
    }

    /**
     * Value of dp to value of px.
     *
     * @param dpValue The value of dp.
     * @return value of px
     */
    fun dp2px(dpValue: Float): Int {
        val scale = Resources.getSystem().displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

}