package com.phone.swipelayout.mode;

import android.view.View;

import com.phone.swipelayout.SwipeLayout;

/**
 * Created by Phone on 2017/4/20.
 */

public class PullOutMode extends SwipeMode {

	public PullOutMode(SwipeLayout swipeLayout) {
		super(swipeLayout);
	}

	@Override
	public void layout() {
		View leftView = swipeLayout.getLeftView();
		View rightView = swipeLayout.getRightView();
		if (leftView != null) {
			leftView.layout(-leftView.getMeasuredWidth(), 0, 0, leftView.getMeasuredHeight());
		}
		if (rightView != null) {
			rightView.layout(swipeLayout.getMeasuredWidth(), 0,
					swipeLayout.getMeasuredWidth() + rightView.getMeasuredWidth(), rightView.getMeasuredHeight());
		}
	}

	@Override
	public void offsetLeftAndRight(int offset) {
		View leftView = swipeLayout.getLeftView();
		View rightView = swipeLayout.getRightView();
		View mainView = swipeLayout.getMainView();
		if (mainView != null) {
			int originalX = 0;
			mainView.layout(originalX + offset, 0, originalX + offset + mainView.getMeasuredWidth(),
					mainView.getMeasuredHeight());
		}
		if (leftView != null) {
			int originalX = -leftView.getMeasuredWidth();
			leftView.layout(originalX + offset, 0, originalX + offset + leftView.getMeasuredWidth(),
					leftView.getMeasuredHeight());
		}
		if (rightView != null) {
			int originalX = swipeLayout.getMeasuredWidth();
			rightView.layout(originalX + offset, 0, originalX + offset + rightView.getMeasuredWidth(),
					rightView.getMeasuredHeight());
		}
		swipeLayout.invalidate();
	}

}
