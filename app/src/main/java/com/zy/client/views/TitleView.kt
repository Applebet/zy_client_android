package com.zy.client.views

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.zy.client.R

/**
 * Title: TitleView
 * <p>
 * Description:
 * </p>
 * @author javakam
 * @date 2020/12/1  15:07
 */
class TitleView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {

    private var mIvLeft: ImageView
    private var mIvRight: ImageView
    private var mTvTitle: TextView

    init {
        val view = inflate(context, R.layout.layout_title_view, null)
        mIvLeft = view.findViewById(R.id.iv_title_back) as ImageView
        mIvRight = view.findViewById(R.id.iv_title_right) as ImageView
        mTvTitle = view.findViewById<View>(R.id.tv_title) as TextView

        mIvLeft.setOnClickListener {
            val activity = context as Activity?
            activity?.onBackPressed()
        }
        this.addView(view)
    }

    /**
     * 设置中央标题名称
     */
    fun setTitle(text: String?) {
        mTvTitle.visibility = if (text.isNullOrBlank()) GONE else VISIBLE
        mTvTitle.text = text
    }

    /**
     * 获取返回控件
     */
    fun getLeftView(): ImageView {
        mIvLeft.visibility = VISIBLE
        return mIvLeft
    }

}