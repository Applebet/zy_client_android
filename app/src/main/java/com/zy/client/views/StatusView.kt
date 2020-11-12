package com.zy.client.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.isVisible
import com.zy.client.R
import com.zy.client.utils.ext.gone
import com.zy.client.utils.ext.invisible
import com.zy.client.utils.ext.visible
import kotlinx.android.synthetic.main.status_view_layout.view.*

/**
 * @author javakam
 * @date 2020/6/9 17:10
 */
class StatusView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    var failRetryClickListener: (() -> Unit)? = null

    var toWebClickListener: (() -> Unit)? = null

    init {

        View.inflate(context, R.layout.status_view_layout, this)

        setOnClickListener {
            if (statusFailView.isVisible) {
                failRetryClickListener?.invoke()
            }
        }

        tvToWeb.setOnClickListener {
            toWebClickListener?.invoke()
        }

        if (toWebClickListener == null) {
            tvToWeb.gone()
        }

        gone()
    }

    fun setLoadingStatus() {
        visible()
        statusLoadingView.visible()
        statusFailView.invisible()
        statusEmptyView.invisible()
    }

    fun setFailStatus() {
        visible()
        statusLoadingView.invisible()
        statusFailView.visible()
        statusEmptyView.invisible()
    }

    fun setEmptyStatus() {
        visible()
        statusLoadingView.invisible()
        statusFailView.invisible()
        statusEmptyView.visible()
    }

    fun setEmptyStatus(emptyMsg: String) {
        visible()
        statusLoadingView.invisible()
        statusFailView.invisible()
        statusEmptyView.visible()
        statusEmptyView.text = emptyMsg
    }

    fun setSuccessStatus() {
        gone()
    }
}