package com.zy.client.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.zy.client.R
import com.zy.client.utils.ext.gone
import com.zy.client.utils.ext.textOrDefault
import com.zy.client.utils.ext.visible
import kotlinx.android.synthetic.main.set_item_view.view.*

/**
 * @author javakam
 *
 * @date 2020/9/17 17:33
 * @desc 设置的单条目
 */
class SetItemView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    var itemName: String? = null
        set(value) {
            field = value
            tvName?.text = value.textOrDefault()
        }

    init {
        View.inflate(context, R.layout.set_item_view, this)

        val ta = context.obtainStyledAttributes(attrs, R.styleable.SetItemView)
        tvName.text = ta.getString(R.styleable.SetItemView_itemName)
        if (ta.getBoolean(R.styleable.SetItemView_needDivideLine, true)) {
            divideLine.visible()
        } else {
            divideLine.gone()
        }

        val needRightArrow = ta.getBoolean(R.styleable.SetItemView_needRightArrow, false)
        val needSwitchBtn = ta.getBoolean(R.styleable.SetItemView_needSwitchBtn, false)

        //右侧箭头图标与开关互斥
        if (needRightArrow) {
            ivArrowRight.visible()
            switchBtn.gone()
        } else if (needSwitchBtn) {
            ivArrowRight.gone()
            switchBtn.visible()
        }
        ta.recycle()
    }

}