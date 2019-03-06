package com.hykj.base.bean;

/**
 * 分页加载帮助类
 */
public class PageInfo {
    private int pageSize = 10;//一次请求数量
    private int pageNo = 1;//分页页码
    private boolean isLoading;//是否正在加载更多
    private boolean isHasNext;//是否还有下一个

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    public boolean isHasNext() {
        return isHasNext;
    }

    public void setHasNext(boolean hasNext) {
        isHasNext = hasNext;
    }

    public void setHasNext(Integer total) {
        this.isHasNext = total != null && pageNo * pageSize < total;
    }

    public boolean isCanLoadMore() {
        return isHasNext && !isLoading;
    }

    //初始化数据
    public void init() {
        isLoading = false;
        isHasNext = false;
        pageNo = 1;
    }

    //下一页
    public void next() {
        pageNo++;
        isLoading = true;
    }

    //是否清空数据，根据页码是不是1来判断
    public boolean isClear() {
        return pageNo == 1;
    }
}
