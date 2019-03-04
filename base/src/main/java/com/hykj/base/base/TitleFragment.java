package com.hykj.base.base;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.hykj.base.R;
import com.hykj.base.view.TitleView;

/**
 * created by cjf
 * on:2019/2/26 17:57
 * 带标题的Fragment
 */
public abstract class TitleFragment extends BaseFragment {
    protected TitleView mTitle;
    protected View vDivider;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.layout_title_content, null);
        mTitle = mView.findViewById(R.id.title);
        vDivider=mView.findViewById(R.id.v_divider);
        View contentView = inflater.inflate(getLayoutId(), null);
        contentView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        ((ViewGroup) mView).addView(contentView);
        View left = createTitleLeft();
        if (left != null) {
            mTitle.getLayoutLeft().removeAllViews();
            mTitle.getLayoutLeft().addView(left);
        }
        View right = createTitleRight();
        if (right != null) {
            mTitle.getLayoutRight().removeAllViews();
            mTitle.getLayoutRight().addView(right);
        }
        mTitle.setBackClickListener(new TitleView.BackClickListener() {
            @Override
            public void onBackClick(View v) {
                onTitleBackClick(v);
            }
        });
        if (checkTransStatus()) {
            setTranslucentStatus(true);
        }
        return mView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(mTitle);
    }

    @Override
    protected void setTranslucentStatus(boolean on) {
        super.setTranslucentStatus(on);
        mTitle.setTranslucentStatus(on);
        mTitle.setStatusBarBackgroundColor(on ? getResources().getColor(R.color.gray_a8) : Color.TRANSPARENT);
    }

    @Override
    protected void init() {

    }

    /**
     * 隐藏分割线
     */
    protected void hideDivider() {
        vDivider.setVisibility(View.GONE);
    }

    /**
     * 设置分割线高度
     */
    protected void setDividerHeight(int dividerHeight) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) vDivider.getLayoutParams();
        params.height = dividerHeight;
        vDivider.setLayoutParams(params);
    }

    /**
     * 初始化
     */
    protected abstract void init(TitleView title);

    /**
     * 标题左边控件点击
     */
    protected void onTitleBackClick(View v) {
        mActivity.onBackPressed();
    }

    /**
     * 创建标题左侧
     *
     * @return
     */
    protected View createTitleLeft() {
        return null;
    }

    /**
     * 创建标题右侧
     *
     * @return
     */
    protected View createTitleRight() {
        return null;
    }
}
