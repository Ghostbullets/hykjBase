package com.hykj.base.base;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class FragmentFitCls extends FragmentFitAbs<Class<? extends Fragment>> {
    public FragmentFitCls(FragmentManager fragmentManager, int containerViewId) {
        super(fragmentManager, containerViewId);
    }

    @Override
    public String getKey(Class<? extends Fragment> value) {
        return value.getName();
    }
}
