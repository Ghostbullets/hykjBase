package com.hykj.base.listener;

/**
 * created by cjf
 * on: 2019/7/31
 * BaseFragment实现该接口，并且返回 HandleBackUtil.handleBackPress(this)，在需要处理返回事件的fragment中根据逻辑返回true or false
 */
public interface HandleBackInterface {
    boolean onBackPressed();
}
