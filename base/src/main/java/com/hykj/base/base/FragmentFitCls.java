package com.hykj.base.base;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

public class FragmentFitCls extends FragmentFitAbs<Class<? extends Fragment>> {
    public FragmentFitCls(FragmentManager fragmentManager, int containerViewId) {
        super(fragmentManager, containerViewId);
    }

    @Override
    public String getKey(Class<? extends Fragment> value) {
        return value.getName();
    }
}
