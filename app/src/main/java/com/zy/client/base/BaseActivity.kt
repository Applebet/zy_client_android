package com.zy.client.base

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.zy.client.R
import com.zy.client.utils.Utils
import com.zy.client.utils.status.StatusBarUtils

abstract class BaseActivity : AppCompatActivity() {

    //系统 DecorView 的根View
    protected lateinit var mView: ViewGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())
        mView = findViewById(android.R.id.content)
        initStyle()
        initView(savedInstanceState)
        initListener()
        initData()
    }

    open fun initStyle(statusBarColor: Int = android.R.color.white) {
        StatusBarUtils.transparentStatusBar(window)
        StatusBarUtils.setDarkMode(window)
        StatusBarUtils.setStatusBarView(this, android.R.color.white)
    }

    open fun initView(savedInstanceState: Bundle?) {
    }

    open fun initListener() {
    }

    open fun initData() {
    }

    abstract fun getLayoutId(): Int

    override fun onBackPressed() {
        supportFragmentManager.fragments.forEach {
            it?.let {
                if ((it as? IBackPressed)?.onBackPressed() == true) {
                    return
                }
            }
        }
        super.onBackPressed()
    }
}