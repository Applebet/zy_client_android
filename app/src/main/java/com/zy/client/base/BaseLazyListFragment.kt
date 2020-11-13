package com.zy.client.base

import com.chad.library.adapter.base.viewholder.BaseViewHolder
import kotlinx.android.synthetic.main.layout_com_title_list.*

/**
 * 懒加载
 *
 * @author javakam
 */
abstract class BaseLazyListFragment<T, H : BaseViewHolder> : BaseListFragment<T, H>(), ILazyLoad {
    override fun initView() {
        super.initView()
        statusView.setLoadingStatus()
    }
}