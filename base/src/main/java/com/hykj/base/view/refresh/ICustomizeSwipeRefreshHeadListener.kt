package com.hykj.base.view.refresh

/**
 * 自定义刷新头部控件监听
 * created by cjf
 * on: 2020/1/20
 */
interface ICustomizeSwipeRefreshHeadListener {
    /**
     * 开始刷新
     */
    fun startRefresh()

    /**
     * 结束刷新
     */
    fun stopRefresh()

    /**
     * 是否可以开始刷新
     */
    fun onRefreshEnable(@CustomizeSwipeRefreshAndLoadMoreLayout.RefreshStatus status: Int)
}