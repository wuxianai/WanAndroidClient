package com.ssk.wanandroid.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.ssk.wanandroid.R
import kotlinx.android.synthetic.main.layout_switchable_constraint_layout_root.view.*

/**
 * Created by shisenkun on 2019-06-29.
 * 可切换状态的约束布局
 * 主要封装了切换加载中布局,正常布局,失败布局的功能
 */
class SwitchableConstraintLayout
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null) : ConstraintLayout(context, attrs) {

    private val mLoadingLayoutResIdArray = intArrayOf(R.id.loadingView)
    private val mFailedLayoutResIdArray = intArrayOf(R.id.ivFailed, R.id.tvFailedMsg, R.id.btnRetry)
    private val mEmptyLayoutResIdArray = intArrayOf(R.id.ivEmpty, R.id.tvEmpty)
    private lateinit var mRetryListener: () -> Unit?

    init {
        View.inflate(context, R.layout.layout_switchable_constraint_layout_root, this)
        initView()
    }

    private fun initView() {
        switchLoadingLayout()

        btnRetry.setOnClickListener {
            switchLoadingLayout();
            mRetryListener()
        }
    }

    fun switchSuccessLayout() {
        loadingView.mLoadingAnimListener = object : LoadingView.LoadingAnimListenerAdapter() {
            override fun onLoadingCancelAfterMinRotateDuration() {
                super.onLoadingCancelAfterMinRotateDuration()
                for (i in 0..childCount - 1) {
                    if (getChildAt(i).id in mLoadingLayoutResIdArray) {
                        getChildAt(i).visibility = View.INVISIBLE
                    } else if (getChildAt(i).id in mFailedLayoutResIdArray) {
                        getChildAt(i).visibility = View.INVISIBLE
                    } else if (getChildAt(i).id in mEmptyLayoutResIdArray) {
                        getChildAt(i).visibility = View.INVISIBLE
                    } else {
                        getChildAt(i).visibility = View.VISIBLE
                    }
                }
            }
        }
        loadingView.stopAnimAfterMinRotateDuration()

    }

    fun switchFailedLayout() {
        switchFailedLayout("")
    }

    fun switchFailedLayout(failedMsg: String) {
        if (failedMsg.isNotEmpty()) {
            tvFailedMsg.setText(failedMsg)
        }
        loadingView.mLoadingAnimListener = object : LoadingView.LoadingAnimListenerAdapter() {
            override fun onLoadingCancelAfterMinRotateDuration() {
                loadingView.stopAnim()
                for (i in 0..childCount - 1) {
                    if (getChildAt(i).id in mFailedLayoutResIdArray) {
                        getChildAt(i).visibility = View.VISIBLE
                    } else {
                        getChildAt(i).visibility = View.INVISIBLE
                    }
                }
            }
        }
        loadingView.stopAnimAfterMinRotateDuration()
    }

    fun switchEmptyLayout() {
        loadingView.mLoadingAnimListener = object : LoadingView.LoadingAnimListenerAdapter() {
            override fun onLoadingCancelAfterMinRotateDuration() {
                super.onLoadingCancelAfterMinRotateDuration()
                for (i in 0..childCount - 1) {
                    if (getChildAt(i).id in mEmptyLayoutResIdArray) {
                        getChildAt(i).visibility = View.VISIBLE
                    } else {
                        getChildAt(i).visibility = View.INVISIBLE
                    }
                }
            }
        }
    }

    fun switchLoadingLayout() {
        for (i in 0..childCount - 1) {
            if (getChildAt(i).id in mLoadingLayoutResIdArray) {
                getChildAt(i).visibility = View.VISIBLE
            } else {
                getChildAt(i).visibility = View.INVISIBLE
            }
        }
        loadingView.startRotateAnim()
    }

    fun setRetryListener(listener: () -> Unit) {
        this.mRetryListener = listener
    }

}