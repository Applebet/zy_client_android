package ando.player.dialog

import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.WindowManager

internal class BaseDialog(context: Context, themeResId: Int) : Dialog(context, themeResId) {

    override fun show() {
        // Set the dialog to not focusable.
        window!!.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        )
        // Show the dialog with NavBar hidden.
        super.show()
        // Set the dialog to focusable again.
        window!!.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
    }

    /**
     * 设置dialog消失显示广播
     */
    private fun initDefaultDialogListener() {
        setOnShowListener { }
        setOnDismissListener {
            //Log.w("123","Dismiss....................")
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        //Log.w("123","onDetachedFromWindow....................")
    }

    init {
        initDefaultDialogListener()
    }
}