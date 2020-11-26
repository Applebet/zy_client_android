package com.zy.client.ui.home

import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.zy.client.bean.Classify
import com.zy.client.base.BaseTabPagerFragment
import com.zy.client.common.HOME_LIST_TID_NEW
import com.zy.client.common.OPEN_FL
import com.zy.client.views.loader.LoadState
import kotlinx.android.synthetic.main.fragment_tab_pager.*

class HomeTabPagerFragment : BaseTabPagerFragment() {

    override fun getItemFragment(classify: Classify): Fragment {
        return HomeListFragment.instance(classify.id.toString())
    }

    override fun initData() {
        super.initData()

        mRepo?.requestHomeData {
            if (it == null) {
                statusView.setLoadState(LoadState.ERROR)
                return@requestHomeData
            }
            //val openFL = SPUtils.get().getBoolean(SP_OPEN_FL)
            val openFL = OPEN_FL
            if (mClassifyList.isNotEmpty()) mClassifyList.clear()
            //mClassifyList.add(Classify(HOME_LIST_TID_NEW, "最新"))
            mClassifyList.addAll(it.classifyList.filter { classify ->
                !classify.id.isNullOrBlank() && !classify.name.isNullOrBlank()
                        &&//筛去福利
                        (if (openFL) true else (!classify.name.contains("福利") && !classify.name.contains(
                            "伦理"
                        )
                                && !classify.name.contains("倫")))
            } as ArrayList<Classify>)
            viewpager.adapter = ViewPageAdapter()
            viewpager.offscreenPageLimit = 100
            tabLayout.setupWithViewPager(viewpager)
            statusView.setLoadState(LoadState.SUCCESS)
        }
    }

}