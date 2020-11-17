package com.zy.client.utils.notch

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.view.WindowManager
import com.zy.client.utils.RomUtil
import com.zy.client.utils.Utils

/**
 * 比较全的方式
 *
 * https://github.com/ChristianFF/NotchCompat
 */
object NotchDisplayUtils {

    fun adapterNotch(activity: Activity) {
        if (hasNotchInScreen(activity)) {
            //如果是刘海屏 直接适配
            val lp = activity.window.attributes
            //针对9.0适配
            // oppo 中设置对于下面代码无效 只能走第二条路线
            if (Build.VERSION.SDK_INT >= 28 && !RomUtil.isOppo) {
                lp.layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER
            } else {
                //第二种适配方案理论上适配所有，将背景设置为黑色，将布局向下摞动状态栏动高度，一般刘海的高度约等于状态栏的高度
                val decorView = activity.window.decorView
                decorView.setBackgroundColor(Color.BLACK)
                decorView.setPadding(
                    decorView.left,
                    Utils.getStatusBarHeight(),
                    decorView.paddingRight,
                    decorView.paddingBottom
                )
            }
            activity.window.attributes = lp
        }
    }

    /**
     * 判断是否有刘海屏
     */
    fun hasNotchInScreen(activity: Activity): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val displayCutout = activity.window?.decorView?.rootWindowInsets?.displayCutout
            if (displayCutout != null) {
                // 说明有刘海屏
                return true
            }
        }
        when {
            RomUtil.isMiui -> return hasNotchXiaoMi(activity)
            RomUtil.isOppo -> return hasNotchOPPO(activity)
            RomUtil.isVivo -> return hasNotchVIVO(activity)
            RomUtil.isEmui -> return hasNotchHw(activity)
        }
        return false
    }

    /**
     * 判断vivo是否有刘海屏
     * https://swsdl.vivo.com.cn/appstore/developer/uploadfile/20180328/20180328152252602.pdf
     *
     * @param activity
     * @return
     */
    private fun hasNotchVIVO(activity: Activity): Boolean {
        return try {
            val c = Class.forName("android.util.FtFeature")
            val get = c.getMethod("isFeatureSupport", Int::class.javaPrimitiveType!!)
            get.invoke(c, 0x20) as Boolean
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }

    }

    /**
     * 判断oppo是否有刘海屏
     * https://open.oppomobile.com/wiki/doc#id=10159
     *
     * @param activity
     * @return
     */
    private fun hasNotchOPPO(activity: Activity): Boolean {
        return activity.packageManager.hasSystemFeature("com.oppo.feature.screen.heteromorphism")
    }

    /**
     * 判断xiaomi是否有刘海屏
     * https://dev.mi.com/console/doc/detail?pId=1293
     *
     * @param activity
     * @return
     */
    private fun hasNotchXiaoMi(activity: Activity): Boolean {
        return try {
            val c = Class.forName("android.os.SystemProperties")
            val get = c.getMethod("getInt", String::class.java, Int::class.javaPrimitiveType)
            get.invoke(c, "ro.miui.notch", 0) as Int == 1
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }

    }

    /**
     * 判断华为是否有刘海屏
     * https://devcenter-test.huawei.com/consumer/cn/devservice/doc/50114
     *
     * @param activity
     * @return
     */
    private fun hasNotchHw(activity: Activity): Boolean {
        return try {
            val cl = activity.classLoader
            val hwNotchSizeUtil = cl.loadClass("com.huawei.android.util.HwNotchSizeUtil")
            val get = hwNotchSizeUtil.getMethod("hasNotchInScreen")
            get.invoke(hwNotchSizeUtil) as Boolean
        } catch (e: Exception) {
            false
        }
    }

}