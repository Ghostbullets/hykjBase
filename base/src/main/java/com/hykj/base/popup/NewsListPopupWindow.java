package com.hykj.base.popup;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hykj.base.R;
import com.hykj.base.adapter.recyclerview2.BaseAdapter;
import com.hykj.base.adapter.recyclerview2.BaseViewHolder;
import com.hykj.base.adapter.recyclerview2.SimpleRecycleViewAdapter;

import java.util.List;

/**
 * 列表PopupWindow
 *
 * @param <T>
 */
public abstract class NewsListPopupWindow<T> extends BasePopupWindow {
    protected SimpleRecycleViewAdapter<T> popupAdapter;
    protected OnMenuItemClickListener<T> mListener;
    protected RecyclerView rvPopup;
    protected DividerItemDecoration decoration;

    public NewsListPopupWindow(Context context, List<T> list, int layoutResId) {
        this(context, null, list, layoutResId);
    }

    public NewsListPopupWindow(Context context, AttributeSet attrs, List<T> list, @LayoutRes int layoutResId) {
        super(context, attrs);
        popupAdapter = createPopupAdapter(context, list, layoutResId);
        initView(context);
    }

    private SimpleRecycleViewAdapter<T> createPopupAdapter(Context context, List<T> list, int layoutResId) {
        return new SimpleRecycleViewAdapter<T>(context, list, layoutResId) {
            @Override
            public void BindData(BaseViewHolder holder, T t, int position, @NonNull List<Object> payloads) {
                onBindData(holder, t, position, payloads);
            }
        };
    }

    protected void initView(Context context) {
        this.context = context;
        setContentView(LayoutInflater.from(context).inflate(R.layout.popup_window_list, null));
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);


        popupAdapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(BaseAdapter adapter, View view, int position) {
                if (mListener != null) {
                    mListener.onMenuItemClick(NewsListPopupWindow.this, view, popupAdapter, position);
                }
            }
        });
        rvPopup = getContentView().findViewById(R.id.rv_popup);
        rvPopup.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        decoration = new DividerItemDecoration(context, DividerItemDecoration.VERTICAL);
        decoration.setDrawable(context.getResources().getDrawable(R.drawable.divider_bg_v_1dp));
        rvPopup.addItemDecoration(decoration);
        rvPopup.setAdapter(popupAdapter);
    }

    @Override
    public void setHeight(int height) {
        super.setHeight(height);
        if (rvPopup != null) {
            ViewGroup.LayoutParams params = rvPopup.getLayoutParams();
            if (params != null) {
                params.height = height;
                rvPopup.setLayoutParams(params);
            }
        }
    }

    @Override
    public void setWidth(int width) {
        super.setWidth(width);
        if (rvPopup != null) {
            ViewGroup.LayoutParams params = rvPopup.getLayoutParams();
            if (params != null) {
                params.width = width;
                rvPopup.setLayoutParams(params);
            }
        }
    }

    public NewsListPopupWindow setOnMenuItemClickListener(OnMenuItemClickListener<T> onMenuItemClickListener) {
        this.mListener = onMenuItemClickListener;
        return this;
    }

    public interface OnMenuItemClickListener<T> {
        void onMenuItemClick(NewsListPopupWindow popupWindow, View view, SimpleRecycleViewAdapter<T> popupAdapter, int position);
    }

    /**
     * 导入数据
     *
     * @param list    新数据
     * @param isClear 是否清空原有数据
     */
    public void reloadListView(List<T> list, boolean isClear) {
        popupAdapter.reloadListView(list, isClear);
    }

    abstract void onBindData(BaseViewHolder holder, T t, int position, @NonNull List<Object> payloads);
}
