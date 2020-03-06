package com.hykj.base.view.refresh

/**
 * 自定义加载更多尾部控件监听
 * created by cjf
 * on: 2020/3/5
 */
interface ICustomizeSwipeLoadMoreFooterListener {
    /**
     * 开始加载
     */
    fun startLoadMore()

    /**
     * 结束加载
     */
    fun stopLoadMore()

    /**
     * 是否可以开始加载更多
     */
    fun onLoadMoreEnable(enable: Boolean)
}