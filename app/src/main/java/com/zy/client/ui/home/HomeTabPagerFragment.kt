package com.zy.client.ui.home

import com.zy.client.base.BaseFragment
import com.zy.client.bean.Classify
import com.zy.client.base.BaseTabPagerFragment
import com.zy.client.common.HOME_LIST_TID_NEW
import com.zy.client.common.filterHealthyLife
import com.zy.client.common.isHealthLife
import com.zy.client.views.loader.LoadState

class HomeTabPagerFragment : BaseTabPagerFragment() {

    override fun getItemFragment(classify: Classify): BaseFragment {
        return HomeListFragment.instance(classify.id.toString())
    }

    override fun initData() {
        super.initData()

        mRepo?.getHomeData {
            if (it == null) {
                mStatusView.setLoadState(LoadState.ERROR)
                return@getHomeData
            }

            if (mClassifyList.isNotEmpty()) mClassifyList.clear()
            mClassifyList.add(Classify(HOME_LIST_TID_NEW, "最新"))
            mClassifyList.addAll(it.classifyList.filter { classify ->
                !classify.id.isNullOrBlank() && !classify.name.isNullOrBlank()
                        && (if (isHealthLife()) true else (!filterHealthyLife(classify.name)))
            } as ArrayList<Classify>)

            mViewPagerAdapter.notifyDataSetChanged()
            mStatusView.setLoadState(LoadState.SUCCESS)
        }
    }

}