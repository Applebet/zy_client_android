package com.zy.client.ui.iptv

import androidx.fragment.app.Fragment
import com.wuhenzhizao.titlebar.widget.CommonTitleBar
import com.zy.client.base.BaseTabPagerFragment
import com.zy.client.bean.Classify
import com.zy.client.http.ConfigManager
import com.zy.client.utils.ext.visible
import com.zy.client.views.loader.LoadState
import kotlinx.android.synthetic.main.fragment_tab_pager.*

class IPTVFragment : BaseTabPagerFragment() {

    override fun initTitleBar(titleBar: CommonTitleBar?) {
        titleBar?.run {
            visible()
            centerTextView.text = "电视"
        }
    }

    override fun getItemFragment(classify: Classify): Fragment {
        return IPTVListFragment.instance(classify.name.toString())
    }

    override fun initData() {
        super.initData()
        initTitleBar(titleBar)

        ConfigManager.getIPTVGroups().apply {
            if (this == null) {
                statusView.setLoadState(LoadState.ERROR)
                return
            }
            mClassifyList.clear()
            mClassifyList.addAll(this)
            viewpager.adapter = ViewPageAdapter()
            viewpager.offscreenPageLimit = 100
            tabLayout.setupWithViewPager(viewpager)
            statusView.setLoadState(LoadState.SUCCESS)
        }
    }

}