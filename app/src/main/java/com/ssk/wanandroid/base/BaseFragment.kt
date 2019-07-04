package com.ssk.wanandroid.base

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.ssk.wanandroid.LoadingDialog
import com.ssk.wanandroid.R
import com.ssk.wanandroid.service.EventManager
import com.ssk.wanandroid.utils.AndroidVersion
import com.ssk.wanandroid.utils.DeviceInfo

/**
 * Created by shisenkun on 2019-06-23.
 */
abstract class BaseFragment : Fragment() {

    lateinit var mContentView: View
    lateinit var mActivity: BaseActivity

    var toolbar: Toolbar? = null
    var mLoadingDialog: LoadingDialog? = null
    var mIsActivityCreate = false;

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mContentView = inflater.inflate(getLayoutResId(), container, false)
        return mContentView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mIsActivityCreate = true
        mActivity = activity as BaseActivity
        initView(savedInstanceState)
        initData(savedInstanceState)
        EventManager.register(this)
    }

    override fun onDestroyView() {
        EventManager.unregister(this)
        super.onDestroyView()
    }

    open fun initView(savedInstanceState: Bundle?) {}
    open fun initData(savedInstanceState: Bundle?) {}

    abstract fun getLayoutResId(): Int

    protected fun startActivity(clazz: Class<*>) {
        startActivity(Intent(mActivity, clazz))
    }

    protected fun startActivity(clazz: Class<*>, bundle: Bundle) {
        startActivity(Intent(mActivity, clazz).putExtras(bundle))
    }

    protected fun setupToolbar(displayHomeAsUpEnabled: Boolean) {
        if (mIsActivityCreate != null) {
            toolbar = mContentView.findViewById(R.id.toolbar)
            mActivity.setSupportActionBar(toolbar)
            val actionBar = mActivity.supportActionBar
            actionBar?.setDisplayHomeAsUpEnabled(displayHomeAsUpEnabled)
            animateToolbarTitle()
        }
    }

    fun animateToolbarTitle() {
        val t = toolbar?.getChildAt(0)
        if (t != null && t is TextView) {
            t.alpha = 0f
            t.scaleX = 0.8f
            t.animate().alpha(1f).scaleX(1f).setDuration(300)
        }
    }

    /**
     * 设置沉浸式状态栏。只适配Android 5.0以上系统的手机。
     * color 沉浸式颜色
     * handleLayoutOverLap 是否处理布局重叠问题
     */
    protected fun immersiveStatusBar(color: Int, handleLayoutOverLap: Boolean) {
        if (mIsActivityCreate && AndroidVersion.hasLollipop()) {
            val decorView = mActivity.window.decorView
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            mActivity.window.statusBarColor = resources.getColor(color)
            val contentView: ViewGroup = decorView.findViewById(android.R.id.content)
            contentView.setPadding(
                decorView.paddingLeft,
                if(handleLayoutOverLap) DeviceInfo.statusBarHeight else 0,
                decorView.paddingRight,
                decorView.paddingBottom
            )
        }
    }

    fun showLoadingDialog(message: String = "") {
        dismissLoadingDialog()
        mLoadingDialog = LoadingDialog(mActivity, message)
        mLoadingDialog!!.show()
    }

    fun dismissLoadingDialogFailed(message: String = "") {
        mLoadingDialog?.dismissFailed(message)
    }

    fun dismissLoadingDialogSuccess(message: String = "") {
        mLoadingDialog?.dismissSuccess(message)
    }

    fun dismissLoadingDialog() {
        mLoadingDialog?.dismiss()
        mLoadingDialog = null
    }

}