package com.zy.client.ui.search

import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.zy.client.R
import com.zy.client.common.BaseLoadMoreAdapter
import com.zy.client.utils.ext.noNull
import com.zy.client.bean.entity.SearchResultData

/**
 * @author javakam
 *
 * @date 2020/9/5 22:51
 * @desc 搜索结果页的适配器
 */

class SearchResultAdapter :
    BaseLoadMoreAdapter<SearchResultData, BaseViewHolder>(
        R.layout.item_search_result
    ) {
    override fun convert(holder: BaseViewHolder, item: SearchResultData) {
        holder.setText(R.id.tvName, item.name.noNull("--"))
        holder.setText(R.id.tvType, item.type.noNull("--"))
    }
}
