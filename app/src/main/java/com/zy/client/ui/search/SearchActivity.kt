package com.zy.client.ui.search

import androidx.core.widget.addTextChangedListener
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.wuhenzhizao.titlebar.widget.CommonTitleBar
import com.zy.client.R
import com.zy.client.base.BaseActivity
import com.zy.client.database.SearchHistoryDBUtils
import com.zy.client.http.ConfigManager
import com.zy.client.utils.KeyboardUtils
import com.zy.client.utils.ext.noNull
import com.zy.client.utils.ext.visible
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.layout_search_history.view.*


/**
 * 搜索页
 *
 * @author javakam
 */
class SearchActivity : BaseActivity() {

    private lateinit var sourceKey: String
    private lateinit var titleBar: CommonTitleBar

    private var searchWord: String = ""
    private var selectSourceDialog: BasePopupView? = null

    override fun getLayoutId() = R.layout.activity_search

    override fun initView() {
        initTitleBar()
        sourceKey = ConfigManager.curUseSourceConfig().req.key
        changeEditHint()
        viewHistory.visible()
        viewHistory.updateHistory()
        initListener()
    }

    private fun initTitleBar() {
        titleBar = findViewById(R.id.title_bar)
        titleBar.run {
            setListener { _, action, _ ->
                when (action) {
                    CommonTitleBar.ACTION_LEFT_BUTTON -> {
                        finish()
                    }
                    CommonTitleBar.ACTION_RIGHT_BUTTON, CommonTitleBar.ACTION_SEARCH_SUBMIT -> {
                        //按下键盘搜索按钮
                        initData()
                        KeyboardUtils.hideSoftInput(this)
                    }
                }
            }
        }
    }

    override fun initListener() {
        super.initListener()
        faBtnExchange.setOnClickListener {
            if (selectSourceDialog == null) {
                val values = ConfigManager.sourceConfigs.values
                val keys = ConfigManager.sourceConfigs.keys.toTypedArray()
                selectSourceDialog = XPopup.Builder(this)
                    .asCenterList("选择视频源",
                        values.map { it.name }.toTypedArray(),
                        null,
                        keys.indexOfFirst { it == sourceKey }
                    ) { position, text ->
                        sourceKey = keys[position]
                        changeEditHint()
                        ConfigManager.saveCurUseSourceConfig(text)
                        initData()
                    }
                    .bindLayout(R.layout.fragment_search_result)

            }
            selectSourceDialog?.show()
        }

        //监听搜索框变化
        titleBar.centerSearchEditText?.addTextChangedListener {
            searchWord = it?.toString() ?: ""
        }

        //历史记录 Item点击监听
        viewHistory.tagGroup.setOnTagClickListener {
            searchWord = it
            titleBar.centerSearchEditText?.setText(it)
            initData()
        }
    }

    override fun initData() {
        super.initData()
        viewHistory.updateHistory()//搜索历史 tip: saveAsync后执行

        if (searchWord.isBlank()) return
        SearchHistoryDBUtils.saveAsync(searchWord) {
            if (it) viewHistory.updateHistory()
        }
        supportFragmentManager
            .beginTransaction()
            .replace(
                R.id.flContainer,
                SearchResultFragment.instance(sourceKey, searchWord),
                "search_result"
            )
            .commitAllowingStateLoss()
    }

    private fun changeEditHint() {
        titleBar.centerSearchEditText?.hint =
            ConfigManager.sourceConfigs[sourceKey]?.name.noNull("搜索")
    }

}