package com.hykj.base.base;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import android.view.ViewGroup;

/**
 * 不销毁而是显示隐藏
 */
public class BaseFragmentPagerAdapter extends FragmentPagerAdapter {
    private FragmentFactory factory;

    public BaseFragmentPagerAdapter(FragmentManager fm, FragmentFactory factory) {
        super(fm);
        this.factory = factory;
    }

    @Override
    public Fragment getItem(int position) {
        return factory.getFragment(position);
    }

    @Override
    public int getCount() {
        return factory.getCount();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        factory.getFragmentManager().beginTransaction().show(fragment).commit();
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        factory.getFragmentManager().beginTransaction().hide((Fragment) object).commit();
    }
}
