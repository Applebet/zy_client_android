package com.zy.client.ui.detail

import com.zy.client.R
import com.zy.client.base.BaseActivity

/**
 * @author javakam
 * @date 2020/6/9 17:49
 */
const val FILM_DETAIL = "film_detail"

class FilmDetailActivity : BaseActivity() {
    override fun getLayoutId() = R.layout.layout_container
    override fun initView() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, FilmDetailFragment(), FILM_DETAIL)
            .commitAllowingStateLoss()
    }
}