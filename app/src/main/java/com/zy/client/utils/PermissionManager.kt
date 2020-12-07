package com.zy.client.utils

import android.Manifest
import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.zy.client.App
import com.zy.client.BuildConfig
import com.zy.client.utils.ext.toastLong
import com.zy.client.utils.status.OSUtils


/**
 * <pre>
 *      shouldShowRequestPermissionRationale -> https://blog.csdn.net/wangpf2011/article/details/80589648
 *
 *      如果返回true表示用户点了禁止获取权限，但没有勾选不再提示。
 *      返回false表示用户点了禁止获取权限，并勾选不再提示 or 第一次请求权限时 false
 *
 *      val shouldShow =ActivityCompat.shouldShowRequestPermissionRationale(fragment.activity,Manifest.permission.CAMERA)
 *
 *      L.w("shouldShow  $shouldShow")
 * </pre>
 * <pre>
 *   shouldShowRequestPermissionRationale
 *       1. 第一次请求权限时 ActivityCompat -> false;
 *       2. 第一次请求权限被禁止，但未选择【不再提醒】 -> true;
 *       3. 允许某权限后 -> false;
 *       4. 禁止权限，并选中【禁止后不再询问】 -> false；
 *
 *  从前面就可以看出来，这个方法大部分情况下是放回false的，只有被用户拒绝了权限，再次获取才会得到true；如果没有申请过，或者禁止了权限，都是返回的false。
 *  所以很多人想要通过shouldShowRequestPermissionRationale去判断是否权限被禁止，有时候是并不准确的，真要说怎样会准确的获取到权限被禁止的情况，那就是：
 *       1.在requestPermissions之后在onRequestPermissionsResult中获取到没给权限，
 *          并且shouldShowRequestPermissionRationale是false，此时可以认定该权限被用户禁止了；
 *       2.还有一个点是是在onRequestPermissionsResult的参数值第三个参数grantResults是null,此时权限也是被拒绝的。
 *         （权限被拒绝后再次调用requestPermissions，没有返回结果）
 *
 * </pre>
 */
object PermissionManager {

    const val REQUEST_EXTERNAL_STORAGE: Int = 21
    const val REQUEST_EXTERNAL_CAMERA: Int = 22
    const val REQUEST_OVERLAY: Int = 23

