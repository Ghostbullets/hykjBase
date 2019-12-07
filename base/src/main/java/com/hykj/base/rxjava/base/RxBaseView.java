package com.hykj.base.rxjava.base;

import com.trello.rxlifecycle3.android.ActivityEvent;
import com.trello.rxlifecycle3.android.FragmentEvent;
import com.trello.rxlifecycle3.LifecycleTransformer;

/**
 * created by cjf
 * on:2019/4/13 17:06
 */
public interface RxBaseView {
    /**
     * 绑定生命周期
     *
     * @param <T>
     * @param obj 在Activity页面调用时请传入{@link ActivityEvent}，在Fragment碎片调用时请传入{@link FragmentEvent}
     * @return
     */
    <T> LifecycleTransformer<T> bindToLife(Object obj);
}
