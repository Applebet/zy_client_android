package com.zy.client.base

import com.chad.library.adapter.base.viewholder.BaseViewHolder
import kotlinx.android.synthetic.main.base_list_fragment.*

/**
 * @author javakam
 *
 * @date 2020/9/17 15:45
 * @desc 懒加载的通用列表
 */

abstract class BaseLazyListFragment<T, H : BaseViewHolder> : BaseListFragment<T, H>(), ILazyLoad {
    override fun initView() {
        super.initView()
        statusView.setLoadingStatus()
    }
}