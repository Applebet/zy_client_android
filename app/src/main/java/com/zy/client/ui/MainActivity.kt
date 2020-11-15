package com.zy.client.ui

import android.os.SystemClock
import android.util.SparseArray
import androidx.core.util.forEach
import androidx.fragment.app.Fragment
import com.zy.client.utils.ext.ToastUtils
import com.zy.client.R
import com.zy.client.base.BaseActivity
import com.zy.client.ui.collect.CollectFragment
import com.zy.client.ui.home.HomeFragment
import com.zy.client.ui.iptv.IPTVFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    private val fragmentArray = SparseArray<Fragment>(2)

    var mHits = LongArray(2)

    override fun getLayoutId(): Int = R.layout.activity_main

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
        navView.setOnNavigationItemSelectedListener {
            switchPage(it.itemId)
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
        System.arraycopy(mHits, 1, mHits, 0, mHits.size - 1);
        mHits[mHits.size - 1] = SystemClock.uptimeMillis()
        if (mHits[0] >= (SystemClock.uptimeMillis() - 1000)) {
            super.onBackPressed();
        } else {
            ToastUtils.showShort("再按一次退出程序")
        }
    }
}
