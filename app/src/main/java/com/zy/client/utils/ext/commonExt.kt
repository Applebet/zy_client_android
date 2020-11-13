package com.zy.client.utils.ext

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.zy.client.App
import com.zy.client.utils.NoShakeClickListener
import java.util.*

/**
 * Title: 扩展函数
 * <p>
 * Description:
 * </p>
 * @author javakam
 * @date 2020/11/11  15:53
 */

fun View?.visibleOrGone(visible: Boolean) {
    this?.run {
        if (visible) {
            if (!isVisible) visibility = View.VISIBLE
        } else {
            if (isVisible) visibility = View.GONE
        }
    }
}

fun View?.visibleOrInvisible(visible: Boolean) {
    this?.run {
        if (visible) {
            if (!isVisible) visibility = View.VISIBLE
        } else {
            if (isVisible) visibility = View.INVISIBLE
        }
    }
}

fun View?.visible() {
    this?.run {
        if (!isVisible) visibility = View.VISIBLE
    }
}

fun View?.invisible() {
    this?.run {
        if (isVisible) visibility = View.INVISIBLE
    }
}

fun View?.gone() {
    this?.run {
        if (isVisible) visibility = View.GONE
    }
}

fun View?.noShake(block: (v: View?) -> Unit) {
    this?.apply {
        setOnClickListener(object : NoShakeClickListener() {
            override fun onSingleClick(v: View?) {
                block.invoke(v)
            }
        })
    }
}

fun String?.noNull(default: String? = ""): String {
    return if (isNullOrBlank()) default ?: "" else this
}

/**
 * 可以在应用内播放的地址
 */
fun String?.isVideoUrl(): Boolean {
    return (this.noNull()).toLowerCase(Locale.ROOT).run {
        endsWith(".m3u8")
                || endsWith(".mp4")
                || endsWith(".flv")
                || endsWith(".avi")
                || endsWith(".rm")
                || endsWith(".rmvb")
                || endsWith(".wmv")
    }
}

fun String.copyToClipBoard() {
    val cm: ClipboardManager? =
        App.instance.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager?
    if (cm != null) {
        cm.setPrimaryClip(ClipData.newPlainText(null, this))//参数一：标签，可为空，参数二：要复制到剪贴板的文本
        if (cm.hasPrimaryClip()) {
            cm.primaryClip?.getItemAt(0)?.text
        }
    }
}

///////////////////////////////////////////Glide
/**
 * Glide+RecyclerView卡在placeHolder视图 , 不显示加载成功图片的问题
 * <pre>
 * https://www.cnblogs.com/jooy/p/12186977.html
</pre> *
 */
fun noAnimate(placeholder: Int = -1): RequestOptions {
    var options = RequestOptions()
        .centerCrop()
        .dontAnimate()
    if (placeholder > 0) {
        options = options.placeholder(placeholder)
    }
    return options
}

fun noAnimate(placeholder: Int = -1, error: Int = -1): RequestOptions {
    var options = RequestOptions()
        .centerCrop()
        .dontAnimate()
    if (placeholder > 0) {
        options = options.placeholder(placeholder)
    }
    if (error > 0) {
        options = options.error(error)
    }
    return options
}

fun loadImage(imageView: ImageView, url: String?, placeholder: Int = -1) {
    if (url != null && url.startsWith("http")) {
        Glide.with(imageView.context)
            .load(url)
            .transition(DrawableTransitionOptions.withCrossFade())
            .apply(noAnimate(placeholder))
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .into(imageView)
    } else {
        Glide.with(imageView.context).load(placeholder).centerCrop()
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).into(imageView)
    }
}

fun loadImage(imageView: ImageView, path: Any?, placeholder: Drawable?) {
    val options = RequestOptions()
        .centerCrop()
        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
        .placeholder(placeholder)

    Glide.with(imageView.context)
        .load(path)
        .apply(options)
        .transition(DrawableTransitionOptions.withCrossFade())
        .dontAnimate()
        .into(imageView)
}