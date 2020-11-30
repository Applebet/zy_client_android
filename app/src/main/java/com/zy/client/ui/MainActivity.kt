package com.zy.client.ui

import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.util.SparseArray
import android.view.MenuItem
import android.view.ViewGroup
import androidx.core.util.forEach
import androidx.core.view.children
import androidx.fragment.app.Fragment
import com.zy.client.App
import com.zy.client.R
import com.zy.client.base.BaseActivity
import com.zy.client.base.BaseFragment
import com.zy.client.ui.collect.CollectFragment
import com.zy.client.ui.home.HomeFragment
import com.zy.client.ui.iptv.IPTVFragment
import com.zy.client.utils.NoShakeClickListener2
import com.zy.client.utils.ext.ToastUtils
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    private val fragmentArray = SparseArray<BaseFragment>(3)

    var mHits = LongArray(2)

    override fun getLayoutId(): Int = R.layout.activity_main

    //问题: MainActivity使用的启动模式是SingleTask，我将闪屏页去掉后，无论打开多少页面，将应用推至后台再启动就回到了主页（MainActivity）
    //郭霖公众号: https://mp.weixin.qq.com/s?__biz=MzA5MzI3NjE2MA==&mid=2650253197&idx=1&sn=e9986456f709f00fb2d36940e1c18b30
    override fun onCreate(savedInstanceState: Bundle?) {
        if (!this.isTaskRoot) { // 当前类不是该Task的根部，那么之前启动
            val intent = intent
            if (intent != null) {
                val action = intent.action
                if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN == action) { // 当前类是从桌面启动的
                    // finish掉该类，直接打开该Task中现存的Activity
                    finish()
                    return
                }
            }
        }
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        super.initView()
        fragmentArray.put(R.id.navigation_home, HomeFragment())
        fragmentArray.put(R.id.navigation_iptv, IPTVFragment())
        fragmentArray.put(R.id.navigation_collect, CollectFragment())
        supportFragmentManager
            .beginTransaction()
            .apply {
                fragmentArray.forEach { key, value ->
                    add(R.id.container, value, key.toString())
                }
            }
            .commitAllowingStateLoss()
        switchPage(R.id.navigation_home)
    }

    override fun initListener() {
        super.initListener()
        //屏蔽长按吐司
        (navView.getChildAt(0) as? ViewGroup)?.children?.forEach { it.setOnLongClickListener { true } }

        //快速点击事件
        val fastClick = object : NoShakeClickListener2() {
            override fun onFastClick(item: Any?) {
                super.onFastClick(item)
                //Log.e("123", "onFastClick Click")
                (item as? MenuItem?)?.apply {
                    val fg = fragmentArray.get(itemId)
                    if (fg.isAdded) fg.refreshData()
                }
            }
        }

        navView.setOnNavigationItemSelectedListener {
            switchPage(it.itemId)
            fastClick.proceedClick(it)
            true
        }
    }

    private fun switchPage(id: Int) {
        supportFragmentManager
            .beginTransaction()
            .apply {
                fragmentArray.forEach { key, value ->
                    if (key == id) show(value) else hide(value)
                }
            }.commitAllowingStateLoss()
    }

    override fun onBackPressed() {
        System.arraycopy(mHits, 1, mHits, 0, mHits.size - 1)
        mHits[mHits.size - 1] = SystemClock.uptimeMillis()
        if (mHits[0] >= (SystemClock.uptimeMillis() - 1000)) {
            App.instance.exitSys()
            super.onBackPressed()
        } else {
            ToastUtils.showShort("再按一次退出程序")
        }
    }

}