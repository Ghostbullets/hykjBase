package com.hykj.base.view.refresh

import android.content.Context
import android.os.Build
import android.support.annotation.FloatRange
import android.support.annotation.IntDef
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.view.animation.Interpolator
import android.widget.AbsListView
import android.widget.LinearLayout
import android.widget.OverScroller
import kotlin.math.abs

/**
 * 自定义刷新、加载更多容器
 * created by cjf
 * on: 2020/1/20
 */
open class CustomizeSwipeRefreshAndLoadMoreLayout : ViewGroup {
    private var target: View? = null //the target of the gesture
    private var lastY: Float = 0f //纪录坐标
    private var isBeginDragged = false // 是否已经在拖拽
    private var interceptLastY = 0f  // 纪录判断拦截用坐标
    private var touchSlop: Int = 0 // 灵敏度

    private var scroller: OverScroller //滚动辅助
    private var pullDistanceListeners = arrayListOf<OnPullDistanceListener>() //拉动监听集合

    // 头部数据
    private var headerContainer: LinearLayout = LinearLayout(context) // 头部容器
    private var headerListener: ICustomizeSwipeRefreshHeadListener? = null // 头部控件监听
    private var refreshLimit: Float = -1f //开始刷新点
    private var isRefreshing = false //是否正在刷新
    var isCanRefreshing = true //是否可以下拉刷新，false则下拉不显示内容
    private var refreshListener: OnRefreshListener? = null// 刷新监听

    private var surprisedLimit: Float = -1f //开始惊喜点
    var isCanPullSurprised = false //是否存在下拉惊喜,类似京东app首页,默认不存在
    private var pullSurprisedListener: OnPullSurprisedListener? = null //下拉惊喜监听

    // 尾部数据
    private var footerContainer: LinearLayout = LinearLayout(context) //尾部容器
    private var footerListener: ICustomizeSwipeLoadMoreFooterListener? = null // 尾部控件监听
    private var loadMoreLimit: Float = -1f //开始加载更多点
    private var isLoadMore = false //是否正在加载更多
    var isCanLoadMore = true //是否可以加载更多,当没有下一页数据时，请设置该值为false
    private var loadMoreListener: OnLoadMoreListener? = null //加载更多监听

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        headerContainer.gravity = Gravity.BOTTOM
        addView(headerContainer)

        footerContainer.gravity = Gravity.TOP
        addView(footerContainer)


        touchSlop = ViewConfiguration.get(context).scaledTouchSlop

