package com.phone.swipelayout.mode;

import android.view.View;

import com.phone.swipelayout.SwipeLayout;

/**
 * Created by Phone on 2017/4/21.
 */

public class StretchMode extends SwipeMode {

	public StretchMode(SwipeLayout swipeLayout) {
		super(swipeLayout);
	}

	@Override
	public void layout() {
		View leftView = swipeLayout.getLeftView();
		View rightView = swipeLayout.getRightView();
		if (leftView != null) {
			leftView.layout(0, 0, leftView.getMeasuredWidth(), leftView.getMeasuredHeight());
			leftView.setScaleX(0f);
		}
		if (rightView != null) {
			rightView.layout(swipeLayout.getMeasuredWidth() - rightView.getMeasuredWidth(), 0,
					swipeLayout.getMeasuredWidth(), rightView.getMeasuredHeight());
			rightView.setScaleX(0f);
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
			leftView.setPivotX(0f);
			float scaleValue = offset * 1.0f / leftView.getMeasuredWidth();
			leftView.setScaleX(scaleValue);
		}
		if (rightView != null) {
			rightView.setPivotX(rightView.getMeasuredWidth());
			float scaleValue = -offset * 1.0f / rightView.getMeasuredWidth();
			rightView.setScaleX(scaleValue);
		}
		swipeLayout.invalidate();
	}
}
