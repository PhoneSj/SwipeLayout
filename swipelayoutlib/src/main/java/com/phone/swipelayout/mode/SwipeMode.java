package com.phone.swipelayout.mode;

import android.view.View;

import com.phone.swipelayout.LogUtils;
import com.phone.swipelayout.SwipeLayout;

import static com.phone.swipelayout.SwipeLayout.DragEdge.Empty;

/**
 * Created by Phone on 2017/4/20.
 */

public abstract class SwipeMode {

	private final String TAG = "SwipeMode";
	private static final boolean TAG_ENABLE = true;
	/** 菜单关闭时控件偏移量 **/
	int mDragCloseDistance = 0;
	/** 菜单打开时控件偏移量 **/
	int mDragOpenDistance = 0;
	/** 当前是打开状态拉动控件，若当前显示比例为0.75则松手后进入打开状态 **/
	float mWillOpenPercentAfterOpen = 0.75f;
	/** 当前是关闭状态拉动控件，若当前显示比例为0.25则松手后进入打开状态 **/
	float mWillOpenPercentAfterClose = 0.25f;

	protected SwipeLayout swipeLayout;

	public SwipeMode(SwipeLayout swipeLayout) {
		this.swipeLayout = swipeLayout;
	}

	public abstract void layout();

	public void onPull() {
		LogUtils.showI(TAG, "onPull...", TAG_ENABLE);
		View leftView = swipeLayout.getLeftView();
		View rightView = swipeLayout.getRightView();
		switch (swipeLayout.getCurrentDragEdge()) {
			case Left: {
				if (leftView != null) {
					//left方向的滑动范围：0~leftView.getMeasuredWidth()
					mDragCloseDistance = 0;
					mDragOpenDistance = leftView.getMeasuredWidth();
					swipeLayout.mCurrentOffset = Math.min(Math.max(swipeLayout.mCurrentOffset, mDragCloseDistance),
							mDragOpenDistance);
				}
				break;
			}
			case Right: {
				if (rightView != null) {
					//right方向的滑动范围：-rightView.getMeasuredHeight()~0
					mDragCloseDistance = 0;
					mDragOpenDistance = -rightView.getMeasuredWidth();
					swipeLayout.mCurrentOffset = Math.min(Math.max(swipeLayout.mCurrentOffset, mDragOpenDistance),
							mDragCloseDistance);
				}
				break;
			}
			case Empty:

				break;
		}
	}

	public void onRelease() {
		LogUtils.showI(TAG, "onRelease...", TAG_ENABLE);
		View mainView = swipeLayout.getMainView();
		if (mainView == null) {
			return;
		}
		if (swipeLayout.getCurrentDragEdge() != Empty) {
			//拖动偏移量百分比打开菜单阈值
			float willOpenPercent = (swipeLayout.isCloseBeforeDragged() ? mWillOpenPercentAfterClose
					: mWillOpenPercentAfterOpen);
			//			float willOpenPercent = mWillOpenPercent;
			//当前拖动偏移量
			int dragedDistance = swipeLayout.mCurrentOffset - mDragCloseDistance;
			//最大偏移量
			int totalDistance = mDragOpenDistance - mDragCloseDistance;
			float openPercent = Math.abs(dragedDistance * 1.0f / totalDistance);
			if (openPercent > willOpenPercent) {
				openMenu();
			} else {
				closeMenu();
			}
		}
	}

	public abstract void offsetLeftAndRight(int offset);

	public void openMenu() {
		LogUtils.showI(TAG, "openMenu...", TAG_ENABLE);
		View leftView = swipeLayout.getLeftView();
		View rightView = swipeLayout.getRightView();
		if (swipeLayout.getCurrentDragEdge() == SwipeLayout.DragEdge.Left) {
			if (leftView != null) {
				swipeLayout.smoothScrollTo(SwipeLayout.Status.Open, leftView.getMeasuredWidth());
			}
		} else if (swipeLayout.getCurrentDragEdge() == SwipeLayout.DragEdge.Right) {
			if (rightView != null) {
				swipeLayout.smoothScrollTo(SwipeLayout.Status.Open, -rightView.getMeasuredWidth());
			}
		}
	}

	public void closeMenu() {
		LogUtils.showI(TAG, "closeMenu...", TAG_ENABLE);
		swipeLayout.smoothScrollTo(SwipeLayout.Status.Close, 0);
	}

}
