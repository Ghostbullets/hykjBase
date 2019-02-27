package com.hykj.base.adapter.recyclerview2;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * 万能适配器RecycleView
 * Created by Administrator on 2018/1/11.
 */

public abstract class BaseAdapter<T, H extends BaseViewHolder> extends RecyclerView.Adapter<BaseViewHolder> {
    protected List<T> mDatas;
    protected Context mContext;
    protected View mView;
    protected int mItemLayoutId;
    protected OnItemClickListener mListener;

    public BaseAdapter(Context context, List<T> datas, int layoutResId) {
        this.mContext = context;
        this.mDatas = datas;
        this.mItemLayoutId = layoutResId;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mView = LayoutInflater.from(this.mContext).inflate(mItemLayoutId, parent, false);
        return new BaseViewHolder(this, mView, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {

    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
        T t = this.getItem(position);//因为不知道具体类型，写个抽象方法
        BindData(holder, t, position, payloads);//需要传的是t对象，因为数据在t里面，而不是传position
    }

    public abstract void BindData(BaseViewHolder holder, T t, int position, @NonNull List<Object> payloads);

    @Override
    public int getItemCount() {
        return mDatas != null ? mDatas.size() : 0;
    }

    public T getItem(int position) {//提供方法，获取当前position的对象
        return mDatas.get(position);
    }

    /**
     * 导入数据
     *
     * @param list    新数据
     * @param isClear 是否清空原有数据
     */
    public void reloadListView(List<T> list, boolean isClear) {
        if (isClear) {
            this.mDatas.clear();
        }
        if (list != null) {
            this.mDatas.addAll(list);
        }
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        mListener = itemClickListener;
    }

    public interface OnItemClickListener {
        void OnItemClick(BaseAdapter adapter, View view, int position);
    }
}
