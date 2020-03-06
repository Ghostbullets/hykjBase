package com.hykj.base.view.refresh

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.hykj.base.R

/**
 * 通用下拉刷新、上拉加载更多容器
 * created by cjf
 * on: 2020/3/5
 */
class SimpleRefreshAndLoadMoreLayout : CustomizeSwipeRefreshAndLoadMoreLayout, ICustomizeSwipeRefreshHeadListener, ICustomizeSwipeLoadMoreFooterListener, CustomizeSwipeRefreshAndLoadMoreLayout.OnPullDistanceListener {
    private lateinit var tvRefresh: TextView //刷新文字
    private var refreshStatus: Int = NOT_REFRESH //当前状态

    private lateinit var tvLoadMore: TextView //加载更多文字
    private var isLoadMoreEnable = true // 是否可加载更多

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        val metrics = resources.displayMetrics
        HEAD_HEIGHT = 70 * metrics.density
        FOOTER_HITGHT = 70 * metrics.density
        SURPRISED_HEIGHT = 200 * metrics.density
        addHeaderView(createHeaderView(context), HEAD_HEIGHT, SURPRISED_HEIGHT, this)
        addFooterView(createFooterView(context), FOOTER_HITGHT, this)
        addOnPullDistanceListener(this)
    }

    private fun createHeaderView(context: Context?): View {
        //设置头部控件宽高，位置
        val view = View.inflate(context, R.layout.layout_refresh_head, null)
        val params = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, HEAD_HEIGHT.toInt())
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
        view.findViewById<LinearLayout>(R.id.layout_refresh).layoutParams = params

        tvRefresh = view.findViewById(R.id.tv_refresh)

        onRefreshEnable(NOT_REFRESH)
        stopRefresh()
        return view
    }

    private fun createFooterView(context: Context?): View {
        //设置尾部控件宽高，位置
        val view = View.inflate(context, R.layout.layout_load_more_footer, null)
        val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, FOOTER_HITGHT.toInt())
        params.gravity = Gravity.TOP
        view.layoutParams = params

        tvLoadMore = view.findViewById(R.id.tv_load_more)

        onLoadMoreEnable(false)
        stopLoadMore()
        return view
    }

    override fun startRefresh() {
        tvRefresh.text = "正在刷新"
    }

    override fun stopRefresh() {

    }

    override fun onRefreshEnable(@RefreshStatus status: Int) {
        if (!isCanRefreshing) {
            tvRefresh.text = ""
            return
        }
        if (refreshStatus != status) {
            this.refreshStatus = status
            when (refreshStatus) {
                NOT_REFRESH -> {
                    tvRefresh.text = "下拉刷新"
                }
                REFRESH -> {
                    tvRefresh.text = if (isCanPullSurprised) "继续下拉有惊喜" else "松开刷新"
                }
                SURPRISED -> {
                    tvRefresh.text = if (isCanPullSurprised) "松手得惊喜" else "松开刷新"
                }
            }
        }
    }

    override fun startLoadMore() {
        tvLoadMore.text = "加载中"
    }

    override fun stopLoadMore() {

    }

    override fun onLoadMoreEnable(enable: Boolean) {
        if (!isCanLoadMore) {
            tvLoadMore.text = "没有更多数据"
            return
        }
        if (isLoadMoreEnable != enable) {
            isLoadMoreEnable = enable
            if (isLoadMoreEnable) {
                tvLoadMore.text = "松开加载"
            } else {
                tvLoadMore.text = "上拉加载"
            }
        }
    }

    override fun onPullDistance(distance: Float) {
    }

    companion object {
        var HEAD_HEIGHT = 0F //头部控件高度
        var FOOTER_HITGHT = 0F //尾部控件高度
        var SURPRISED_HEIGHT = 0F //下拉惊喜高度
    }
}