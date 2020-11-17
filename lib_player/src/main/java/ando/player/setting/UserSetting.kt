package ando.player.setting

import android.content.Context

/**
 * Title: UserSetting
 *
 *
 * Description: 保存用户设置
 *
 *
 * @author javakam
 * @date 2020/11/17  10:52
 */
class UserConfig {
    //是否允许小窗播放
    var enableBackgroundPlay = false

    //记录小窗位置

}

object UserSetting {

    private const val PLAYER_SETTING = "player_setting"

    fun saveConfig(context: Context, config: UserConfig) {
        val sp = context.getSharedPreferences(PLAYER_SETTING, Context.MODE_PRIVATE)
        val set = mutableSetOf<String>()
        sp.edit().putStringSet("", set).apply()
    }

    val config: UserConfig
        get() {
            val config = UserConfig()
            config.enableBackgroundPlay = true
            return config
        }
}
