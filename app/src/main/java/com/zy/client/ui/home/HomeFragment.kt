package com.zy.client.ui.home

import ando.player.service.DownloadService
import android.content.Intent
import android.widget.ImageView
import android.widget.TextView
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.zy.client.R
import com.zy.client.http.ConfigManager
import com.zy.client.http.repo.CommonRepository
import com.zy.client.base.BaseFragment
import com.zy.client.common.AppRouter
import com.zy.client.utils.PermissionManager
import com.zy.client.utils.ext.noNull
import com.zy.client.utils.ext.toastLong
import kotlinx.android.synthetic.main.fragment_home.*

/**
 * @author javakam
 *
 * @date 2020/9/2 22:39
 */
class HomeFragment : BaseFragment() {

    private lateinit var ivHistory: ImageView
    private lateinit var tvSearch: TextView
    private var mRepo: CommonRepository? = null
    private var mSourceDialog: BasePopupView? = null

    override fun getLayoutId(): Int = R.layout.fragment_home

    override fun initView() {
        super.initView()
        mRepo = ConfigManager.curUseSourceConfig()
        ivHistory = rootView.findViewById(R.id.iv_home_history)
        tvSearch = rootView.findViewById(R.id.tv_home_search)
    }

    private fun requestStoragePermission(block: (result: Boolean) -> Unit) {
        val hasStoragePermission =
            PermissionManager.havePermissions(baseActivity, *PermissionManager.PERMISSIONS_STORAGE)

        val shouldShow = PermissionManager.checkShowRationale(
            baseActivity,
            *PermissionManager.PERMISSIONS_STORAGE
        )

        //用户点了禁止获取权限，并勾选不再提示 , 建议做成弹窗提示并提供权限申请页面的跳转
        if (!shouldShow && !hasStoragePermission) {
            toastLong("""请到"设置"中开启"存储"权限! """)
            val intent = PermissionManager.createAppDetailSettingIntent(baseActivity)
            baseActivity.startActivity(intent)
            return
        }

        if (!hasStoragePermission) {
            PermissionManager.verifyStoragePermissions(baseActivity)
        }
        block.invoke(true)
    }

    override fun initListener() {
        super.initListener()
        ivHistory.setOnClickListener {
            AppRouter.toHistoryActivity(baseActivity)
//            requestStoragePermission {
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