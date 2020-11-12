package com.zy.client.ui.search

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.zy.client.R
import com.zy.client.database.SearchHistoryDBUtils
import kotlinx.android.synthetic.main.layout_search_history.view.*

/**
 * 搜索历史
 *
 * @author javakam
 */
class SearchHistoryView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    init {
        // 添加Popup窗体内容View
        val contentView =
            LayoutInflater.from(context).inflate(R.layout.layout_search_history, this, false)
        // 事先隐藏，等测量完毕恢复，避免View影子跳动现象。
        contentView.alpha = 0f
        addView(contentView)

        ivDelete.setOnClickListener {
            //清除全部记录
            if (SearchHistoryDBUtils.deleteAll()) {
                tagGroup.setTags(arrayListOf())
                statusView.setEmptyStatus()
            }
        }

        tagGroup.setOnTagClickListener {
            onSelectListener?.invoke(it)
        }

        contentView.alpha = 1f
    }

    var onSelectListener: ((searchWord: String) -> Unit)? = null

    fun updateHistory() {
        statusView.setLoadingStatus()

        SearchHistoryDBUtils.searchAllAsync {
            if (it.isNullOrEmpty()) {
                statusView.setEmptyStatus("没有记录")
            } else {
                tagGroup.setTags(it.map { model -> model.searchWord })

                statusView.setSuccessStatus()
            }
        }
    }

}