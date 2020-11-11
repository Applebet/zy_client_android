package com.zy.client.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import com.zy.client.utils.SPUtils
import com.zy.client.R
import com.zy.client.http.ConfigManager
import com.zy.client.common.SP_OPEN_FL
import com.zy.client.http.sources.BaseSource
import com.zy.client.bean.entity.Classify
import com.zy.client.base.BaseFragment
import com.zy.client.ui.channel.HomeChannelFragment
import com.wuhenzhizao.titlebar.widget.CommonTitleBar
import kotlinx.android.synthetic.main.fragment_home.statusView
import kotlinx.android.synthetic.main.fragment_home_source.*

/**
 * @author javakam
 *
 * @date 2020/9/13 12:25
 * @desc
 */
class HomeSourceFragment : BaseFragment() {
    private var classifyList = ArrayList<Classify>()

    private var source: BaseSource? = null


    override fun getLayoutId(): Int = R.layout.fragment_home_source

    override fun initTitleBar(titleBar: CommonTitleBar?) {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        source = ConfigManager.curUseSourceConfig()
    }

    override fun initListener() {
        super.initListener()
        statusView.run {
            failRetryClickListener = {
                initData()
            }
        }
    }


    override fun initData() {
        super.initData()
        statusView.setLoadingStatus()
        source?.requestHomeData {
            if (it == null) {
                statusView.setFailStatus()
                return@requestHomeData
            }
            val openFL = SPUtils.get().getBoolean(SP_OPEN_FL)
            classifyList.clear()
            classifyList.add(Classify("new", "最新"))
            classifyList.addAll(it.classifyList.filter { classify ->
                !classify.id.isNullOrBlank() && !classify.name.isNullOrBlank() &&
                        //筛去福利
                        (if (openFL) true else (!classify.name.contains("福利") && !classify.name.contains(
                            "伦理"
                        )))
            } as ArrayList<Classify>)
            viewpager.adapter = ViewPageAdapter()
            viewpager.offscreenPageLimit = 100
            tabLayout.setupWithViewPager(viewpager)
            statusView.setSuccessStatus()
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