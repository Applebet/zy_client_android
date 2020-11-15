package com.zy.client.ui.home

import android.os.Bundle
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.zy.client.R
import com.zy.client.http.ConfigManager
import com.zy.client.utils.ext.gone
import com.zy.client.http.repo.CommonRepository
import com.zy.client.base.BaseFragment
import com.wuhenzhizao.titlebar.widget.CommonTitleBar
import com.zy.client.common.AppRouter
import com.zy.client.utils.ext.noNull
import kotlinx.android.synthetic.main.fragment_home.*

/**
 * @author javakam
 *
 * @date 2020/9/2 22:39
 */
class HomeFragment : BaseFragment() {

    private var source: CommonRepository? = null
    private var selectSourceDialog: BasePopupView? = null

    override fun getLayoutId(): Int = R.layout.fragment_home

    override fun initTitleBar(titleBar: CommonTitleBar?) {
        titleBar?.run {
            centerSearchRightImageView.gone()
            setListener { _, action, _ ->
                if (action == CommonTitleBar.ACTION_SEARCH) {
                    AppRouter.toSearchActivity(baseActivity)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        source = ConfigManager.curUseSourceConfig()
    }

    override fun initListener() {
        super.initListener()
        faBtn.setOnClickListener {
            //选择视频源
            if (selectSourceDialog == null) {
                val values = ConfigManager.sourceConfigs.values
                val keys = ConfigManager.sourceConfigs.keys.toTypedArray()
                selectSourceDialog = XPopup.Builder(requireActivity())
                    .asCenterList("选择视频源",
                        values.map { it.name }.toTypedArray(),
                        null,
                        keys.indexOfFirst { it == source?.req?.key }
                    ) { position, _ ->
                        source =
                            ConfigManager.generateSource(keys[position])
                        ConfigManager.saveCurUseSourceConfig(source?.req?.key)
                        initData()
                    }
                    .bindLayout(R.layout.fragment_search_result)
            }
            selectSourceDialog?.show()
        }
    }

    override fun initData() {
        super.initData()
        titleBar?.centerSearchEditText?.hint = source?.req?.name.noNull("搜索")
        childFragmentManager
            .beginTransaction()
            .replace(R.id.flContainer, HomeSourceFragment())
            .commitNowAllowingStateLoss()
    }
}