package com.zy.client.views.loader

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.zy.client.R
import com.zy.client.utils.ext.noShake

/**
 * Title:LoaderLayout
 *
 * Description: 网络请求加载页,在请求完毕后通过[.setLoadState]设置请求结果
 *
 * @author javakam
 * @date 2019/11/18 14:51
 */
class LoaderLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : Loader(
    context, attrs, defStyle
) {
    init {
        setBackgroundResource(android.R.color.transparent)
    }

    override fun createLoadingView(): View? {
        return inflate(context, R.layout.layout_loader, null)
    }

    override fun createEmptyView(): View? {
        return inflate(context, R.layout.layout_loader_empty, null)
    }

    override fun createErrorView(): View? {
        val view = inflate(context, R.layout.layout_loader_error, null)
        view.findViewById<View>(R.id.btn_reload).noShake {
            reload()
        }
        return view
    }
}