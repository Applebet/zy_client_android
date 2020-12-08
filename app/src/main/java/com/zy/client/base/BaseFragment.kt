package com.zy.client.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

/**
 * @author javakam
 */
abstract class BaseFragment : Fragment(), IBackPressed {

    lateinit var baseActivity: BaseActivity
    lateinit var rootView: View
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

    open fun initView() {}
    open fun initListener() {}
    open fun initData() {}
    open fun refreshData() {}

    override fun onBackPressed(): Boolean = false
}