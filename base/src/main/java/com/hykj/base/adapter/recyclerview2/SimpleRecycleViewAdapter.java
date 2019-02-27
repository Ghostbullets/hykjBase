package com.hykj.base.adapter.recyclerview2;

import android.content.Context;

import java.util.List;

/**万能适配RecycleView一个item
 * Created by Administrator on 2018/1/11.
 */

public abstract class SimpleRecycleViewAdapter<T> extends BaseAdapter<T,BaseViewHolder> {

    protected SimpleRecycleViewAdapter(Context context, List<T> datas, int layoutResId) {
        super(context, datas, layoutResId);
    }
}
