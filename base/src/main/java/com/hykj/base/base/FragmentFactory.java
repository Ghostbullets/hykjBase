package com.hykj.base.base;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.SparseArray;

import java.util.HashMap;
import java.util.Map;

/**
 * 用于ViewPager+Fragment使用的管理工具类
 */
public class FragmentFactory {
    private Map<String, ParFragment> fragmentMap = new HashMap<>();
    private SparseArray<String> positionList = new SparseArray<>();
    private FragmentManager fragmentManager;
    private Fragment currentFragment;

    public FragmentFactory(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    public void register(Class<? extends Fragment> cls, int position) {
        register(cls, position, null);
    }

    /**
     * 注册
     *
     * @param cls      Class
     * @param listener 注册监听，用于设置setArguments
     */
    public void register(Class<? extends Fragment> cls, int position, OnRegisterListener listener) {
        fragmentMap.put(cls.getSimpleName(), new ParFragment(cls, listener));
        positionList.put(position, cls.getName());
    }

    /**
     * 根据key得到fragment
     *
     * @param position
     * @return
     */
    public Fragment getFragment(int position) {
        String key = positionList.get(position);
        ParFragment parFragment = fragmentMap.get(key);
        if (parFragment != null) {
            Fragment fragment = parFragment.fragment;
            if (fragment == null) {
                try {
                    fragment = parFragment.cls.newInstance();
                    if (parFragment.listener != null) {
                        parFragment.listener.initFragment(fragment);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            currentFragment = fragment;
            return fragment;
        }
        return null;
    }

    public int getCount(){
        return fragmentMap.size();
    }

    public FragmentManager getFragmentManager() {
        return fragmentManager;
    }

    public Fragment getCurrentFragment() {
        return currentFragment;
    }

    public interface OnRegisterListener {
        void initFragment(Fragment fragment);
    }

    /**
     * 实体类
     */
    public class ParFragment {
        Class<? extends Fragment> cls;
        OnRegisterListener listener;
        Fragment fragment;

        public ParFragment(Class<? extends Fragment> cls, OnRegisterListener listener) {
            this.cls = cls;
            this.listener = listener;
        }
    }
}
