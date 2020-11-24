package com.zy.client.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context

import android.content.Intent
import android.net.Uri
import com.zy.client.App

object ClipboardUtils {
    /**
     * 复制文本到剪贴板
     *
     * @param text 文本
     */
    fun copyText(text: CharSequence?) {
        val cm: ClipboardManager = App.instance
            .getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        cm.setPrimaryClip(ClipData.newPlainText("text", text))
    }

    /**
     * 获取剪贴板的文本
     *
     * @return 剪贴板的文本
     */
    fun getText(): CharSequence? {
        val cm: ClipboardManager = App.instance
            .getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip: ClipData? = cm.primaryClip
        return if (clip != null && clip.itemCount > 0) {
            clip.getItemAt(0)
                .coerceToText(App.instance)
        } else null
    }

    /**
     * 复制uri到剪贴板
     *
     * @param uri uri
     */
    fun copyUri(uri: Uri?) {
        val cm: ClipboardManager = App.instance
            .getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        cm.setPrimaryClip(
            ClipData.newUri(
                App.instance.contentResolver, "uri", uri
            )
        )
    }

    /**
     * 获取剪贴板的uri
     *
     * @return 剪贴板的uri
     */
    fun getUri(): Uri? {
        val cm: ClipboardManager = App.instance
            .getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip: ClipData? = cm.primaryClip
        return if (clip != null && clip.itemCount > 0) {
            clip.getItemAt(0).uri
        } else null
    }

    /**
     * 复制意图到剪贴板
     *
     * @param intent 意图
     */
    fun copyIntent(intent: Intent?) {
        val cm: ClipboardManager = App.instance
            .getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        cm.setPrimaryClip(ClipData.newIntent("intent", intent))
    }

    /**
     * 获取剪贴板的意图
     *
     * @return 剪贴板的意图
     */
    fun getIntent(): Intent? {
        val cm: ClipboardManager = App.instance
            .getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip: ClipData? = cm.primaryClip
        return if (clip != null && clip.itemCount > 0) {
            clip.getItemAt(0).intent
        } else null
    }
}