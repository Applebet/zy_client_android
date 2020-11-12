package com.zy.client.ui.channel

import com.bumptech.glide.Glide
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.zy.client.R
import com.zy.client.common.BaseLoadMoreAdapter
import com.zy.client.utils.ext.noNull
import com.zy.client.bean.entity.HomeChannelData

/**
 * @author javakam
 *
 * @date 2020/9/5 22:51
 * @desc 首页频道的适配器
 */

class HomeChannelAdapter :
    BaseLoadMoreAdapter<HomeChannelData, BaseViewHolder>(
        R.layout.home_channel_item_layout
    ) {
    override fun convert(holder: BaseViewHolder, item: HomeChannelData) {
        holder.setText(R.id.tvTitle, item.name.noNull("--"))
        Glide.with(context)
            .load(item.pic)
            .centerCrop()
//           .transform(CenterCrop(), RoundedCorners(Utils.dp2px(12.0f)))
            .into(holder.getView(R.id.ivPiv))
    }

}