    var PERMISSIONS_STORAGE = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE
    ) else arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    var PERMISSIONS_CAMERA = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    fun verifyStoragePermissions(activity: Activity) {
        // Check if we have write permission
        for (permission in PERMISSIONS_STORAGE) {
            val granted = ContextCompat.checkSelfPermission(activity, permission)
            if (granted != PackageManager.PERMISSION_GRANTED) {
                // We don't have permission so prompt the user
                ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
                )
                break
            }
        }
    }

    fun verifyCameraPermissions(activity: Activity) {
        // Check if we have write permission
        for (permission in PERMISSIONS_CAMERA) {
            val granted = ContextCompat.checkSelfPermission(activity, permission)
            if (granted != PackageManager.PERMISSION_GRANTED) {
                // We don't have permission so prompt the user
                ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_CAMERA,
                    REQUEST_EXTERNAL_CAMERA
                )
                break
            }
        }
    }

    fun checkShowRationale(
        activity: Activity,
        vararg permissions: String
    ): Boolean {
        //用户点了禁止获取权限，但没有勾选不再提示
        var showRationale = true
        if (permissions.isNotEmpty()) {
            for (permission in permissions) {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(
                        activity,
                        permission
                    )
                ) {
                    //用户点了禁止获取权限，并勾选不再提示
                    showRationale = false
                    break
                }
            }
        }
        return showRationale
    }

    /**
     * 1.采用该策略申请权限 <必须> 在进入页面时候申请权限  PermissionManager.verifyStoragePermissions(this)
     * 然后在 Click 事件中再次处理, 避开 ` 第一次请求权限时 ActivityCompat 返回为 false;` 的问题;
     * 2.此方式不需要处理 onRequestPermissionsResult 回调
     * <pre>
     *      val  showRationale = PermissionManager.checkShowRationaleAndGoToSetting(this,"请您到系统权限页面申请存储权限!",*PERMISSIONS_STORAGE)
     *      if (showRationale){
     *          PermissionManager.verifyStoragePermissions(this)
     *      }
     *      return@setOnClickListener
     * </pre>
     */
    fun checkShowRationaleAndGoToSetting(
        activity: Activity,
        notice: String? = "请申请存储权限!",
        vararg permissions: String
    ): Boolean {
        var showRationale: Boolean = true
        if (permissions.isNotEmpty()) {
            for (permission in permissions) {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(
                        activity,
                        permission
                    )
                ) {
                    showRationale = false
                    break
                }
            }
        }
        if (!showRationale) { //用户点了禁止获取权限，并勾选不再提示
            Toast.makeText(activity, notice, Toast.LENGTH_LONG).show()
            goToSettings(activity)
            return false
        }
        return true
    }

    fun handleRequestPermissionsResult(
        activity: Activity,
        permissions: Array<String>,
        grantResults: IntArray,
        block: (result: Boolean, showRationale: Boolean) -> Unit
    ) {
        // If request is cancelled, the result arrays are empty.
        if (havePermissions(grantResults)) {
            //showImages()
            block.invoke(true, false)
        } else {
            // If we weren't granted the permission, check to see if we should show
            // rationale for the permission.

            val showRationale = checkShowRationale(activity, *permissions)

            permissions.forEach {
                //FileLogger.i( "权限 : $it")
            }
            Log.i("123", "handleRequestPermissionsResult showRationale=$showRationale ")
            block.invoke(false, showRationale)
        }
    }

    fun havePermissions(grantResults: IntArray): Boolean {
        if (grantResults.isNotEmpty()) {
            grantResults.forEach {
                if (it != PackageManager.PERMISSION_GRANTED)
                    return false
            }
        }
        return true
    }

    fun havePermissions(context: Context?, vararg permissions: String): Boolean {
        if (context != null && permissions.isNotEmpty()) {
            for (permission in permissions) {
                if (ContextCompat.checkSelfPermission(
                        context,
                        permission
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return false
                }
            }
        }
        return true
    }

    /**
     * 悬浮窗权限  AndroidManifest.xml
     *
     * <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW"/>
     */
    fun overlay(activity: Activity, onGranted: () -> Unit = {}) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //检查是否已经授予权限
            if (!Settings.canDrawOverlays(activity)) {
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                intent.data = Uri.parse("package:${activity.packageName}")
                activity.startActivityForResult(intent, REQUEST_OVERLAY)
            } else onGranted.invoke()
        } else onGranted.invoke()
    }

    /**
     * 获取应用权限详情页面 Intent
     */
    fun goToSettings(activity: Activity) {
        Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.parse("package:${activity.packageName}")
        ).apply {
            addCategory(Intent.CATEGORY_DEFAULT)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }.also { intent ->
            activity.startActivity(intent)
        }
    }

    private fun createAppSettingIntent(): Intent {
        return when {
            OSUtils.isEmui -> {
                gotoHuaweiPermission()
            }
            OSUtils.isMiui -> {
                gotoMiuiPermission()
            }
            OSUtils.isFlyme -> {
                gotoMeizuPermission()
            }
            else -> createAppDetailSettingIntent()
        }
    }

    /**
     * 获取应用详情页面 通用 Intent
     *
     * https://www.cnblogs.com/zhujiabin/p/9284835.html
     */
    private fun createAppDetailSettingIntent(): Intent {
        val localIntent = Intent()
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            localIntent.action = "android.settings.APPLICATION_DETAILS_SETTINGS"
            localIntent.data = Uri.fromParts("package", App.instance.packageName, null)
        } else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.FROYO) {
            localIntent.action = Intent.ACTION_VIEW
            localIntent.setClassName(
                "com.android.settings",
                "com.android.settings.InstalledAppDetails"
            )
            localIntent.putExtra(
                "com.android.settings.ApplicationPkgName",
                App.instance.packageName
            )
        }
        return localIntent
    }

    /**
     * 跳转到miui的权限管理页面
     */
    private fun gotoMiuiPermission(): Intent {
        return try {
            val intent = Intent("miui.intent.action.APP_PERM_EDITOR")
            val componentName = ComponentName(
                "com.miui.securitycenter",
                "com.miui.permcenter.permissions.AppPermissionsEditorActivity"
            )
            intent.component = componentName
            intent.putExtra("extra_pkgname", App.instance.packageName)
            intent
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            createAppDetailSettingIntent()
        }
    }

    /**
     * 魅族的权限管理页面
     */
    private fun gotoMeizuPermission(): Intent {
        return try {
            val intent = Intent("com.meizu.safe.security.SHOW_APPSEC")
            intent.addCategory(Intent.CATEGORY_DEFAULT)
            intent.putExtra("packageName", BuildConfig.APPLICATION_ID)
            intent
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            createAppDetailSettingIntent()
        }
    }

    /**
     * 华为的权限管理页面
     */
    private fun gotoHuaweiPermission(): Intent {
        return try {
            val intent = Intent()
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

            //华为权限管理
            val comp = ComponentName(
                "com.huawei.systemmanager",
                "com.huawei.permissionmanager.ui.MainActivity"
            )
            intent.component = comp
            intent
        } catch (e: Exception) {
            e.printStackTrace()
            createAppDetailSettingIntent()
        }
    }

    /////////////////////////

    fun proceedStoragePermission(activity: Activity, block: (result: Boolean) -> Unit) {
        val hasStoragePermission =
            havePermissions(activity, *PERMISSIONS_STORAGE)
        if (hasStoragePermission) block.invoke(true)

        val shouldShow = checkShowRationale(
            activity,
            *PERMISSIONS_STORAGE
        )

        Log.i("123", "proceedStoragePermission  shouldShow = $shouldShow")

        //用户点了禁止获取权限，并勾选不再提示 , 建议做成弹窗提示并提供权限申请页面的跳转
        if (!shouldShow) {
            verifyStoragePermissions(activity)

//            activity.toastLong("""请开启"存储"权限! """)
//            val intent = createAppSettingIntent()
//            activity.startActivity(intent)
            return
        }

        block.invoke(true)
    }

}