        scroller = OverScroller(context, Interpolator { input ->
            val t = input - 1.0f
            t * t * t * t * t + 1.0f
        })
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (target == null) {
            ensureTarget()
        }
        if (target == null)
            return
        target?.measure(
                MeasureSpec.makeMeasureSpec(measuredWidth - paddingLeft - paddingRight, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(measuredHeight - paddingTop - paddingBottom, MeasureSpec.EXACTLY))
        headerContainer.measure(
                MeasureSpec.makeMeasureSpec(measuredWidth - paddingLeft - paddingRight, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(measuredHeight, MeasureSpec.EXACTLY))
        footerContainer.measure(
                MeasureSpec.makeMeasureSpec(measuredWidth - paddingLeft - paddingRight, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(measuredHeight, MeasureSpec.EXACTLY))
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val width = measuredWidth
        val height = measuredHeight
        if (childCount == 0)
            return
        if (target == null) {
            ensureTarget()
        }
        if (target == null) {
            return
        }
        target?.let {
            val childWidth = width - paddingLeft - paddingRight
            val childHeight = height - paddingTop - paddingBottom
            //更新目标布局的位置
            it.layout(paddingLeft, paddingTop, paddingLeft + childWidth, paddingTop + childHeight)
            // 更新头部容器的位置
            headerContainer.layout(paddingLeft, -height, paddingLeft + childWidth, 0)
            // 更新尾部容器的位置
            footerContainer.layout(paddingLeft, height, paddingLeft + childWidth, 2 * height)
        }
    }

    /**
     * 确定目标容器
     */
    private fun ensureTarget() {
        if (target == null) {
            for (i in 0 until childCount) {
                val child = getChildAt(i)
                if (child != headerContainer && child != footerContainer) {
                    target = child
                    break
                }
            }
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        if (isRefreshing)
            return true
        if (isLoadMore)
            return true
        //不可以用状态
        if (!isEnabled)
            return false
        ev?.let { event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                // 纪录位置
                interceptLastY = event.y
            }
            val stayTop = canChildScrollUp()
            val stayBottom = canChildScrollDown()
            when (event.action) {
                MotionEvent.ACTION_MOVE, MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    val offsetY = event.y - interceptLastY
                    if (abs(offsetY) < touchSlop) {
                        isBeginDragged = false
                        return false
                    }
                    return if (offsetY > 0 && stayTop) {
                        true
                    } else if (offsetY < 0 && stayBottom) {
                        true
                    } else {
                        isBeginDragged = false
                        false
                    }
                }
                else -> {
                }
            }

        } ?: return false
        return false
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (isRefreshing)
            return false
        if (isLoadMore)
            return false
        event?.let { ev ->
            when (ev.action) {
                MotionEvent.ACTION_DOWN -> {//按下
                    //纪录起始点
                    lastY = ev.y
                    return true
                }
                MotionEvent.ACTION_MOVE -> {//移动
                    if (!isBeginDragged) {// 并不在拖拽,则纪录起始点,并终止滚动动画
                        lastY = ev.y
                        isBeginDragged = true
                        scroller.abortAnimation()
                    } else {
                        // 计算移动距离
                        val offsetY = (ev.y - lastY) * if (canChildScrollUp()) HEADER_PULL_RESISTANCE else if (canChildScrollDown()) FOOTER_PULL_RESISTANCE else 0f
                        // 移动位置
                        target?.translationY = offsetY
                        //刷新
                        if (canChildScrollUp()) {
                            headerContainer.translationY = offsetY
                            headerListener?.onRefreshEnable(if (offsetY > surprisedLimit) SURPRISED else if (offsetY > refreshLimit) REFRESH else NOT_REFRESH)
                        }
                        //加载更多
                        if (canChildScrollDown()) {
                            footerContainer.translationY = offsetY
                            footerListener?.onLoadMoreEnable(-offsetY > loadMoreLimit)
                        }
                        //拉动
                        for (listener in pullDistanceListeners) {
                            listener.onPullDistance(offsetY)
                        }
                    }
                }
                MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {// 取消或者抬起
                    // 计算移动距离
                    val offsetY = (ev.y - lastY) * if (canChildScrollUp()) HEADER_PULL_RESISTANCE else if (canChildScrollDown()) FOOTER_PULL_RESISTANCE else 0f
                    if (offsetY > 0 && offsetY > refreshLimit && refreshListener != null && isCanRefreshing) {
                        if (offsetY > surprisedLimit && isCanPullSurprised) { //下拉惊喜
                            val dy = target?.translationY?.toInt() ?: 0
                            scroller.startScroll(0, dy, 0, measuredHeight - dy, ANIMATE_TO_START_DURATION)
                            invalidate()
                        } else {//下拉刷新
                            val dy = target?.translationY?.toInt() ?: 0
                            scroller.startScroll(0, dy, 0, -dy + refreshLimit.toInt(), ANIMATE_TO_START_DURATION)
                            invalidate()

                            isRefreshing = true
                            //开始刷新
                            refreshListener?.onRefresh()
                            headerListener?.startRefresh()
                        }
                    } else if (offsetY < 0 && abs(offsetY) > loadMoreLimit && loadMoreListener != null && isCanLoadMore) {
                        val dy = target?.translationY?.toInt() ?: 0
                        scroller.startScroll(0, dy, 0, -dy - loadMoreLimit.toInt(), ANIMATE_TO_START_DURATION)
                        invalidate()

                        isLoadMore = true
                        //开始加载更多
                        loadMoreListener?.onLoadMore()
                        footerListener?.startLoadMore()
                    } else {// 回弹
                        val dy = target?.translationY?.toInt() ?: 0
                        scroller.startScroll(0, dy, 0, -dy, ANIMATE_TO_START_DURATION)
                        invalidate()
                    }
                }
                else -> {
                }
            }
        }

        return super.onTouchEvent(event)
    }

    /**
     * @return Whether it is possible for the child view of this layout to
     * scroll up. Override this if the child view is a custom view.
     */
    fun canChildScrollUp(): Boolean {
        if (target is RecyclerView) {
            val recyclerView = target as RecyclerView
            val layoutManager = recyclerView.layoutManager ?: return true
            if (recyclerView.childCount == 0)
                return true
            val isTop = when (layoutManager::class.java) {
                GridLayoutManager::class.java -> {
                    (layoutManager as GridLayoutManager).findFirstCompletelyVisibleItemPosition() == 0
                }
                LinearLayoutManager::class.java -> {
                    (layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition() == 0
                }
                StaggeredGridLayoutManager::class.java -> {
                    (layoutManager as StaggeredGridLayoutManager).findFirstCompletelyVisibleItemPositions(null)[0] == 0
                }
                else -> false
            }
            return isTop && recyclerView.getChildAt(0).top == recyclerView.paddingTop
        }

        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            if (target is AbsListView) {
                val absListView = target as AbsListView
                absListView.childCount > 0 && (absListView.firstVisiblePosition > 0 ||
                        absListView.getChildAt(0).top < absListView.paddingTop)
            } else {
                target?.run {
                    canScrollVertically(-1) && scrollY > 0
                } ?: false
            }
        } else {
            target?.canScrollVertically(-1) ?: false
        }
    }

    /**
     * @return Whether it is possible for the child view of this layout to
     * scroll down. Override this if the child view is a custom view.
     */
    fun canChildScrollDown(): Boolean {
        if (target is RecyclerView) {
            val recyclerView = target as RecyclerView
            val layoutManager = recyclerView.layoutManager ?: return false
            val itemCount = recyclerView.adapter?.itemCount ?: 0
            if (itemCount == 0)
                return false
            val isBottom = when (layoutManager::class.java) {
                GridLayoutManager::class.java -> {
                    (layoutManager as GridLayoutManager).findLastCompletelyVisibleItemPosition() == itemCount - 1
                }
                LinearLayoutManager::class.java -> {
                    (layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition() == itemCount - 1
                }
                StaggeredGridLayoutManager::class.java -> {
                    val manager = layoutManager as StaggeredGridLayoutManager
                    manager.findLastCompletelyVisibleItemPositions(null)[manager.spanCount - 1] == itemCount - 1
                }
                else -> false
            }
            return isBottom /*&& recyclerView.getChildAt(recyclerView.childCount - 1).bottom == recyclerView.bottom - recyclerView.paddingBottom*/
        }
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            if (target is AbsListView) {
                val absListView = target as AbsListView
                val childCount = absListView.childCount
                absListView.childCount > 0 && (absListView.lastVisiblePosition < childCount - 1 ||
                        absListView.getChildAt(childCount - 1).bottom > absListView.paddingBottom)
            } else {
                target?.run {
                    canScrollVertically(-1) && scrollY > 0
                } ?: false
            }
        } else {
            target?.canScrollVertically(-1) ?: false
        }
    }

    override fun computeScroll() {
        if (scroller.computeScrollOffset()) {
            val currY = scroller.currY.toFloat()
            Log.e(CustomizeSwipeRefreshAndLoadMoreLayout::class.java.simpleName, "scroller currY=$currY")

            target?.translationY = currY
            if (canChildScrollUp()) {
                headerContainer.translationY = currY
            }
            if (canChildScrollDown()) {
                footerContainer.translationY = currY
            }
            for (listener in pullDistanceListeners) {
                listener.onPullDistance(currY)
            }
            postInvalidate()
        } else {
            //当最终滚动偏移量=measuredHeight时,执行下拉惊喜,需要跳到另一个activity，并在之后执行恢复滚动，即重置操作
            //当下拉惊喜监听=null时，直接重置
            if (scroller.finalY == measuredHeight) {
                pullSurprisedListener?.onPullSurprised() ?: resetScroll()
            }
        }
        super.computeScroll()
    }

    /**
     * 执行恢复滚动，即重置操作
     */
    fun resetScroll() {
        if (scroller.finalY == measuredHeight) {
            scroller.startScroll(0, measuredHeight, 0, -measuredHeight, ANIMATE_TO_START_DURATION)
            postInvalidate()
        }
    }

    /**
     * 追加尾部控件
     *
     * @param view         控件
     * @param loadMoreLimit 加载更多距离值
     * @param listener     监听
     */
    fun addFooterView(view: View, loadMoreLimit: Float, listener: ICustomizeSwipeLoadMoreFooterListener) {
        this.footerContainer.removeAllViews()
        this.footerContainer.addView(view)
        this.footerListener = listener
        this.loadMoreLimit = loadMoreLimit
    }

    /**
     * 手动调用,结束加载更多
     */
    fun loadMoreFinish() {
        if (isLoadMore) {
            //停止尾部刷新
            footerListener?.stopLoadMore()
            val dy = target?.translationY?.toInt() ?: 0
            scroller.startScroll(0, dy, 0, -dy, ANIMATE_TO_START_DURATION)
            invalidate()
            isLoadMore = false
        }
    }

    /**
     * 手动调用,开启加载更多动作
     */
    fun doLoadMore() {
        if (isCanLoadMore) {
            scroller.startScroll(0, -1, 0, 1 + loadMoreLimit.toInt(), ANIMATE_TO_START_DURATION)
            invalidate()
            // 开始加载更多
            isLoadMore = true
            footerListener?.startLoadMore()
            loadMoreListener?.onLoadMore()
        }
    }

    /**
     * 追加头部控件
     *
     * @param view         控件
     * @param refreshLimit 刷新距离值
     * @param listener     监听
     */
    fun addHeaderView(view: View, refreshLimit: Float, surprisedLimit: Float, listener: ICustomizeSwipeRefreshHeadListener) {
        this.headerContainer.removeAllViews()
        this.headerContainer.addView(view)
        this.headerListener = listener
        this.refreshLimit = refreshLimit
        this.surprisedLimit = surprisedLimit
    }

    /**
     * 手动调用,结束刷新
     */
    fun refreshFinish() {
        if (isRefreshing) {
            headerListener?.stopRefresh()
            // 开始回弹
            val dy = target?.translationY?.toInt() ?: 0
            scroller.startScroll(0, dy, 0, -dy, ANIMATE_TO_START_DURATION)
            invalidate()
            isRefreshing = false
        }
    }

    /**
     * 手动调用,开启刷新动作
     */
    fun doRefresh() {
        if (isCanRefreshing) {
            scroller.startScroll(0, 1, 0, -1 + refreshLimit.toInt(), ANIMATE_TO_START_DURATION)
            invalidate()
            isRefreshing = true
            // 开始刷新
            refreshListener?.onRefresh()
            headerListener?.startRefresh()
        }
    }

    /**
     * 设置刷新监听
     *
     * @param listener
     */
    fun setOnRefreshListener(listener: OnRefreshListener?) {
        this.refreshListener = listener
    }

    /**
     * 设置加载更多监听
     *
     * @param listener
     */
    fun setOnLoadMoreListener(listener: OnLoadMoreListener?) {
        this.loadMoreListener = listener
    }

    /**
     * 设置下拉惊喜监听
     * @param listener
     */
    fun setOnPullSurprisedListener(listener: OnPullSurprisedListener?) {
        this.pullSurprisedListener = listener
    }

    /**
     * 添加某个拉动监听
     *
     * @param listener
     */
    fun addOnPullDistanceListener(listener: OnPullDistanceListener) {
        this.pullDistanceListeners.add(listener)
    }

    /**
     * 移除某个拉动监听
     *
     * @param listener
     */
    fun removeOnPullDistanceListener(listener: OnPullDistanceListener) {
        this.pullDistanceListeners.remove(listener)
    }

    /**
     * 设置灵敏度
     *
     * @param sensitivity
     */
    fun setSensitivity(@FloatRange(from = 0.01, to = 10.00) sensitivity: Float) {
        touchSlop *= (1 / sensitivity).toInt()
    }

    companion object {
        const val HEADER_PULL_RESISTANCE = 0.7F//头部拉动阻力
        const val FOOTER_PULL_RESISTANCE = 0.3F //尾部拉动阻力
        const val ANIMATE_TO_START_DURATION = 600//动画启动持续时间

        const val NOT_REFRESH = 0
        const val REFRESH = 1
        const val SURPRISED = 2
    }

    //刷新状态，NOT_REFRESH 未达到刷新；REFRESH 已达到刷新；SURPRISED 已达到下拉惊喜
    @Retention(AnnotationRetention.SOURCE)
    @IntDef(NOT_REFRESH, REFRESH, SURPRISED)
    annotation class RefreshStatus

    /**
     * 刷新监听
     */
    interface OnRefreshListener {
        fun onRefresh()
    }

    /**
     * 加载更多监听
     */
    interface OnLoadMoreListener {
        fun onLoadMore()
    }

    /**
     * 下拉惊喜监听
     */
    interface OnPullSurprisedListener {
        /**
         * 请在执行完这个方法跳到其他activity后,调用resetScroll()方法恢复偏移
         */
        fun onPullSurprised()
    }

    /**
     * 拉动距离监听
     */
    interface OnPullDistanceListener {
        fun onPullDistance(distance: Float)
    }
}