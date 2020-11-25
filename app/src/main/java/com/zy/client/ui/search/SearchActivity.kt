package com.zy.client.ui.search

import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import com.zy.client.R
import com.zy.client.base.BaseActivity
import com.zy.client.database.SearchHistoryDBUtils
import com.zy.client.http.ConfigManager
import com.zy.client.utils.KeyboardUtils
import com.zy.client.utils.ext.gone
import com.zy.client.utils.ext.noNull
import com.zy.client.utils.ext.visible
import com.zy.client.utils.ext.visibleOrGone
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.layout_search_history.view.*

/**
 * 搜索页
 *
 * @author javakam
 */
class SearchActivity : BaseActivity() {

    private lateinit var sourceKey: String
    private lateinit var mEditSearch: AppCompatEditText
    private lateinit var mIvSearchDelete: AppCompatImageView
    private lateinit var mTvCancel: TextView

    private var searchWord: String = ""
    private var selectSourceDialog: BasePopupView? = null

    override fun getLayoutId() = R.layout.activity_search

    override fun initView() {
        initSearchView()
        sourceKey = ConfigManager.curUseSourceConfig().req.key
        changeEditHint()
        viewHistory.visible()
        viewHistory.updateHistory()
        initListener()
    }

    private fun initSearchView() {
        mEditSearch = findViewById(R.id.edt_search)
        mIvSearchDelete = findViewById(R.id.iv_search_delete)
        mEditSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                mIvSearchDelete.visibleOrGone((s?.isNotBlank() == true))
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                mIvSearchDelete.visibleOrGone((s?.isNotBlank() == true))
            }

            override fun afterTextChanged(s: Editable?) {
                searchWord = s?.toString() ?: ""
            }
        })
        mEditSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                //按下键盘搜索按钮
                initData()
                KeyboardUtils.hideSoftInput(this)
                true
            } else false
        }
        mIvSearchDelete.setOnClickListener {
            mEditSearch.setText("")
            mIvSearchDelete.gone()
        }

        mTvCancel = findViewById(R.id.tv_search_action)
        mTvCancel.setOnClickListener {
            KeyboardUtils.hideSoftInput(this)
            finish()
        }
    }

    override fun initListener() {
        super.initListener()
        faBtnExchange.setOnClickListener {
            if (selectSourceDialog == null) {
                val values = ConfigManager.sourceConfigs.values
                val keys = ConfigManager.sourceConfigs.keys.toTypedArray()
                selectSourceDialog = XPopup.Builder(this)
                    .asCenterList("视频源",
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

        //历史记录 Item点击监听
        viewHistory.tagGroup.setOnTagClickListener {
            searchWord = it
            mEditSearch.setText(it)
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
        mEditSearch.hint = ConfigManager.sourceConfigs[sourceKey]?.name.noNull("搜索")
    }

}