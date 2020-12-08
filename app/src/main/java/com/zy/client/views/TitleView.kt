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
 */
class TitleView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {

    private var mIvLeft: ImageView
    private var mIvRight: ImageView
    private var mTvTitle: TextView
    private var mTvRight: TextView

    init {
        val view = inflate(context, R.layout.layout_title_view, null)
        mIvLeft = view.findViewById(R.id.iv_title_back) as ImageView
        mIvRight = view.findViewById(R.id.iv_title_right) as ImageView
        mTvTitle = view.findViewById<View>(R.id.tv_title) as TextView
        mTvRight = view.findViewById<View>(R.id.tv_title_right) as TextView

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

    fun getRightText(): TextView {
        mIvRight.visibility = GONE
        mTvRight.visibility = VISIBLE
        return mTvRight
    }

    fun getRightImage(): ImageView {
        mTvRight.visibility = GONE
        mIvRight.visibility = VISIBLE
        return mIvRight
    }

}