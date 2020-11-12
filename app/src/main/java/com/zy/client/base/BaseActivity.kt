package com.zy.client.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * @author javakam
 * @date 2020/6/8 16:33
 */
abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())
        initView()
        initListener()
        initData()
    }

    open fun initView() {
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