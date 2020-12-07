package com.zy.client.utils

import android.content.res.Resources
import com.zy.client.App
import fr.arnaudguyon.xmltojsonlib.XmlToJson
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author javakam
 */
object Utils {

    private const val DATE_FORMAT = "yyyy-MM-dd HH:mm:ss"
    private const val DATE_FORMAT1 = "yyyy-MM-dd HH:mm"
    private const val DATE_FORMAT2 = "yyyy-MM-dd"
    private const val DATE_FORMAT3 = "yyyyMMdd"

    fun parseTimeLong(time: String?): Long {
        if (time.isNullOrBlank()) return 0L
        try {
            val cal = Calendar.getInstance()
            cal.time = SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).parse(time) ?: return 0L
            return cal.timeInMillis
        } catch (e: Exception) {
        }
        return 0L
    }

    /**
     * 2020-11-27 00:07:26  ->  格式化后的样式
     */
    fun isToday(timeLong: Long): Boolean {
        val sdf = SimpleDateFormat(DATE_FORMAT2, Locale.getDefault())
        return sdf.format(Date(timeLong)) == sdf.format(Date())
    }

    /**
     * 返回今天日期: 20201207
     */
    fun getToday(): String {
        val sdf = SimpleDateFormat(DATE_FORMAT3, Locale.getDefault())
        return sdf.format(Date())
    }

    /**
     * 时间格式：
     * 1小时内用，多少分钟前；
     * 超过1小时，显示时间而无日期；
     * 如果是昨天，则显示昨天
     * 超过昨天再显示日期；
     * 超过1年再显示年。
     */
    fun millisToLifeString(millis: Long): String {
        val now = System.currentTimeMillis()
        val todayStart = string2Millis(millisToStringDate(now, "yyyy-MM-dd"), "yyyy-MM-dd")
        if (now - millis in 1..oneHourMillis) { // 一小时内
            val m = millisToStringShort(now - millis, isWhole = false, isFormat = false)
            return if ("" == m) "1分钟内" else m + "前"
        }
        if (millis >= todayStart && millis <= oneDayMillis + todayStart) { // 大于今天开始开始值，小于今天开始值加一天（即今天结束值）
            return "今天 " + millisToStringDate(millis, "HH:mm")
        }
        if (millis > todayStart - oneDayMillis) { // 大于（今天开始值减一天，即昨天开始值）
            return "昨天 " + millisToStringDate(millis, "HH:mm")
        }
        val thisYearStart = string2Millis(millisToStringDate(now, "yyyy"), "yyyy")
        return if (millis > thisYearStart) { // 大于今天小于今年
            millisToStringDate(millis, "MM月dd日 HH:mm")
        } else millisToStringDate(
            millis,
            "yyyy年MM月dd日 HH:mm"
        )
    }

    private const val oneHourMillis: Long = (60 * 60 * 1000).toLong()   // 一小时的毫秒数
    private const val oneDayMillis: Long = (24 * oneHourMillis)         // 一天的毫秒数

    private fun millisToStringDate(millis: Long, pattern: String?): String =
        SimpleDateFormat(pattern, Locale.getDefault()).format(Date(millis))

    /**
     * 字符串解析成毫秒数
     */
    private fun string2Millis(str: String, pattern: String): Long {
        val format = SimpleDateFormat(pattern, Locale.getDefault())
        var millis: Long = 0
        try {
            millis = format.parse(str)?.time ?: 0
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return millis
    }

    /**
     * 把一个毫秒数转化成时间字符串。格式为小时/分/秒/毫秒（如：24903600 --> 06小时55分钟）。
     *
     * @param millis   要转化的毫秒数。
     * @param isWhole  是否强制全部显示小时/分。
     * @param isFormat 时间数字是否要格式化，如果true：少位数前面补全；如果false：少位数前面不补全。
     * @return 返回时间字符串：小时/分/秒/毫秒的格式（如：24903600 --> 06小时55分钟）。
     */
    private fun millisToStringShort(millis: Long, isWhole: Boolean, isFormat: Boolean): String {
        var h = ""
        var m = ""
        if (isWhole) {
            h = if (isFormat) "00小时" else "0小时"
            m = if (isFormat) "00分钟" else "0分钟"
        }
        var temp = millis
        val hper = 60 * 60 * 1000.toLong()
        val mper = 60 * 1000.toLong()
        if (temp / hper > 0) {
            h = if (isFormat) {
                if (temp / hper < 10) "0" + temp / hper else "${temp / hper}"
            } else {
                "${temp / hper}"
            }
            h += "小时"
        }
        temp %= hper
        if (temp / mper > 0) {
            m = if (isFormat) {
                if (temp / mper < 10) "0" + temp / mper else "${temp / mper}"
            } else {
                "${temp / mper}"
            }
            m += "分钟"
        }
        return h + m
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