package com.hykj.base.popup;

import android.content.Context;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hykj.base.R;
import com.hykj.base.adapter.recyclerview2.BaseAdapter;
import com.hykj.base.adapter.recyclerview2.SimpleRecycleViewAdapter;

/**
 * 列表PopupWindow
 *
 * @param <T>
 */
public class ListPopupWindow<T> extends BasePopupWindow {
    protected SimpleRecycleViewAdapter<T> popupAdapter;
    protected OnMenuItemClickListener<T> mListener;
    protected RecyclerView rvPopup;
    protected DividerItemDecoration decoration;

    public ListPopupWindow(Context context, SimpleRecycleViewAdapter<T> popupAdapter) {
        this(context, null, popupAdapter);
    }

    public ListPopupWindow(Context context, AttributeSet attrs, SimpleRecycleViewAdapter<T> popupAdapter) {
        super(context, attrs);
        if (popupAdapter == null)
            throw new RuntimeException("请不要传入空的适配器");
        this.popupAdapter = popupAdapter;
        initView(context);
    }

    public void initView(Context context) {
        this.context = context;
        setContentView(LayoutInflater.from(context).inflate(R.layout.popup_window_list, null));
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);


        popupAdapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(BaseAdapter adapter, View view, int position) {
                if (mListener != null) {
                    mListener.onMenuItemClick(ListPopupWindow.this, view, popupAdapter, position);
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

    public ListPopupWindow setOnMenuItemClickListener(OnMenuItemClickListener<T> onMenuItemClickListener) {
        this.mListener = onMenuItemClickListener;
        return this;
    }

    public interface OnMenuItemClickListener<T> {
        void onMenuItemClick(ListPopupWindow popupWindow, View view, SimpleRecycleViewAdapter<T> popupAdapter, int position);
    }
}
