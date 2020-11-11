package com.zy.client.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.zy.client.R
import com.zy.client.utils.ext.gone
import com.zy.client.utils.ext.visible
import kotlinx.android.synthetic.main.video_select_panel.view.*

/**
 * @author javakam
 *
 * @date 2020/9/19 12:35
 * @desc 播放器的选择面板
 */

abstract class BaseVideoSelectPanel<T> @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    val myAdapter by lazy { getAdapter() }

    init {
        View.inflate(context, R.layout.video_select_panel, this)

        rvSelect.run {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = myAdapter
        }
        setOnClickListener {
            gone()
        }
    }

    abstract fun getAdapter(): BaseQuickAdapter<T, BaseViewHolder>

    fun setOnItemClickListener(onItemClickListener: (T?, pos: Int) -> Unit) {

        myAdapter.setOnItemClickListener { adapter, view, position ->
            gone()
            onItemClickListener.invoke(adapter.data[position] as? T, position)
        }
    }

    fun setData(dataList: ArrayList<T>) {
        visible()
        myAdapter.setNewInstance(dataList)
    }
}