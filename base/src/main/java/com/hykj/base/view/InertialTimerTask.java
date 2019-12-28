package com.hykj.base.view;

import android.graphics.RectF;

import java.util.TimerTask;

/**
 * created by cjf
 * on: 2019/12/24
 * 惯性滚动的实现
 */
public class InertialTimerTask extends TimerTask {
    private static final int MAX_VELOCITY = 6000;
    private static final int DECLINE_VELOCITY = 20;
    private float firstVelocityY;//手指下滑>0,手指上滑<0
    private float firstVelocityX;//手指向右滑>0,手指向左滑<0
    private float currentVelocityY;
    private float currentVelocityX;
    private ZoomImageView zoomImageView;

    public InertialTimerTask(ZoomImageView zoomImageView, float velocityX, float velocityY) {
        this.zoomImageView = zoomImageView;
        this.firstVelocityX = velocityX;
        this.firstVelocityY = velocityY;
        currentVelocityY = Integer.MAX_VALUE;
        currentVelocityX = Integer.MAX_VALUE;
    }

    @Override
    public void run() {
        //开始任务的时候，为了防止闪动，对速度做一个限制。
        if (currentVelocityY == Integer.MAX_VALUE) {
            currentVelocityY = Math.abs(firstVelocityY) > MAX_VELOCITY ? firstVelocityY > 0 ? MAX_VELOCITY : -MAX_VELOCITY : firstVelocityY;
        }
        if (currentVelocityX == Integer.MAX_VALUE) {
            currentVelocityX = Math.abs(firstVelocityX) > MAX_VELOCITY ? firstVelocityX > 0 ? MAX_VELOCITY : -MAX_VELOCITY : firstVelocityX;
        }
        //判断是否结束某一方向滚动
        if (Math.abs(currentVelocityY) >= 0 && Math.abs(currentVelocityY) <= DECLINE_VELOCITY) {
            currentVelocityY = 0;
        }
        if (Math.abs(currentVelocityX) >= 0 && Math.abs(currentVelocityX) <= DECLINE_VELOCITY) {
            currentVelocityX = 0;
        }
        RectF rectF = zoomImageView.getMatrixRectF();
        //判断是否结束滚动。当水平、垂直速度均为0时，结束滚动；当滚动到顶部、底部、左侧边缘、右侧边缘时，结束滚动
        if ((currentVelocityX == 0 && currentVelocityY == 0) ||
                (((currentVelocityY > 0 && rectF.top == 0) || (currentVelocityY < 0 && rectF.bottom == zoomImageView.getHeight()))
                        && ((currentVelocityX > 0 && rectF.left == 0) || (currentVelocityX < 0 && rectF.right == zoomImageView.getWidth())))) {
            zoomImageView.cancelFuture();
        }
        //平滑滚动
        float dy = currentVelocityY / 100;
        float dx = currentVelocityX / 100;
        zoomImageView.smoothScroll(dx, dy);

        //逐渐递减速度
        if (currentVelocityY < 0) {
            currentVelocityY += DECLINE_VELOCITY;
        } else {
            currentVelocityY -= DECLINE_VELOCITY;
        }
        //逐渐递减速度
        if (currentVelocityX < 0) {
            currentVelocityX += DECLINE_VELOCITY;
        } else {
            currentVelocityX -= DECLINE_VELOCITY;
        }
    }
}
