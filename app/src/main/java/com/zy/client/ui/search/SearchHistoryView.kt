package com.zy.client.ui.search

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import co.lujun.androidtagview.TagView
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
                tagGroup.tags = emptyList()
            }
        }

        tagGroup.setOnTagClickListener(object : TagView.OnTagClickListener {
            override fun onTagClick(position: Int, text: String?) {
                if (!text.isNullOrBlank()) {
                    onSelectListener?.invoke(text)
                }
            }

            override fun onTagLongClick(position: Int, text: String?) {
            }

            override fun onSelectedTagDrag(position: Int, text: String?) {
            }

            override fun onTagCrossClick(position: Int) {
            }
        })

        contentView.alpha = 1f
    }

    var onSelectListener: ((searchWord: String) -> Unit)? = null

    fun updateHistory() {
        SearchHistoryDBUtils.searchAllAsync {
            if (!it.isNullOrEmpty()) {
                tagGroup.tags = it.map { model -> model.searchWord }
            }
        }
    }

}