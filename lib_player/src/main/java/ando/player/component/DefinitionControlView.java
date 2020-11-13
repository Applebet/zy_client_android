package ando.player.component;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dueeeke.videoplayer.player.VideoView;
import com.dueeeke.videoplayer.util.L;
import com.dueeeke.videoplayer.util.PlayerUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import ando.player.R;

public class DefinitionControlView extends AndoVodControlView {

    private final TextView mDefinition;
    private final PopupWindow mPopupWindow;
    private final LinearLayout mPopLayout;
    private List<String> mRateStr;

    private int mCurIndex;

    private LinkedHashMap<String, String> mMultiRateData;

    private OnRateSwitchListener mOnRateSwitchListener;

    public DefinitionControlView(@NonNull Context context) {
        super(context);
    }

    public DefinitionControlView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DefinitionControlView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    {
        mPopupWindow = new PopupWindow(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopLayout = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.layout_ando_player_rate_pop, this, false);
        mPopupWindow.setContentView(mPopLayout);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(0xffcccccc));
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setClippingEnabled(false);
        mDefinition = findViewById(R.id.tv_definition);
        mDefinition.setVisibility(VISIBLE);
        mDefinition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRateMenu();
            }
        });
    }

    private void showRateMenu() {
        mPopLayout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        mPopupWindow.showAsDropDown(mDefinition, -((mPopLayout.getMeasuredWidth() - mDefinition.getMeasuredWidth()) / 2),
                -(mPopLayout.getMeasuredHeight() + mDefinition.getMeasuredHeight() + PlayerUtils.dp2px(getContext(), 6)));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_ando_player_definition_control_view;
    }

    @Override
    public void onVisibilityChanged(boolean isVisible, Animation anim) {
        super.onVisibilityChanged(isVisible, anim);
        if (!isVisible) {
            mPopupWindow.dismiss();
        }
    }

    @Override
    public void onPlayerStateChanged(int playerState) {
        super.onPlayerStateChanged(playerState);
        if (playerState == VideoView.PLAYER_FULL_SCREEN) {
            mDefinition.setVisibility(VISIBLE);
        } else {
            //mDefinition.setVisibility(GONE);
            mDefinition.setVisibility(VISIBLE);
            mPopupWindow.dismiss();
        }
    }

    public void setData(LinkedHashMap<String, String> multiRateData) {
        mMultiRateData = multiRateData;
        if (mDefinition != null && TextUtils.isEmpty(mDefinition.getText())) {
            L.d("DefinitionControlView multiRate");
            if (multiRateData == null) {
                return;
            }
            mRateStr = new ArrayList<>();
            int index = 0;
            ListIterator<Map.Entry<String, String>> iterator = new ArrayList<>(multiRateData.entrySet()).listIterator(multiRateData.size());
            while (iterator.hasPrevious()) {//反向遍历
                Map.Entry<String, String> entry = iterator.previous();
                mRateStr.add(entry.getKey());
                final TextView rateItem = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.layout_ando_player_rate_item, null);
                rateItem.setText(entry.getKey());
                rateItem.setTag(index);
                rateItem.setOnClickListener(rateOnClickListener);
                mPopLayout.addView(rateItem);
                index++;
            }

//            ((TextView) mPopLayout.getChildAt(index - 1)).setTextColor(getResources().getColor(R.color.ando_player_theme_color));
//            mDefinition.setText(mRateStr.get(index - 1));
            mCurIndex = index - 1;
        }
    }

    public void setCurrentIndex(int mCurIndex) {
        if (mMultiRateData == null || mMultiRateData.isEmpty()) {
            return;
        }
        if (mCurIndex < 1) {//至少为1
            return;
        }

        if (mDefinition != null && mCurIndex < mMultiRateData.size()) {
            this.mCurIndex = mCurIndex - 1;
            final TextView rateItem = (TextView) mPopLayout.getChildAt(mCurIndex - 1);
            rateItem.setTextColor(getResources().getColor(R.color.dkplayer_theme_color));
            mDefinition.setText(mRateStr.get(mCurIndex - 1));
        } else {
            this.mCurIndex = 0;
            final TextView rateItem = (TextView) mPopLayout.getChildAt(0);
            rateItem.setTextColor(getResources().getColor(R.color.dkplayer_theme_color));
            if (mDefinition != null) {
                mDefinition.setText(mRateStr.get(0));
            }
        }
    }

    private final View.OnClickListener rateOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            int index = (int) v.getTag();
            if (mCurIndex == index) {
                return;
            }
            ((TextView) mPopLayout.getChildAt(mCurIndex)).setTextColor(Color.BLACK);
            ((TextView) mPopLayout.getChildAt(index)).setTextColor(getResources().getColor(R.color.dkplayer_theme_color));
            mDefinition.setText(mRateStr.get(index));
            switchDefinition(mRateStr.get(index));
            mPopupWindow.dismiss();
            mCurIndex = index;
        }
    };

    private void switchDefinition(String s) {
        mControlWrapper.hide();
        mControlWrapper.stopProgress();
        String url = mMultiRateData.get(s);
        if (mOnRateSwitchListener != null) {
            mOnRateSwitchListener.onRateChange(url);
        }
    }

    public LinkedHashMap<String, String> getMultiRateData() {
        return mMultiRateData;
    }

    public interface OnRateSwitchListener {
        void onRateChange(String url);
    }

    public void setOnRateSwitchListener(OnRateSwitchListener onRateSwitchListener) {
        mOnRateSwitchListener = onRateSwitchListener;
    }

}