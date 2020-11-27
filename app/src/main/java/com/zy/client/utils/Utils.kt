package com.zy.client.utils

import android.content.res.Resources
import com.zy.client.App
import fr.arnaudguyon.xmltojsonlib.XmlToJson
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author javakam
 */
object Utils {

    /**
     * 2020-11-27 00:07:26  ->  格式化后的样式
     */
    fun isToday(time: String?): Boolean {
        return time?.run {
            if (isBlank()) return false
            try {
                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val cal = Calendar.getInstance()
                cal.time = sdf.parse(this) ?: return false
                val timeLong = cal.timeInMillis

                val sdf2 = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                return sdf2.format(Date(timeLong)) == sdf2.format(Date())
            } catch (e: Exception) {
            }
            false
        } ?: false
    }

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
        return if (resourceId > 0) resources.getDimensionPixelSize(resourceId) else 0
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