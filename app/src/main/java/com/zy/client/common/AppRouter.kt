package com.zy.client.common

import android.content.Intent
import com.zy.client.base.BaseActivity
import com.zy.client.database.SourceModel
import com.zy.client.ui.HistoryActivity
import com.zy.client.ui.video.VideoDetailActivity
import com.zy.client.ui.search.SearchActivity
import com.zy.client.ui.video.VideoTvActivity

/**
 * Title: 页面路由
 * <p>
 * Description:
 * </p>
 * @author javakam
 * @date 2020/11/11  16:22
 */
object AppRouter {

    fun toHistoryActivity(activity: BaseActivity) {
        activity.startActivity(Intent(activity, HistoryActivity::class.java))
    }

    fun toSearchActivity(activity: BaseActivity) {
        activity.startActivity(Intent(activity, SearchActivity::class.java))
    }

    fun toVideoDetailActivity(activity: BaseActivity, sourceKey: String, id: String) {
        activity.startActivity(Intent(activity, VideoDetailActivity::class.java).apply {
            putExtra(SOURCE_KEY, sourceKey)
            putExtra(ID, id)
        })
    }

    fun toTvActivity(activity: BaseActivity,bean:SourceModel) {
        activity.startActivity(Intent(activity, VideoTvActivity::class.java).apply {
            putExtra(TV_BEAN, bean)
        })
    }

}