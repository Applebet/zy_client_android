package com.zy.client.utils.permission

import android.Manifest
import android.annotation.TargetApi
import android.content.res.Configuration
import android.content.res.Configuration.UI_MODE_TYPE_NORMAL
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.permissionx.guolindev.dialog.RationaleDialogFragment
import com.permissionx.guolindev.dialog.getPermissionMapOnQ
import com.permissionx.guolindev.dialog.getPermissionMapOnR
import com.zy.client.R
import kotlinx.android.synthetic.main.item_permissionx.view.*
import kotlinx.android.synthetic.main.layout_permission_dialog.*

@TargetApi(30)
class PermissionDialogFragment() : RationaleDialogFragment() {

    var mMessage: String = ""

    var mPermissions: List<String> = emptyList()

    constructor(message: String, permissions: List<String>) : this() {
        mMessage = message
        mPermissions = permissions
    }

    @Suppress("DEPRECATION")
    private val permissionMap = mapOf(
        Manifest.permission.READ_CALENDAR to Manifest.permission_group.CALENDAR,
        Manifest.permission.WRITE_CALENDAR to Manifest.permission_group.CALENDAR,
        Manifest.permission.READ_CALL_LOG to Manifest.permission_group.CALL_LOG,
        Manifest.permission.WRITE_CALL_LOG to Manifest.permission_group.CALL_LOG,
        Manifest.permission.PROCESS_OUTGOING_CALLS to Manifest.permission_group.CALL_LOG,
        Manifest.permission.CAMERA to Manifest.permission_group.CAMERA,
        Manifest.permission.READ_CONTACTS to Manifest.permission_group.CONTACTS,
        Manifest.permission.WRITE_CONTACTS to Manifest.permission_group.CONTACTS,
        Manifest.permission.GET_ACCOUNTS to Manifest.permission_group.CONTACTS,
        Manifest.permission.ACCESS_FINE_LOCATION to Manifest.permission_group.LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION to Manifest.permission_group.LOCATION,
        Manifest.permission.ACCESS_BACKGROUND_LOCATION to Manifest.permission_group.LOCATION,
        Manifest.permission.RECORD_AUDIO to Manifest.permission_group.MICROPHONE,
        Manifest.permission.READ_PHONE_STATE to Manifest.permission_group.PHONE,
        Manifest.permission.READ_PHONE_NUMBERS to Manifest.permission_group.PHONE,
        Manifest.permission.CALL_PHONE to Manifest.permission_group.PHONE,
        Manifest.permission.ANSWER_PHONE_CALLS to Manifest.permission_group.PHONE,
        Manifest.permission.ADD_VOICEMAIL to Manifest.permission_group.PHONE,
        Manifest.permission.USE_SIP to Manifest.permission_group.PHONE,
        Manifest.permission.ACCEPT_HANDOVER to Manifest.permission_group.PHONE,
        Manifest.permission.BODY_SENSORS to Manifest.permission_group.SENSORS,
        Manifest.permission.ACTIVITY_RECOGNITION to Manifest.permission_group.ACTIVITY_RECOGNITION,
        Manifest.permission.SEND_SMS to Manifest.permission_group.SMS,
        Manifest.permission.RECEIVE_SMS to Manifest.permission_group.SMS,
        Manifest.permission.READ_SMS to Manifest.permission_group.SMS,
        Manifest.permission.RECEIVE_WAP_PUSH to Manifest.permission_group.SMS,
        Manifest.permission.RECEIVE_MMS to Manifest.permission_group.SMS,
        Manifest.permission.READ_EXTERNAL_STORAGE to Manifest.permission_group.STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE to Manifest.permission_group.STORAGE,
        Manifest.permission.ACCESS_MEDIA_LOCATION to Manifest.permission_group.STORAGE
    )

    private val groupSet = HashSet<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return LayoutInflater.from(context)
            .inflate(R.layout.layout_permission_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        messageText.text = mMessage
        buildPermissionsLayout()
        isCancelable = true
    }

    override fun getNegativeButton(): View? {
        return negativeBtn
    }

    override fun getPositiveButton(): View {
        return positiveBtn
    }

    override fun getPermissionsToRequest(): List<String> {
        return mPermissions
    }

    private fun buildPermissionsLayout2() {
        for (permission in mPermissions) {
            val permissionGroup = permissionMap[permission]
            if (permissionGroup != null && !groupSet.contains(permissionGroup)) {
                val textView = LayoutInflater.from(context)
                    .inflate(R.layout.item_permissionx, permissionsLayout, false) as TextView
                textView.text = context?.let {
                    it.packageManager.getPermissionGroupInfo(permissionGroup, 0)
                        .loadLabel(it.packageManager)
                }
                permissionsLayout.addView(textView)
                groupSet.add(permissionGroup)
            }
        }
    }

    private fun buildPermissionsLayout() {
        val currentVersion = Build.VERSION.SDK_INT
        for (permission in mPermissions) {
            val permissionGroup = when (currentVersion) {
                Build.VERSION_CODES.Q -> {
                    getPermissionMapOnQ()[permission]
                }
                Build.VERSION_CODES.R -> {
                    getPermissionMapOnR()[permission]
                }
                else -> {
                    val permissionInfo =
                        requireContext().packageManager.getPermissionInfo(permission, 0)
                    permissionInfo.group
                }
            }
            if (permissionGroup != null && !groupSet.contains(permissionGroup)) {
                val layout = LayoutInflater.from(context)
                    .inflate(R.layout.item_permissionx, permissionsLayout, false) as LinearLayout
                context?.apply {
                    layout.tv_permission_name.text =
                        getString(
                            packageManager.getPermissionGroupInfo(
                                permissionGroup,
                                0
                            ).labelRes
                        )
                    layout.iv_permission_icon.setImageResource(
                        packageManager.getPermissionGroupInfo(permissionGroup, 0).icon
                    )
                }
//                if (isDarkTheme()) {
//                    if (darkColor != -1) {
//                        layout.iv_permission_icon.setColorFilter(darkColor, PorterDuff.Mode.SRC_ATOP)
//                    }
//                } else {
//                    if (lightColor != -1) {
//                        layout.iv_permission_icon.setColorFilter(lightColor, PorterDuff.Mode.SRC_ATOP)
//                    }
//                }
                permissionsLayout.addView(layout)
                groupSet.add(permissionGroup)
            }
        }
    }

    /**
     * Currently we are in dark theme or not.
     */
    private fun isDarkTheme(): Boolean {
        val flag = context?.resources?.configuration?.uiMode
            ?: UI_MODE_TYPE_NORMAL and Configuration.UI_MODE_NIGHT_MASK
        return flag == Configuration.UI_MODE_NIGHT_YES
    }

    private fun setupWindow() {
        val width = requireContext().resources.displayMetrics.widthPixels
        val height = requireContext().resources.displayMetrics.heightPixels
        if (width < height) {
            // now we are in portrait
            dialog?.window?.let {
                val param = it.attributes
                it.setGravity(Gravity.CENTER)
                param.width = (width * 0.86).toInt()
                it.attributes = param
            }
        } else {
            // now we are in landscape
            dialog?.window?.let {
                val param = it.attributes
                it.setGravity(Gravity.CENTER)
                param.width = (width * 0.6).toInt()
                it.attributes = param
            }
        }
    }


}