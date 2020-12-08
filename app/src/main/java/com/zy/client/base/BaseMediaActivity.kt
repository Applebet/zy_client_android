package com.zy.client.base

import android.view.MenuItem
import com.lxj.xpopup.impl.BottomListPopupView
import com.zy.client.ui.video.VideoController
import com.zy.client.ui.video.WebController
import com.zy.client.utils.status.StatusBarUtils

/**
 * Title: BaseMediaActivity
 * <p>
 * Description: 音视频播放
 * </p>
 * @author javakam
 */
abstract class BaseMediaActivity : BaseActivity() {

    protected var webController: WebController? = null
    protected var videoController: VideoController? = null

    protected var mSelectListDialog: BottomListPopupView? = null

    override fun initStyle(statusBarColor: Int) {
        //super.initStyle(statusBarColor)
        StatusBarUtils.transparentStatusBar(window)
        StatusBarUtils.setLightMode(window)
        StatusBarUtils.setStatusBarView(this, android.R.color.black)
        //or StatusBarUtils.setStatusBarColor(window, Color.BLACK, 0)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        webController?.onResume()
        videoController?.onResume()
    }

    override fun onPause() {
        super.onPause()
        webController?.onPause()
        videoController?.onPause()
    }

    override fun onDestroy() {
        mSelectListDialog?.dismiss()
        webController?.onDestroy()
        videoController?.onDestroy()
        super.onDestroy()
    }

    override fun onBackPressed() {
        if (videoController?.onBackPressed() == true || webController?.onBackPressed() == true) {
            super.onBackPressed()
        }
    }

}