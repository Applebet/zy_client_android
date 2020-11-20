package ando.player.dialog;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dueeeke.videoplayer.util.PlayerUtils;

import java.util.List;
import java.util.Locale;

import ando.player.R;

/**
 * 选集
 */
public class VideoListDialog {

    private final Context context;
    private final ViewGroup viewGroup;

    private boolean isFullScreen = true;
    private BaseDialog dialog;
    private List<String> data;
    private int currPosition;
    private IPlayerCallBack callBack;

    public VideoListDialog(Context context, ViewGroup viewGroup) {
        this.context = context;
        this.viewGroup = viewGroup;
    }

    private void initContentView(View view) {
        final TextView tvList = view.findViewById(R.id.tv_video_list);
        tvList.setText(String.format(Locale.getDefault(), context.getString(R.string.str_player_list_size), data.size()));

        final RecyclerView rv = view.findViewById(R.id.rv_video_list);
        rv.setHasFixedSize(true);
        rv.setItemAnimator(null);
        rv.setLayoutManager(new GridLayoutManager(context, 4));

        final int padding = context.getResources().getDimensionPixelSize(R.dimen.dimen_player_controller_icon_padding_s);
        rv.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.set(0, padding, padding, padding);
            }
        });

        VideoListAdapter adapter = new VideoListAdapter();
        adapter.setData(data, currPosition);
        adapter.setCallBack(callBack);
        rv.setAdapter(adapter);

        dialog.setContentView(view);
    }

    private void showDialog() {
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

    public void setIsFullScreen(boolean isFullScreen) {
        this.isFullScreen = isFullScreen;
    }

    private void setData(List<String> data) {
        this.data = data;
    }

    private void setPosition(int position) {
        this.currPosition = position;
    }

    public void setCallBack(IPlayerCallBack callBack) {
        this.callBack = callBack;
    }

    private void createDialogView() {
        dialog = new BaseDialog(context, R.style.dialog_full_transparent);
        if (isFullScreen) {
            View view = LayoutInflater.from(context).inflate(R.layout.player_dialog_video_list, null, false);
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

    static class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.VideoListHolder> {

        private IPlayerCallBack mCallBack;
        private List<String> mData;
        private int mPosition = -1;

        public void onBind(VideoListHolder holder, String item, int position) {
        }

        public void setData(List<String> data, int position) {
            this.mData = data;
            this.mPosition = position;
            notifyDataSetChanged();
        }

        public void replaceData(List<String> data) {
            this.mData.clear();
            this.mData.addAll(data);
            notifyDataSetChanged();
        }

        public void setCallBack(IPlayerCallBack callBack) {
            this.mCallBack = callBack;
        }

        @NonNull
        @Override
        public VideoListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new VideoListHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.player_dialog_video_list_item,
                    null, false));
        }

        @Override
        public void onBindViewHolder(@NonNull final VideoListHolder holder, final int position) {
            final String item = mData.get(position);
            onBind(holder, item, position);
            if (mPosition != -1 && mPosition == position) {
                holder.flContainer.setSelected(true);
                holder.tvItem.setSelected(true);
            }else {
                holder.flContainer.setSelected(false);
                holder.tvItem.setSelected(false);
            }

            holder.tvItem.setText(item);
            holder.flContainer.setOnClickListener(v -> {
                if (mCallBack != null) {
                    holder.flContainer.setSelected(true);
                    holder.tvItem.setSelected(true);
                    mPosition = position;

                    mCallBack.onListItemClick(item, position);
                }
            });
        }

        @Override
        public int getItemCount() {
            return (mData == null || mData.isEmpty()) ? 0 : mData.size();
        }

        static class VideoListHolder extends RecyclerView.ViewHolder {

            protected FrameLayout flContainer;
            protected TextView tvItem;

            public VideoListHolder(@NonNull View itemView) {
                super(itemView);
                flContainer = itemView.findViewById(R.id.fl_video_list_item);
                tvItem = itemView.findViewById(R.id.tv_video_list_item);
            }
        }
    }

    public static final class Builder {
        private final Context mContext;
        private final ViewGroup mViewGroup;
        private List<String> mData;
        private int mCurrPosition;
        private IPlayerCallBack mListener;
        private boolean mIsFullScreen;

        public Builder(ViewGroup viewGroup) {
            this.mContext = viewGroup.getContext();
            this.mViewGroup = viewGroup;
        }

        public Builder setData(List<String> data) {
            this.mData = data;
            return this;
        }

        public Builder setPosition(int position) {
            this.mCurrPosition = position;
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

        public VideoListDialog build() {
            final VideoListDialog dialog = new VideoListDialog(mContext, mViewGroup);
            dialog.setData(mData);
            dialog.setPosition(mCurrPosition);
            dialog.setCallBack(mListener);
            dialog.setIsFullScreen(mIsFullScreen);
            dialog.createDialogView();
            return dialog;
        }

    }

}