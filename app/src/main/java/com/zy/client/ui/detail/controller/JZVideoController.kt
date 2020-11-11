package com.zy.client.ui.detail.controller

import cn.jzvd.Jzvd
import cn.jzvd.JzvdStd

/**
 * @author javakam
 *
 * @date 2020/9/17 22:38
 * @desc JZVideo的控制类
 */
class JZVideoController {

    private lateinit var player: JzvdStd

    fun init(player: JzvdStd) {
        this.player = player
    }

    fun onBackPressed(): Boolean {
        return Jzvd.backPress()
    }

    fun onResume() {
        JzvdStd.goOnPlayOnResume()
    }

    fun onPause() {
        JzvdStd.goOnPlayOnPause()
    }

    fun onDestroy() {
        JzvdStd.releaseAllVideos()
    }

    fun play(playUrl: String?, name: String?) {
        player.setUp(playUrl, name)
        player.startVideo()
    }
}