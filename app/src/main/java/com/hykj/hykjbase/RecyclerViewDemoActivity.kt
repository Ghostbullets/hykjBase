package com.hykj.hykjbase


import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hykj.base.adapter.recyclerview2.BaseViewHolder
import com.hykj.base.adapter.recyclerview2.SimpleRecycleViewAdapter
import com.hykj.base.utils.text.Tip
import com.hykj.base.view.refresh.CustomizeSwipeRefreshAndLoadMoreLayout
import com.hykj.base.view.refresh.SimpleRefreshAndLoadMoreLayout
import java.util.ArrayList

class RecyclerViewDemoActivity : AppCompatActivity() {
    private var contentAdapter: SimpleRecycleViewAdapter<String>? = null
    private val contentList = ArrayList<String>()

    private lateinit var refreshLoadMoreLayout: SimpleRefreshAndLoadMoreLayout
    private val handler = Handler(Looper.getMainLooper())


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycler_view_demo)
        initView()
    }

    private fun initView() {
        for (i in 0 until 30) {
            contentList.add("这是第${i}个内容")
        }
        contentAdapter = createContentAdapter(contentList)

        val rvContent = findViewById<RecyclerView>(R.id.rv_content)
        rvContent.layoutManager = LinearLayoutManager(this)
        val decoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        decoration.setDrawable(resources.getDrawable(R.drawable.divider_trans_v_10dp))
        rvContent.addItemDecoration(decoration)
        rvContent.adapter = contentAdapter

        refreshLoadMoreLayout = findViewById(R.id.srl_refresh_load_more)
        refreshLoadMoreLayout.isCanPullSurprised = true
        //refreshLoadMoreLayout.isCanRefreshing=false
        refreshLoadMoreLayout.isCanLoadMore = false
        refreshLoadMoreLayout.setOnRefreshListener(object : CustomizeSwipeRefreshAndLoadMoreLayout.OnRefreshListener {
            override fun onRefresh() {
                handler.postDelayed({ refreshLoadMoreLayout.refreshFinish() }, 3000)

            }
        })
        refreshLoadMoreLayout.setOnLoadMoreListener(object : CustomizeSwipeRefreshAndLoadMoreLayout.OnLoadMoreListener {
            override fun onLoadMore() {
                handler.postDelayed({ refreshLoadMoreLayout.loadMoreFinish() }, 3000)
            }
        })
        refreshLoadMoreLayout.setOnPullSurprisedListener(object : CustomizeSwipeRefreshAndLoadMoreLayout.OnPullSurprisedListener {
            override fun onPullSurprised() {
                Tip.showShort("你打开了惊喜")
                handler.postDelayed({ refreshLoadMoreLayout.resetScroll() }, 1000)
            }
        })
    }

    private fun createContentAdapter(contentList: ArrayList<String>): SimpleRecycleViewAdapter<String>? {
        return object : SimpleRecycleViewAdapter<String>(this, contentList, R.layout.item_recycleview_text) {
            override fun BindData(holder: BaseViewHolder, item: String, position: Int, payloads: MutableList<Any>) {
                (holder.itemView as TextView).text = item
            }
        }
    }
}
