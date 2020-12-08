package ando.player.setting

import android.content.Context
import android.content.SharedPreferences

/**
 * Title: UserSetting
 *
 * Description: 保存用户设置
 */
data class FloatPosition(var mDownX: Int = 0, var mDownY: Int = 0)

object UserSetting {

    const val LIST = "list"         //列表播放
    const val SEAMLESS = "seamless" //无缝播放
    const val PIP = "pip"           //画中画

    private const val PLAYER_SETTING = "player_setting"
    private const val BACKGROUND_PLAY = "background_play"
    private const val FLOAT_X = "float_x"
    private const val FLOAT_Y = "float_y"

    /**
     * 是否允许小窗播放
     * 默认不允许: false
     */
    fun setBackgroundPlay(context: Context, bgPlay: Boolean = false) {
        getSP(context).edit().putBoolean(BACKGROUND_PLAY, bgPlay).apply()
    }

    fun getBackgroundPlay(context: Context): Boolean {
        return getSP(context).getBoolean(BACKGROUND_PLAY, false)
    }

    /**
     * 记录小窗位置
     */
    fun setFloatPosition(context: Context, x: Int = 20, y: Int = 20) {
        getSP(context).edit().putInt(FLOAT_X, x).putInt(FLOAT_Y, y)
            .apply()
    }

    fun getFloatPosition(context: Context): FloatPosition {
        return FloatPosition(
            getSP(context).getInt(FLOAT_X, 20),
            getSP(context).getInt(FLOAT_Y, 20)
        )
    }

    private fun getSP(context: Context): SharedPreferences {
        return context.getSharedPreferences(PLAYER_SETTING, Context.MODE_PRIVATE)
    }

}