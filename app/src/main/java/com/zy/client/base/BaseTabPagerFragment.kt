package com.zy.client.base

import android.os.Bundle
import android.view.ViewGroup
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.zy.client.R
import com.zy.client.bean.Classify
import com.zy.client.http.ConfigManager
import com.zy.client.http.IRepository
import com.zy.client.views.loader.Loader
import com.zy.client.views.loader.LoaderLayout

abstract class BaseTabPagerFragment : BaseFragment() {

    protected lateinit var mStatusView: LoaderLayout
    protected lateinit var mTabLayout: TabLayout
    protected lateinit var mViewPager: ViewPager
    protected lateinit var mViewPagerAdapter: ViewPageAdapter

    //
    protected var mRepo: IRepository? = null
    protected var mClassifyList = ArrayList<Classify>()

    override fun getLayoutId(): Int = R.layout.fragment_tab_pager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mRepo = ConfigManager.curUseSourceConfig()
    }

    override fun initView() {
        super.initView()
        mStatusView = rootView.findViewById(R.id.statusView)
        mTabLayout = rootView.findViewById(R.id.tabLayout)
        mViewPager = rootView.findViewById(R.id.viewpager)

        mViewPagerAdapter = ViewPageAdapter()
        mViewPager.adapter = mViewPagerAdapter
        mViewPager.offscreenPageLimit = 100
        mTabLayout.setupWithViewPager(mViewPager)
    }

    override fun initListener() {
        super.initListener()
        mStatusView.setOnReloadListener(object : Loader.OnReloadListener {
            override fun onReload() {
                initData()
            }
        })
    }

    override fun refreshData() {
        super.refreshData()
        if (!isAdded) return
        mViewPagerAdapter.mCurrFragment.refreshData()
    }

    inner class ViewPageAdapter : FragmentPagerAdapter(
        childFragmentManager,
        BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
    ) {
        lateinit var mCurrFragment: BaseFragment

        override fun getItem(position: Int): BaseFragment {
            mCurrFragment = getItemFragment(mClassifyList[position])
            return mCurrFragment
        }

        override fun getCount(): Int = mClassifyList.size

        override fun getPageTitle(position: Int): CharSequence? {
            return mClassifyList[position].name
        }
    }

    abstract fun getItemFragment(classify: Classify): BaseFragment

}