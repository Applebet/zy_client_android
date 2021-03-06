package com.zy.client.views.loader

/**
 * Title:LoadState
 *
 * Description:加载状态
 */
enum class LoadState(private val state: String, private val value: Int) {
    /**
     *
     */
    UNLOADED("默认的状态", 1),
    LOADING("加载的状态", 2),
    ERROR("失败的状态", 3),
    EMPTY("空的状态", 4),
    SUCCESS("成功的状态", 5);

    fun value(): Int {
        return value
    }
}