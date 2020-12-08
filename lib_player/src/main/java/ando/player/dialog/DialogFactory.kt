package ando.player.dialog

import ando.player.setting.Theme
import android.view.ViewGroup

internal object DialogFactory {

    fun createFullSpeedDialog(
            viewGroup: ViewGroup?, isFullScreen: Boolean, initSpeed: Int, theme: Theme?,
            listener: IPlayerCallBack?
    ): SpeedDialog {
        return SpeedDialog.Builder(viewGroup)
                .setInitSpeed(initSpeed)
                .setOnItemClickListener(listener)
                .setIsFullScreen(isFullScreen)
                .setTheme(theme)
                .build()
    }

    fun createVideoListDialog(
            viewGroup: ViewGroup?, isFullScreen: Boolean, data: List<String>, position: Int,
            listener: IPlayerCallBack?
    ): VideoListDialog {
        return VideoListDialog.Builder(viewGroup)
                .setIsFullScreen(isFullScreen)
                .setData(data)
                .setPosition(position)
                .setOnItemClickListener(listener)
                .build();
    }

}