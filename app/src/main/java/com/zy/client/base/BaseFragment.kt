package com.zy.client.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.zy.client.R
import com.wuhenzhizao.titlebar.widget.CommonTitleBar

/**
 * @author javakam
 * @date 2020/6/8 15:16
 */
abstract class BaseFragment : Fragment(), IBackPressed {

    lateinit var baseActivity: BaseActivity
    lateinit var rootView: View
    var titleBar: CommonTitleBar? = null
    private var isLoaded = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        baseActivity = context as BaseActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(getLayoutId(), container, false)
        return rootView
    }

    abstract fun getLayoutId(): Int

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        titleBar = view.findViewById(R.id.title_bar) as? CommonTitleBar
        initTitleBar(titleBar)
        initView()
        initListener()
        if (this !is ILazyLoad) {
            initData()
        }
    }

    override fun onResume() {
        super.onResume()
        if (!isLoaded && this is ILazyLoad) {
            initData()
            isLoaded = true
        }
    }

    abstract fun initTitleBar(titleBar: CommonTitleBar?)

    open fun initView() {}
    open fun initListener() {}
    open fun initData() {}

    override fun onBackPressed(): Boolean = false
}