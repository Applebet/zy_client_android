package com.zy.client.base

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import com.zy.client.R
import com.zy.client.bean.Classify
import com.zy.client.http.ConfigManager
import com.zy.client.http.repo.IRepository
import com.zy.client.views.loader.Loader
import kotlinx.android.synthetic.main.fragment_tab_pager.*

abstract class BaseTabPagerFragment : BaseFragment() {

    protected var mRepo: IRepository? = null
    protected var mClassifyList = ArrayList<Classify>()

    override fun getLayoutId(): Int = R.layout.fragment_tab_pager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mRepo = ConfigManager.curUseSourceConfig()
    }

    override fun initListener() {
        super.initListener()
        statusView.setOnReloadListener(object : Loader.OnReloadListener {
            override fun onReload() {
                initData()
            }
        })
    }

    inner class ViewPageAdapter : FragmentPagerAdapter(
        childFragmentManager,
        BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
    ) {
        override fun getItem(position: Int): Fragment {
            return getItemFragment(mClassifyList[position])
        }

        override fun getCount(): Int = mClassifyList.size

        override fun getPageTitle(position: Int): CharSequence? {
            return mClassifyList[position].name
        }
    }

    abstract fun getItemFragment(classify: Classify): Fragment

}