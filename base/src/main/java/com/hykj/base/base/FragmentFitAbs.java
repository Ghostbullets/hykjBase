package com.hykj.base.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Activity+Fragment形式工具类
 *
 * @param <T>
 */
public abstract class FragmentFitAbs<T extends Serializable> {
    private static final String EX_STR_CURRENT_KEY = "currentKey";

    private Map<String, ParFragment> fragmentMap = new HashMap<>();
    private FragmentManager fragmentManager;
    private int containerViewId;

    private T currentKey;
    private Fragment currentFragment;


    public FragmentFitAbs(FragmentManager fragmentManager, int containerViewId) {
        this.fragmentManager = fragmentManager;
        this.containerViewId = containerViewId;
    }

    public void register(Class<? extends Fragment> cls) {
        register(cls, null);
    }

    /**
     * 注册
     *
     * @param cls      Class
     * @param listener 注册监听，用于设置setArguments
     */
    public void register(Class<? extends Fragment> cls, OnRegisterListener listener) {
        fragmentMap.put(cls.getName(), new ParFragment(cls, listener));
    }

    /**
     * @param value 根据key显示Fragment
     */
    public void showFragment(T value) {
        Fragment fragment = getFragment(value);
        if (fragment != null) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            List<Fragment> fragments = fragmentManager.getFragments();
            for (Fragment item : fragments) {
                if (item != fragment)
                    transaction.hide(item);
            }
            if (fragment.isAdded()) {
                transaction.show(fragment);
            } else {
                transaction.add(containerViewId, fragment, getKey(value));
            }
            currentKey = value;
            currentFragment = fragment;
            transaction.commit();
        }
    }


    /**
     * 根据value 得到fragment
     *
     * @param value
     * @return
     */
    public Fragment getFragment(T value) {
        ParFragment parFragment = fragmentMap.get(getKey(value));
        if (parFragment != null) {
            if (parFragment.fragment == null) {
                try {
                    parFragment.fragment = parFragment.cls.newInstance();
                    if (parFragment.listener != null)
                        parFragment.listener.initFragment(parFragment.fragment);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return parFragment.fragment;
        }
        return null;
    }

    /**
     * 保存Fragment信息
     *
     * @param outState
     */
    public void saveFragment(Bundle outState) {
        if (outState != null) {
            for (String key : fragmentMap.keySet()) {
                ParFragment parFragment = fragmentMap.get(key);
                if (parFragment != null && parFragment.fragment != null && parFragment.fragment.isAdded()) {
                    fragmentManager.putFragment(outState, key, parFragment.fragment);
                }
            }
            if (currentKey != null) outState.putSerializable(EX_STR_CURRENT_KEY, currentKey);
        }
    }

    public void restoreFragment(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            for (String key : fragmentMap.keySet()) {
                Fragment fragment = fragmentManager.getFragment(savedInstanceState, key);
                ParFragment parFragment = fragmentMap.get(key);
                if (fragment != null && parFragment != null) {
                    parFragment.fragment = fragment;
                }
            }
            currentKey = (T) savedInstanceState.getSerializable(EX_STR_CURRENT_KEY);
        }
    }

    public abstract String getKey(T value);


    public FragmentManager getFragmentManager() {
        return fragmentManager;
    }

    public T getCurrentKey() {
        return currentKey;
    }

    public Fragment getCurrentFragment() {
        return currentFragment;
    }

    public int getCount() {
        return fragmentMap.size();
    }

    public interface OnRegisterListener {
        void initFragment(Fragment fragment);
    }

    /**
     * 实体类
     */
    public class ParFragment {
        public Class<? extends Fragment> cls;
        public OnRegisterListener listener;
        public Fragment fragment;

        public ParFragment(Class<? extends Fragment> cls, OnRegisterListener listener) {
            this.cls = cls;
            this.listener = listener;
        }
    }
}
