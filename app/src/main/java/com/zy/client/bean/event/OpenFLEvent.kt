package com.zy.client.bean.event

/**
 * @author javakam
 *
 * @date 2020/9/19 18:42
 * @desc 开启福利的事件
 */

class OpenFLEvent(val open: Boolean) {
    override fun equals(other: Any?): Boolean {
        return other is OpenFLEvent
    }

    override fun hashCode(): Int {
        return open.hashCode()
    }
}