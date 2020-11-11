package com.zy.client.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.blankj.utilcode.util.ToastUtils
import com.zy.client.App
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
            return XmlToJson.Builder(xmlString!!).build()
        } catch (e: Exception) {
        }
        return null
    }
}