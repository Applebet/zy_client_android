package com.zy.client.utils;

import android.annotation.SuppressLint
import android.content.Context
import android.telephony.TelephonyManager

object NetConstants {
    /**
     * wifi net work
     */
    const val NETWORK_WIFI = "wifi"

    /**
     * "2G" networks
     */
    const val NETWORK_CLASS_2_G = "2G"

    /**
     * "3G" networks
     */
    const val NETWORK_CLASS_3_G = "3G"

    /**
     * "4G" networks
     */
    const val NETWORK_CLASS_4_G = "4G"

    /**
     * "5G" networks
     */
    const val NETWORK_CLASS_5_G = "5G"
}

object NetWorkUtils {

    @SuppressLint("MissingPermission")
    fun getNetWorkClass(context: Context): String {
        val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return when (telephonyManager.networkType) {
            TelephonyManager.NETWORK_TYPE_GPRS, TelephonyManager.NETWORK_TYPE_EDGE, TelephonyManager.NETWORK_TYPE_CDMA, TelephonyManager.NETWORK_TYPE_1xRTT, TelephonyManager.NETWORK_TYPE_IDEN -> NetConstants.NETWORK_CLASS_2_G
            TelephonyManager.NETWORK_TYPE_UMTS, TelephonyManager.NETWORK_TYPE_EVDO_0, TelephonyManager.NETWORK_TYPE_EVDO_A, TelephonyManager.NETWORK_TYPE_HSDPA, TelephonyManager.NETWORK_TYPE_HSUPA, TelephonyManager.NETWORK_TYPE_HSPA, TelephonyManager.NETWORK_TYPE_EVDO_B, TelephonyManager.NETWORK_TYPE_EHRPD, TelephonyManager.NETWORK_TYPE_HSPAP -> NetConstants.NETWORK_CLASS_3_G
            TelephonyManager.NETWORK_TYPE_LTE -> NetConstants.NETWORK_CLASS_4_G
            TelephonyManager.NETWORK_TYPE_NR -> NetConstants.NETWORK_CLASS_5_G
            else                                                                                                                                                                                                                                                                                                                                         -> NetConstants.NETWORK_WIFI
        }
    }

    fun isWifi(context: Context): Boolean {
        return NetConstants.NETWORK_WIFI == getNetWorkClass(context)
    }
}