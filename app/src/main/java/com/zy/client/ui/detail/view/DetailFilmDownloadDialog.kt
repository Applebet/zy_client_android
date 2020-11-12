package com.zy.client.ui.detail.view

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.zy.client.utils.ext.ToastUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.zy.client.App
import com.zy.client.R
import com.zy.client.ui.detail.model.FilmItemInfo
import com.zy.client.utils.ClipboardUtils
import com.zy.client.utils.Utils
import kotlinx.android.synthetic.main.dialog_sheet_detail_download.view.*

/**
 * @author javakam
 *
 * @date 2020/6/17 15:24
 */
const val TAG_DOWNLOAD_DIALOG = "tag_download_dialog"

class DetailFilmDownloadDialog : BottomSheetDialogFragment() {
    private lateinit var rootView: View

    private lateinit var dataList: ArrayList<FilmItemInfo>


    fun show(dataList: ArrayList<FilmItemInfo>?, fragmentManager: FragmentManager) {
        if (dataList.isNullOrEmpty()) {
            ToastUtils.showShort("暂不支持下载")
            return
        }
        this.dataList = dataList
        show(fragmentManager, TAG_DOWNLOAD_DIALOG)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        rootView = View.inflate(requireActivity(), R.layout.dialog_sheet_detail_download, null)
        dialog.setContentView(rootView)
        //透明
        dialog.findViewById<View>(R.id.design_bottom_sheet)
            .setBackgroundColor(ContextCompat.getColor(App.instance, R.color.transparent))

        rootView.rvDownload.run {
            layoutManager = LinearLayoutManager(activity)
            adapter = MyAdapter()
        }

        rootView.ivClose.setOnClickListener {
            dismiss()
        }
        return dialog

    }


    inner class MyAdapter :
        BaseQuickAdapter<FilmItemInfo, BaseViewHolder>(
            R.layout.dialog_sheet_detail_download_item,
            dataList
        ) {
        override fun convert(holder: BaseViewHolder, item: FilmItemInfo) {
            holder.setText(R.id.tvFilm, item.name)

            holder.getView<View>(R.id.ivDownload).setOnClickListener {
                if (item.videoUrl.isBlank()) {
                    ToastUtils.showShort("下载地址为空")
                    return@setOnClickListener
                }
                ClipboardUtils.copyText(item.videoUrl)
                Utils.openBrowser(requireActivity(), item.videoUrl)
                ToastUtils.showShort("已复制下载地址到剪贴板")
            }
        }

    }
}