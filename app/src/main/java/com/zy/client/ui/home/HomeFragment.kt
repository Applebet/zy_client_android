package com.zy.client.ui.home

import android.widget.ImageView
import android.widget.TextView
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.zy.client.R
import com.zy.client.http.ConfigManager
import com.zy.client.http.NetRepository
import com.zy.client.base.BaseFragment
import com.zy.client.common.AppRouter
import com.zy.client.common.isHealthLife
import com.zy.client.common.switchHealthLife
import com.zy.client.utils.ext.noNull
import com.zy.client.utils.ext.toastShort
import kotlinx.android.synthetic.main.fragment_home.*

/**
 * @author javakam
 *
 * @date 2020/9/2 22:39
 */
class HomeFragment : BaseFragment() {

    private lateinit var ivHistory: ImageView
    private lateinit var tvSearch: TextView
    private var mRepo: NetRepository? = null
    private var mSourceDialog: BasePopupView? = null

    override fun getLayoutId(): Int = R.layout.fragment_home

    override fun initView() {
        super.initView()
        mRepo = ConfigManager.curUseSourceConfig()
        ivHistory = rootView.findViewById(R.id.iv_home_history)
        tvSearch = rootView.findViewById(R.id.tv_home_search)
    }

    override fun initListener() {
        super.initListener()
        ivHistory.setOnLongClickListener {
            val isHealthyLife = isHealthLife()
            switchHealthLife(!isHealthyLife)
            if (isHealthyLife) toastShort("健康生活已关闭!") else toastShort("健康生活已开启!")
            initData()
            true
        }
        ivHistory.setOnClickListener {
            AppRouter.toHistoryActivity(baseActivity)
//            PermissionManager.proceedStoragePermission(baseActivity){
//                if (it) {
//                    val intent = Intent(baseActivity, DownloadService::class.java)
//                    baseActivity.startService(intent)
//                }
//            }
        }

        tvSearch.setOnClickListener {
            AppRouter.toSearchActivity(baseActivity)
        }

        faBtn.setOnClickListener {
            //选择视频源
            if (mSourceDialog == null) {
                val values = ConfigManager.sourceConfigs.values
                val keys = ConfigManager.sourceConfigs.keys.toTypedArray()
                mSourceDialog = XPopup.Builder(requireActivity())
                    .asCenterList("视频源",
                        values.map { it.name }.toTypedArray(),
                        null,
                        keys.indexOfFirst { it == mRepo?.req?.key }
                    ) { position, _ ->
                        mRepo = ConfigManager.generateSource(keys[position])
                        ConfigManager.saveCurUseSourceConfig(mRepo?.req?.key)
                        initData()
                    }
                    .bindLayout(R.layout.fragment_search_result)
            }
            mSourceDialog?.show()
        }
    }

    override fun initData() {
        super.initData()
        tvSearch.hint = mRepo?.req?.name.noNull("搜索")
        childFragmentManager
            .beginTransaction()
            .replace(R.id.container, HomeTabPagerFragment())
            .commitNowAllowingStateLoss()
    }
}