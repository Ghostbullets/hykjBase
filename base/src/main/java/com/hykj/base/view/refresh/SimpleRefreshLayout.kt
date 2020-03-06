package com.hykj.base.view.refresh

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.hykj.base.R

/**
 * 通用刷新容器
 * created by cjf
 * on: 2020/3/4
 */
class SimpleRefreshLayout : CustomizeSwipeRefreshAndLoadMoreLayout, ICustomizeSwipeRefreshHeadListener, CustomizeSwipeRefreshAndLoadMoreLayout.OnPullDistanceListener {
    private lateinit var tvRefresh: TextView // 刷新文字
    private var refreshStatus: Int = NOT_REFRESH //当前状态

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        val metrics = resources.displayMetrics
        HEAD_HEIGHT = 70 * metrics.density
        SURPRISED_HEIGHT = 200 * metrics.density
        addHeaderView(createHeadView(context), HEAD_HEIGHT, SURPRISED_HEIGHT, this)
    }

    /**
     * 创建头部信息
     */
    private fun createHeadView(context: Context?): View {
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

    override fun startRefresh() {
        tvRefresh.text = "正在刷新"
    }

    override fun stopRefresh() {

    }

    override fun onRefreshEnable(status: Int) {
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

    override fun onPullDistance(distance: Float) {
    }

    companion object {
        var HEAD_HEIGHT = 0F //头部高度
        var SURPRISED_HEIGHT = 0F //下拉惊喜高度
    }
}