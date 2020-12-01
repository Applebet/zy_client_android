package com.zy.client.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.app.NotificationManagerCompat
import com.zy.client.App

object NotificationUtils {

    val isNotificationEnabled: Boolean
        get() = NotificationManagerCompat.from(App.instance).areNotificationsEnabled()

    fun toNotificationSetting() {
        val context: Context = App.instance
        try {
            when {
                //21 以上，直接跳通知栏权限设置页
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                    val intent = Intent()
                    intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                    //android.settings.APP_NOTIFICATION_SETTINGS
                    intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                    intent.putExtra(Settings.EXTRA_CHANNEL_ID, context.applicationInfo.uid)
                    context.startActivity(intent)
                }
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> {
                    val intent = Intent()
                    //intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);android.settings.APP_NOTIFICATION_SETTINGS
                    intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
                    intent.putExtra("app_package", context.packageName)
                    intent.putExtra("app_uid", context.applicationInfo.uid)
                    context.startActivity(intent)
                }
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN -> {
                    val intent = Intent()
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    intent.data = Uri.fromParts("package", context.packageName, null)
                    context.startActivity(intent)
                }
            }
        } catch (e: Exception) {
            val intent = Intent()
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            intent.data = Uri.fromParts("package", context.packageName, null)
            context.startActivity(intent)
        }
    }

}