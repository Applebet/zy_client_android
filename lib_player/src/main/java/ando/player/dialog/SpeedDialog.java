package ando.player.dialog;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.util.Log;
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

public class SpeedDialog {

    private final Context context;
    private final ViewGroup viewGroup;

    private BaseDialog dialog;
    private IPlayerCallBack IPlayerCallBack;
    private int initSpeed;
    private boolean isFullScreen = true;
    private Theme theme;

    public SpeedDialog(Context context, ViewGroup viewGroup) {
        this.context = context;
        this.viewGroup = viewGroup;
    }

    private void initContentView(View view) {
        final RadioGroup rg = view.findViewById(R.id.ll_full_definition_contain);
        RadioButton rbSpeed05 = (RadioButton) rg.getChildAt(0);
        RadioButton rbSpeed075 = (RadioButton) rg.getChildAt(1);
        RadioButton rbSpeed10 = (RadioButton) rg.getChildAt(2);
        RadioButton rbSpeed125 = (RadioButton) rg.getChildAt(3);
        RadioButton rbSpeed15 = (RadioButton) rg.getChildAt(4);
        RadioButton rbSpeed20 = (RadioButton) rg.getChildAt(5);
        int currentSpeed = initSpeed;
        switch (currentSpeed) {
            case MediaConstants.PLAYSPEED_05:
                applyItemTheme(rbSpeed05, true);
                break;
            case MediaConstants.PLAYSPEED_075:
                applyItemTheme(rbSpeed075, true);
                break;
            case MediaConstants.PLAYSPEED_10:
                applyItemTheme(rbSpeed10, true);
                break;
            case MediaConstants.PLAYSPEED_125:
                applyItemTheme(rbSpeed125, true);
                break;
            case MediaConstants.PLAYSPEED_15:
                applyItemTheme(rbSpeed15, true);
                break;
            case MediaConstants.PLAYSPEED_20:
                applyItemTheme(rbSpeed20, true);
                break;
            default:
        }
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int speedType = MediaConstants.PLAYSPEED_10;
                float playSpeed = 1f;
                String playSpeedName = context.getString(R.string.str_player_speed75);
                if (checkedId == R.id.speed_05) {
                    playSpeed = 0.5f;
                    speedType = MediaConstants.PLAYSPEED_05;
                    playSpeedName = context.getString(R.string.str_player_speed05);
                } else if (checkedId == R.id.speed_075) {
                    playSpeed = 0.75f;
                    speedType = MediaConstants.PLAYSPEED_075;
                    playSpeedName = context.getString(R.string.str_player_speed75);
                } else if (checkedId == R.id.speed_10) {
                    playSpeed = 1f;
                    speedType = MediaConstants.PLAYSPEED_10;
                    playSpeedName = context.getString(R.string.str_player_speed100);
                } else if (checkedId == R.id.speed_125) {
                    playSpeed = 1.25f;
                    speedType = MediaConstants.PLAYSPEED_125;
                    playSpeedName = context.getString(R.string.str_player_speed125);
                } else if (checkedId == R.id.speed_15) {
                    playSpeed = 1.5f;
                    speedType = MediaConstants.PLAYSPEED_15;
                    playSpeedName = context.getString(R.string.str_player_speed150);
                } else if (checkedId == R.id.speed_20) {
                    speedType = MediaConstants.PLAYSPEED_20;
                    playSpeed = 2.0f;
                    playSpeedName = context.getString(R.string.str_player_speed200);
                }

                Log.i("123", "SpeedX " + playSpeed + "  " + playSpeedName);
                if (IPlayerCallBack != null) {
                    IPlayerCallBack.onSpeedItemClick(speedType, playSpeed, playSpeedName);
                }
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        dialog.setContentView(view);
    }

    public void applyItemTheme(RadioButton rb, boolean isSelect) {
        rb.setChecked(isSelect);
        if (isSelect) {
            //更新主题
            if (theme == Theme.DEFAULT) {
                rb.setTextColor(ContextCompat.getColor(context, R.color.color_player_theme));
            } else if (theme == Theme.Blue) {
                rb.setTextColor(ContextCompat.getColor(context, R.color.player_blue));
            } else if (theme == Theme.Green) {
                rb.setTextColor(ContextCompat.getColor(context, R.color.player_green));
            } else if (theme == Theme.Orange) {
                rb.setTextColor(ContextCompat.getColor(context, R.color.player_orange));
            } else if (theme == Theme.Red) {
                rb.setTextColor(ContextCompat.getColor(context, R.color.player_red));
            } else if (theme == Theme.Blue_20a0FF) {
                rb.setTextColor(ContextCompat.getColor(context, R.color.blue_20a0ff));
            } else if (theme == Theme.Green_76C9FC) {
                rb.setTextColor(ContextCompat.getColor(context, R.color.green_76C9FC));
                //设置为加粗
                rb.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            }
        } else {
            rb.setTextColor(ContextCompat.getColor(context, R.color.color_player_white));
            //设置不为加粗
            rb.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        }
    }

    private void showDialog() {
        // 设置lp
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        if (isFullScreen) {
            dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                View decorView = dialog.getWindow().getDecorView();
                decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            }
            lp.height = PlayerUtils.getScreenHeight(context, false);
            lp.gravity = Gravity.CENTER;
        } else {
            lp.height = this.viewGroup.getLayoutParams().height + (int) PlayerUtils.getStatusBarHeight(context);
            lp.gravity = Gravity.TOP;
        }
        dialog.getWindow().setAttributes(lp);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    public void setTheme(Theme theme) {
        this.theme = theme;
    }

    public void setInitSpeed(int mInitSpeed) {
        this.initSpeed = mInitSpeed;
    }

    public void setIsFullScreen(boolean isFullScreen) {
        this.isFullScreen = isFullScreen;
    }

    public void setIPlayerCallBack(IPlayerCallBack IPlayerCallBack) {
        this.IPlayerCallBack = IPlayerCallBack;
    }

    private void createDialogView() {
        dialog = new BaseDialog(context, R.style.dialog_full_transparent);
        if (isFullScreen) {
            View view = LayoutInflater.from(context).inflate(R.layout.player_dialog_speed_full, null, false);
            view.setOnClickListener(v -> dialog.dismiss());
            initContentView(view);
            showDialog();
        }
    }

    public void dismiss() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }

    public static final class Builder {
        private final Context mContext;
        private final ViewGroup mViewGroup;
        private int mInitSpeed;
        private IPlayerCallBack mListener;
        private boolean mIsFullScreen;
        private Theme theme;

        public Builder(ViewGroup viewGroup) {
            this.mContext = viewGroup.getContext();
            this.mViewGroup = viewGroup;
        }

        public Builder setInitSpeed(int initSpeed) {
            this.mInitSpeed = initSpeed;
            return this;
        }

        public Builder setOnItemClickListener(IPlayerCallBack listener) {
            this.mListener = listener;
            return this;
        }

        public Builder setIsFullScreen(boolean isFullScreen) {
            this.mIsFullScreen = isFullScreen;
            return this;
        }


        public Builder setTheme(Theme theme) {
            this.theme = theme;
            return this;
        }

        public SpeedDialog build() {
            final SpeedDialog dialog = new SpeedDialog(mContext, mViewGroup);
            dialog.setInitSpeed(mInitSpeed);
            dialog.setIPlayerCallBack(mListener);
            dialog.setIsFullScreen(mIsFullScreen);
            dialog.setTheme(theme);
            dialog.createDialogView();
            return dialog;
        }
    }

}