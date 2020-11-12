package com.zy.client.ui.home

import android.os.Bundle
import com.zy.client.utils.SPUtils
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.lxj.xpopup.interfaces.OnSelectListener
import com.zy.client.R
import com.zy.client.http.ConfigManager
import com.zy.client.common.SP_OPEN_FL
import com.zy.client.utils.ext.gone
import com.zy.client.utils.ext.noNull
import com.zy.client.bean.event.OpenFLEvent
import com.zy.client.http.sources.BaseSource
import com.zy.client.base.BaseFragment
import com.wuhenzhizao.titlebar.widget.CommonTitleBar
import com.zy.client.common.AppRouter
import kotlinx.android.synthetic.main.fragment_home_new.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * @author javakam
 *
 * @date 2020/9/2 22:39
 */
class HomeFragment : BaseFragment() {
    private var source: BaseSource? = null

    private var selectSourceDialog: BasePopupView? = null

    private var openFL = false

    override fun getLayoutId(): Int = R.layout.fragment_home_new

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

        openFL = SPUtils.get().getBoolean(SP_OPEN_FL)
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
                        keys.indexOfFirst { it == source?.key },
                        OnSelectListener { position, text ->
                            source =
                                ConfigManager.generateSource(keys[position])
                            ConfigManager.saveCurUseSourceConfig(source?.key)
                            initData()
                        })
                    .bindLayout(R.layout.fragment_search_result)
            }
            selectSourceDialog?.show()
        }
    }

    override fun initData() {
        super.initData()
        titleBar?.centerSearchEditText?.hint = source?.name.noNull("搜索")
        childFragmentManager
            .beginTransaction()
            .replace(R.id.flContainer, HomeSourceFragment())
            .commitNowAllowingStateLoss()
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onMessageEvent(event: OpenFLEvent) {
        if (openFL == event.open) {
            //与当前一样，return
            return
        }
        openFL = event.open
        //不一样时重新加载数据
        initData()
    }

}