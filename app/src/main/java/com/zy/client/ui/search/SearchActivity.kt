package com.zy.client.ui.search

import android.app.Activity
import android.content.Intent
import com.zy.client.R
import com.zy.client.base.BaseActivity

/**
 * @author javakam
 *
 * @date 2020/9/7 21:22
 * @desc 搜索页
 */
const val SEARCH = "search"

class SearchActivity : BaseActivity() {
    override fun getLayoutId() = R.layout.layout_container
    override fun initView() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, SearchFragment(), SEARCH)
            .commitAllowingStateLoss()
    }

    companion object {
        fun jump(activity: Activity) {
            activity.startActivity(Intent(activity, SearchActivity::class.java))
        }
    }
}