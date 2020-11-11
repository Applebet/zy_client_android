package com.zy.client.ui.splash

import android.content.Intent
import android.os.Bundle
import com.blankj.utilcode.util.ThreadUtils
import com.zy.client.R
import com.zy.client.base.BaseActivity
import com.zy.client.ui.MainActivity
import com.wuhenzhizao.titlebar.statusbar.StatusBarUtils

/**
 * @author javakam
 * @date 2020/6/10 23:20
 */
class SplashActivity : BaseActivity() {
    override fun getLayoutId(): Int = R.layout.activity_splash

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarUtils.transparentStatusBar(window)

        ThreadUtils.getMainHandler().postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 200)
    }
}