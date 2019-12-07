package com.hykj.base.utils;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.hykj.base.listener.HandleBackInterface;

import java.util.List;

/**
 * created by cjf
 * on: 2019/7/31
 * 用于在fragment内处理back按键事件
 */
public class HandleBackUtils {

    /**
     * 将back事件分发给 FragmentManager 中管理的子Fragment，如果该 FragmentManager 中的所有Fragment都
     * 没有处理back事件，则尝试 FragmentManager.popBackStack()
     *
     * @return 如果处理了back键则返回 <b>true</b>
     * @see #handleBackPress(Fragment)
     * @see #handleBackPress(FragmentActivity)
     */
    public static boolean handleBackPress(FragmentManager fm) {
        List<Fragment> fragments = fm.getFragments();
        for (Fragment fragment : fragments) {
            if (isFragmentBackHandled(fragment))
                return true;
        }
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
            return true;
        }
        return false;
    }

    public static boolean handleBackPress(Fragment fragment) {
        return handleBackPress(fragment.getChildFragmentManager());
    }

    public static boolean handleBackPress(FragmentActivity activity) {
        return handleBackPress(activity.getSupportFragmentManager());
    }

    /**
     * 判断Fragment是否处理了Back键
     *
     * @return 如果处理了back键则返回 <b>true</b>
     */
    public static boolean isFragmentBackHandled(Fragment fragment) {
        return fragment != null && fragment.isVisible() && fragment.getUserVisibleHint()
                && fragment instanceof HandleBackInterface && ((HandleBackInterface) fragment).onBackPressed();
    }
}
