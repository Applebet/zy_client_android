package com.zy.client.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import com.zy.client.R
import com.zy.client.http.ConfigManager
import com.zy.client.http.repo.CommonRepository
import com.zy.client.bean.Classify
import com.zy.client.base.BaseFragment
import com.wuhenzhizao.titlebar.widget.CommonTitleBar
import com.zy.client.http.repo.IRepository
import com.zy.client.views.loader.LoadState
import com.zy.client.views.loader.Loader
import kotlinx.android.synthetic.main.fragment_home_source.*

/**
 * @author javakam
 *
 * @date 2020/9/13 12:25
 */
class HomeSourceFragment : BaseFragment() {
    private var classifyList = ArrayList<Classify>()

    private var source: IRepository? = null

    override fun getLayoutId(): Int = R.layout.fragment_home_source

    override fun initTitleBar(titleBar: CommonTitleBar?) {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        source = ConfigManager.curUseSourceConfig()
    }

    override fun initListener() {
        super.initListener()
        statusView.setLoadState(LoadState.LOADING)
        statusView.setOnReloadListener(object :Loader.OnReloadListener{
            override fun onReload() {
                initData()
            }
        })
    }

    override fun initData() {
        super.initData()

        source?.requestHomeData {
            if (it == null) {
                statusView.setLoadState(LoadState.ERROR)
                return@requestHomeData
            }
            //val openFL = SPUtils.get().getBoolean(SP_OPEN_FL)
            val openFL = true
            classifyList.clear()
            classifyList.add(Classify("new", "最新"))
            classifyList.addAll(it.classifyList.filter { classify ->
                !classify.id.isNullOrBlank() && !classify.name.isNullOrBlank() &&
                        //筛去福利
                        (if (openFL) true else (!classify.name.contains("福利") && !classify.name.contains("伦理")))
            } as ArrayList<Classify>)
            viewpager.adapter = ViewPageAdapter()
            viewpager.offscreenPageLimit = 100
            tabLayout.setupWithViewPager(viewpager)
            statusView.setLoadState(LoadState.SUCCESS)
        }
    }

    inner class ViewPageAdapter : FragmentPagerAdapter(
        childFragmentManager,
        BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
    ) {
        override fun getItem(position: Int): Fragment {
            return HomeChannelFragment.instance(classifyList[position].id.toString())
        }

        override fun getCount(): Int = classifyList.size

        override fun getPageTitle(position: Int): CharSequence? {
            return classifyList[position].name
        }
    }

}