package com.zy.client.common

import android.content.Intent
import com.zy.client.base.BaseActivity
import com.zy.client.ui.detail.VideoDetailActivity
import com.zy.client.ui.search.SearchActivity

/**
 * Title: 页面路由
 * <p>
 * Description:
 * </p>
 * @author javakam
 * @date 2020/11/11  16:22
 */
object AppRouter {

    fun toSearchActivity(activity: BaseActivity) {
        activity.startActivity(Intent(activity, SearchActivity::class.java))
    }

    fun toDetailActivity(activity: BaseActivity,sourceKey: String, id: String) {
        activity.startActivity(Intent(activity, VideoDetailActivity::class.java).apply {
            putExtra(SOURCE_KEY, sourceKey)
            putExtra(ID, id)
        })

    }

}