package com.zy.client.ui.search

import androidx.core.widget.addTextChangedListener
import com.blankj.utilcode.util.KeyboardUtils
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.lxj.xpopup.interfaces.OnSelectListener
import com.zy.client.R
import com.zy.client.http.ConfigManager
import com.zy.client.utils.ext.textOrDefault
import com.zy.client.database.SearchHistoryDBUtils
import com.zy.client.base.BaseFragment
import com.wuhenzhizao.titlebar.widget.CommonTitleBar
import com.zy.client.utils.ext.visible
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.layout_search_history.view.*

/**
 * @author javakam
 *
 * @date 2020/9/7 21:19
 * @desc 搜索页
 */
class SearchFragment : BaseFragment() {

    private var sourceKey: String? = null
    private var searchWord: String = ""
    private var selectSourceDialog: BasePopupView? = null

    override fun getLayoutId(): Int = R.layout.fragment_search

    override fun initTitleBar(titleBar: CommonTitleBar?) {
        titleBar?.run {
            setListener { _, action, extra ->
                when (action) {
                    CommonTitleBar.ACTION_SEARCH_DELETE -> {
                        //删除按钮
                    }
                    CommonTitleBar.ACTION_LEFT_BUTTON -> {
                        requireActivity().finish()
                    }
                }
                if (extra != null) {
                    //按下键盘搜索按钮
                    initData()
                    KeyboardUtils.hideSoftInput(requireActivity())
                }
            }
        }
    }

    override fun initView() {
        super.initView()
        sourceKey = ConfigManager.curUseSourceConfig().key
        changeEditHint()
        viewHistory.visible()
        viewHistory.statusView.setLoadingStatus()
    }

    override fun initListener() {
        super.initListener()
        faBtnExchange.setOnClickListener {
            //选择视频源
            if (selectSourceDialog == null) {
                val values = ConfigManager.sourceConfigs.values
                val keys = ConfigManager.sourceConfigs.keys.toTypedArray()
                selectSourceDialog = XPopup.Builder(requireActivity())
                    .asCenterList("选择视频源",
                        values.map { it.name }.toTypedArray(),
                        null,
                        keys.indexOfFirst { it == sourceKey },
                        OnSelectListener { position, text ->
                            sourceKey = keys[position]
                            changeEditHint()
                            ConfigManager.saveCurUseSourceConfig(text)
                            initData()
                        })
                    .bindLayout(R.layout.fragment_search_result)

            }
            selectSourceDialog?.show()
        }

        //监听搜索框变化
        titleBar?.centerSearchEditText?.addTextChangedListener {
            searchWord = it?.toString() ?: ""
        }
    }

    override fun initData() {
        super.initData()
        //搜索历史
        showSearchHistory()

        if (searchWord.isBlank()) return
        SearchHistoryDBUtils.saveAsync(searchWord)
        childFragmentManager
            .beginTransaction()
            .replace(
                R.id.flContainer,
                SearchResultFragment.instance(sourceKey!!, searchWord),
                SEARCH_RESULT
            )
            .commitAllowingStateLoss()
    }

    private fun changeEditHint() {
        titleBar?.centerSearchEditText?.hint =
            ConfigManager.sourceConfigs[sourceKey]?.name.textOrDefault("搜索")
    }

    private fun showSearchHistory() {
        viewHistory.ivDelete.setOnClickListener {
            //清除全部记录
            if (SearchHistoryDBUtils.deleteAll()) {
                viewHistory.tagGroup.setTags(arrayListOf())
                viewHistory.statusView.setEmptyStatus()
            }
        }

        viewHistory.tagGroup.setOnTagClickListener {
            searchWord = it
            titleBar?.centerSearchEditText?.setText(it)
            initData()
        }

        SearchHistoryDBUtils.searchAllAsync {
            if (it.isNullOrEmpty()) {
                viewHistory.statusView.setEmptyStatus()
            } else {
                viewHistory.tagGroup.setTags(it.map { model -> model.searchWord })
                viewHistory.statusView.setSuccessStatus()
            }
        }

    }
}