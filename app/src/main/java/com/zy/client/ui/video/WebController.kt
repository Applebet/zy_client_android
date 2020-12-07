package com.zy.client.ui.video

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.net.http.SslError
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.webkit.SslErrorHandler
import android.webkit.WebView
import android.widget.FrameLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.just.agentweb.AgentWeb
import com.just.agentweb.BaseIndicatorView
import com.zy.client.R
import com.zy.client.utils.ext.gone
import com.zy.client.utils.ext.visible

/**
 * 网页控制器
 *
 * @author javakam
 */
class WebController {
    private var agentWeb: AgentWeb? = null
    fun loadUrl(activity: Activity, url: String?, container: ViewGroup) {
        loadUrl(AgentWeb.with(activity), url, container)
    }

    fun loadUrl(fragment: Fragment, url: String?, container: ViewGroup) {
        loadUrl(AgentWeb.with(fragment), url, container)
    }

    private fun loadUrl(agentBuilder: AgentWeb.AgentBuilder, url: String?, container: ViewGroup) {
        agentWeb = agentBuilder.setAgentWebParent(
            container,
            ViewGroup.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        )
            .setCustomIndicator(CustomIndicator(container.context))
            .setWebViewClient(getWebViewClient())
            .createAgentWeb()
            .ready()
            .go(url)
            .apply {
                webCreator.webView.setBackgroundColor(Color.BLACK)
            }
    }

    fun onPause() {
        agentWeb?.webLifeCycle?.onPause()
    }

    fun onResume() {
        agentWeb?.webLifeCycle?.onResume()
    }

    fun onDestroy() {
        agentWeb?.webLifeCycle?.onDestroy()
    }

    fun onBackPressed(): Boolean {
        return agentWeb?.back() == true
    }

    private fun getWebViewClient(): com.just.agentweb.WebViewClient {
        return object : com.just.agentweb.WebViewClient() {
            override fun onReceivedSslError(
                view: WebView?,
                handler: SslErrorHandler,
                error: SslError?
            ) {
                handler.proceed()
            }
        }
    }

    inner class CustomIndicator @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
    ) : BaseIndicatorView(context, attrs, defStyleAttr) {
        private var tvProgress: TextView

        init {
            val view = View.inflate(context, R.layout.layout_web_indicator, this)
            tvProgress = view.findViewById(R.id.tvProgress)
        }

        override fun offerLayoutParams(): LayoutParams {
            return LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        override fun show() {
            super.show()
            visible()
        }

        override fun hide() {
            super.hide()
            gone()
        }

        @SuppressLint("SetTextI18n")
        override fun setProgress(newProgress: Int) {
            super.setProgress(newProgress)
            tvProgress.text = "$newProgress%"
        }

        override fun reset() {
            super.reset()
            tvProgress.text = "加载中..."
        }
    }

}