package ando.player.dialog;

import android.content.Context;
import android.view.ViewGroup;

import ando.player.setting.Theme;

/**
 * Title:
 * <p>
 * Description:
 * </p>
 *
 * @author javakam
 * @date 2020/11/19  15:54
 */
public class PlayerDialogFactory {

    public static PlaySpeedDialog getFullSpeedDialog(Context context, boolean isFullScreen, int initSpeed,
                                                     ViewGroup.LayoutParams layoutParams, Theme theme,
                                                     OnItemClickListener listener) {
        return new PlaySpeedDialog.Builder(context)
                .setInitSpeed(initSpeed)
                .setLayoutParams(layoutParams)
                .setOnItemClickListener(listener)
                .setIsFullScreen(isFullScreen)
                .setTheme(theme)
                .create();
    }

}