package ando.player.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.core.content.ContextCompat;

import com.dueeeke.videoplayer.util.PlayerUtils;

import ando.player.R;
import ando.player.setting.MediaConstants;
import ando.player.setting.Theme;

public class PlaySpeedDialog {

    private final Context mContext;
    private final ViewGroup.LayoutParams layoutParams;

    private BaseDialog baseDialog;
    private OnItemClickListener onItemClickListener;
    private int initSpeed;
    private boolean isFullScreen = true;
    private Theme mTheme;

    public PlaySpeedDialog(Context context, ViewGroup.LayoutParams mLayoutParams) {
        this.mContext = context;
        this.layoutParams = mLayoutParams;
    }

    public BaseDialog show() {
        return baseDialog;
    }

    private void setSpeedData(View view, final Dialog dialog) {
        final RadioGroup rg = view.findViewById(R.id.ll_full_definition_contain);
        RadioButton rbSpeed05 = (RadioButton) rg.getChildAt(0);
        RadioButton rbSpeed075 = (RadioButton) rg.getChildAt(1);
        RadioButton rbSpeed10 = (RadioButton) rg.getChildAt(2);
        RadioButton rbSpeed12 = (RadioButton) rg.getChildAt(3);
        RadioButton rbSpeed15 = (RadioButton) rg.getChildAt(4);
        RadioButton rbSpeed20 = (RadioButton) rg.getChildAt(5);
        int currentSpeed = initSpeed;
        switch (currentSpeed) {
            case MediaConstants.PLAYSPEED_05:
                setRes(rbSpeed05, true);
                break;
            case MediaConstants.PLAYSPEED_075:
                setRes(rbSpeed075, true);
                break;
            case MediaConstants.PLAYSPEED_10:
                setRes(rbSpeed10, true);
                break;
            case MediaConstants.PLAYSPEED_125:
                setRes(rbSpeed12, true);
                break;
            case MediaConstants.PLAYSPEED_15:
                setRes(rbSpeed15, true);
                break;
            case MediaConstants.PLAYSPEED_20:
                setRes(rbSpeed20, true);
                break;
            default:
        }
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int speedType = MediaConstants.PLAYSPEED_10;
                float playSpeed = 1f;
                String playSpeedName = mContext.getString(R.string.str_player_speed75);
                if (checkedId == R.id.speed_05) {
                    playSpeed = 0.5f;
                    speedType = MediaConstants.PLAYSPEED_05;
                    playSpeedName = mContext.getString(R.string.str_player_speed05);
                } else if (checkedId == R.id.speed_075) {
                    playSpeed = 0.75f;
                    speedType = MediaConstants.PLAYSPEED_075;
                    playSpeedName = mContext.getString(R.string.str_player_speed75);
                } else if (checkedId == R.id.speed_10) {
                    playSpeed = 1f;
                    speedType = MediaConstants.PLAYSPEED_10;
                    playSpeedName = mContext.getString(R.string.str_player_speed100);
                } else if (checkedId == R.id.speed_125) {
                    playSpeed = 1.2f;
                    speedType = MediaConstants.PLAYSPEED_125;
                    playSpeedName = mContext.getString(R.string.str_player_speed125);
                } else if (checkedId == R.id.speed_15) {
                    playSpeed = 1.5f;
                    speedType = MediaConstants.PLAYSPEED_15;
                    playSpeedName = mContext.getString(R.string.str_player_speed150);
                } else if (checkedId == R.id.speed_20) {
                    speedType = MediaConstants.PLAYSPEED_20;
                    playSpeed = 2.0f;
                    playSpeedName = mContext.getString(R.string.str_player_speed200);
                }
                if (onItemClickListener != null) {
                    onItemClickListener.onSpeedItemClick(speedType, playSpeed, playSpeedName);
                }
                dialog.dismiss();
            }
        });
    }

    public void setRes(RadioButton radioButton, boolean isSelect) {
        radioButton.setChecked(isSelect);
        if (isSelect) {
            setTheme(radioButton);
        } else {
            radioButton.setTextColor(ContextCompat.getColor(mContext, R.color.color_player_white));
            //设置不为加粗
            radioButton.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        }
    }

    public void setTheme(RadioButton radioButton) {
        //更新主题
        if (mTheme == Theme.DEFAULT) {
            radioButton.setTextColor(ContextCompat.getColor(mContext, R.color.color_player_theme));
        } else if (mTheme == Theme.Blue) {
            radioButton.setTextColor(ContextCompat.getColor(mContext, R.color.player_blue));
        } else if (mTheme == Theme.Green) {
            radioButton.setTextColor(ContextCompat.getColor(mContext, R.color.player_green));
        } else if (mTheme == Theme.Orange) {
            radioButton.setTextColor(ContextCompat.getColor(mContext, R.color.player_orange));
        } else if (mTheme == Theme.Red) {
            radioButton.setTextColor(ContextCompat.getColor(mContext, R.color.player_red));
        } else if (mTheme == Theme.Blue_20a0FF) {
            radioButton.setTextColor(ContextCompat.getColor(mContext, R.color.blue_20a0ff));
        } else if (mTheme == Theme.Green_76C9FC) {
            radioButton.setTextColor(ContextCompat.getColor(mContext, R.color.green_76C9FC));
            //设置为加粗
            radioButton.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        }
    }

    private void initDialog(Dialog definitionDialog, View view) {
        definitionDialog.setContentView(view);
        // 设置lp
        WindowManager.LayoutParams lp = definitionDialog.getWindow().getAttributes();
        if (isFullScreen) {
            definitionDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                View decorView = definitionDialog.getWindow().getDecorView();
                decorView.setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

            }
            lp.height = PlayerUtils.getScreenHeight(mContext, false);
            //definitionDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            lp.gravity = Gravity.CENTER;
        } else {
            lp.height = this.layoutParams.height + (int) PlayerUtils.getStatusBarHeight(mContext);
            lp.gravity = Gravity.TOP;
        }
        definitionDialog.getWindow().setAttributes(lp);
        definitionDialog.setCanceledOnTouchOutside(true);
        definitionDialog.show();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    private void setInitSpeed(int mInitSpeed) {
        this.initSpeed = mInitSpeed;
    }

    public void setIsFullScreen(boolean isFullScreen) {
        this.isFullScreen = isFullScreen;
    }

    public static final class Builder {
        private final Context mContext;
        private int mInitSpeed;
        private OnItemClickListener mListener;
        private boolean mIsFullScreen;
        private ViewGroup.LayoutParams mLayoutParams;
        private Theme theme;

        public Builder(Context context) {
            this.mContext = context;
        }

        public Builder setInitSpeed(int initSpeed) {
            this.mInitSpeed = initSpeed;
            return this;
        }

        public Builder setOnItemClickListener(OnItemClickListener listener) {
            this.mListener = listener;
            return this;
        }

        public Builder setIsFullScreen(boolean isFullScreen) {
            this.mIsFullScreen = isFullScreen;
            return this;
        }

        public Builder setLayoutParams(ViewGroup.LayoutParams layoutParams) {
            this.mLayoutParams = layoutParams;
            return this;
        }

        public Builder setTheme(Theme theme) {
            this.theme = theme;
            return this;
        }

        public PlaySpeedDialog create() {
            PlaySpeedDialog dialog = new PlaySpeedDialog(mContext, mLayoutParams);
            dialog.setInitSpeed(mInitSpeed);
            dialog.setOnItemClickListener(mListener);
            dialog.setIsFullScreen(mIsFullScreen);
            dialog.setTheme(theme);
            dialog.createDialogView();
            return dialog;
        }
    }

    private void createDialogView() {
        baseDialog = new BaseDialog(mContext, R.style.dialog_full_transparent);
        View view;
        if (isFullScreen) {
            view = LayoutInflater.from(mContext).inflate(R.layout.player_dialog_speed_full, null);
            setSpeedData(view, baseDialog);
            view.setOnClickListener(v -> baseDialog.dismiss());
            initDialog(baseDialog, view);
        }
    }

    private void setTheme(Theme theme) {
        this.mTheme = theme;
    }

}