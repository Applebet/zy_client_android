package com.zy.client.ui.iptv

import android.widget.TextView
import com.zy.client.R
import com.zy.client.base.BaseFragment
import com.zy.client.base.BaseTabPagerFragment
import com.zy.client.bean.Classify
import com.zy.client.http.ConfigManager
import com.zy.client.utils.ext.visible
import com.zy.client.views.loader.LoadState

class IPTVFragment : BaseTabPagerFragment() {

    override fun initView() {
        super.initView()
        val tvTitle = rootView.findViewById<TextView>(R.id.tv_title)
        tvTitle.visible()
        tvTitle.text = "电视"
    }

    override fun getItemFragment(classify: Classify): BaseFragment {
        return IPTVListFragment.instance(classify.name.toString())
    }

    override fun initData() {
        super.initData()

        ConfigManager.getIPTVGroups().apply {
            mClassifyList.clear()
            mClassifyList.addAll(this)

            mViewPagerAdapter.notifyDataSetChanged()
            mStatusView.setLoadState(LoadState.SUCCESS)
        }
    }

}