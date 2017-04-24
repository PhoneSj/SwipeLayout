package com.phone.swipelayout.mode;

import android.view.View;

import com.phone.swipelayout.SwipeLayout;


/**
 * Created by Phone on 2017/4/20.
 */

public class LayDownMode extends SwipeMode {

	public LayDownMode(SwipeLayout swipeLayout) {
		super(swipeLayout);
	}

	@Override
	public void layout() {
		View leftView = swipeLayout.getLeftView();
		View rightView = swipeLayout.getRightView();
		if (leftView != null) {
			leftView.layout(0, 0, leftView.getMeasuredWidth(), leftView.getMeasuredHeight());
		}
		if (rightView != null) {
			rightView.layout(swipeLayout.getMeasuredWidth() - swipeLayout.getMeasuredWidth(), 0,
					swipeLayout.getMeasuredWidth(), rightView.getMeasuredHeight());
		}
	}

	@Override
	public void offsetLeftAndRight(int offset) {
		View mainView = swipeLayout.getMainView();
		if (mainView != null) {
			int originalX = 0;
			mainView.layout(originalX + offset, 0, originalX + offset + mainView.getMeasuredWidth(),
					mainView.getMeasuredHeight());
		}
		swipeLayout.invalidate();
	}

}
