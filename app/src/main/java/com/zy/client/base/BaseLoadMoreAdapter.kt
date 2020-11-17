package com.zy.client.base

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder

/**
 *能上拉加载的Adapter
 *
 * @author javakam
 * @date 2020/9/5 23:13
 */
abstract class BaseLoadMoreAdapter<T, H : BaseViewHolder>(
    layoutResId: Int,
    data: MutableList<T>? = null
) : BaseQuickAdapter<T, H>(layoutResId, data), LoadMoreModule