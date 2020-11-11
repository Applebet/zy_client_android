package com.zy.client.bean.entity

/**
 * @author javakam
 *
 * @date 2020/9/7 22:23
 * @desc 搜索结果
 */

data class SearchResultData(
    //视频id
    val id: String?,
    //名字
    val name: String?,
    //类型，战争片
    val type: String
)