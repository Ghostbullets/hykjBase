package com.hykj.base.base;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;

import com.hykj.base.R;
import com.hykj.base.view.TitleView;

public abstract class TitleActivity extends BaseActivity{
    protected TitleView mTitle;

    @Override
    protected void onCreateSub() {
        ViewGroup layout = (ViewGroup) View.inflate(mActivity, R.layout.layout_title_content,null);
        mTitle= (TitleView) layout.findViewById(R.id.title);
        View contentView = View.inflate(this,getLayoutId(),null);
        contentView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        layout.addView(contentView);
        setContentView(layout);
        View left = createTitleLeft();
        if (left!=null){
            mTitle.getLayoutLeft().removeAllViews();
            mTitle.getLayoutLeft().addView(left);
        }
        View right = createTitleRight();
        if (right!=null){
            mTitle.getLayoutRight().removeAllViews();
            mTitle.getLayoutRight().addView(right);
        }
        mTitle.setBackClickListener(new TitleView.BackClickListener() {
            @Override
            public void onBackClick(View v) {
                onTitleBackClick(v);
            }
        });

        if (checkTransStatus()){
            setTranslucentStatus(true);
        }
        init(mTitle);
    }

    @Override
    protected void setTranslucentStatus(boolean on) {
        super.setTranslucentStatus(on);
        mTitle.setTranslucentStatus(on);
        mTitle.setStatusBarBackgroundColor(on?getResources().getColor(R.color.gray_a8): Color.TRANSPARENT);
    }

    @Override
    protected void init() {

    }
    /**
     * 获取布局
     *
     * @return
     */
    protected abstract int getLayoutId();

    /**
     * 初始化
     */
    protected abstract void init(TitleView title);

    /**
     * 标题控件点击
     */
    protected void onTitleBackClick(View v) {
        onBackPressed();
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
