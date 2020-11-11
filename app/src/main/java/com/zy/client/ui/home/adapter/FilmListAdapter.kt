package com.zy.client.ui.home.adapter

import android.content.Intent
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.zy.client.R
import com.zy.client.ui.detail.FilmDetailActivity
import com.zy.client.ui.detail.FilmDetailFragment
import com.zy.client.ui.home.model.FilmModelItem

/**
 * @author javakam
 * @date 2020/6/9 9:39
 */
class FilmListAdapter(data: MutableList<FilmModelItem>) :
    BaseQuickAdapter<FilmModelItem, BaseViewHolder>(
        R.layout.film_item_layout, data
    ), LoadMoreModule {
    override fun convert(holder: BaseViewHolder, item: FilmModelItem) {
        holder.setText(R.id.tvFilm, item.name)
        holder.setText(R.id.tvType, item.type)
        holder.itemView.setOnClickListener {
            context.startActivity(
                Intent(context, FilmDetailActivity::class.java).apply {
                    putExtra(FilmDetailFragment.KEY, item.site)
                    putExtra(FilmDetailFragment.DETAIL_URL, item.detail)
                }
            )
        }
    }
}