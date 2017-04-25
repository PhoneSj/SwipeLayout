package com.phone.test;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import custom.phone.com.mycustomview.R;

/**
 * Created by Phone on 2017/4/22.
 */

public class SwipeLayoutTest extends ListActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setListAdapter(new MyAdapter());
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Toast.makeText(this, "第" + position + "项", Toast.LENGTH_SHORT).show();
	}

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return 20;
		}

		@Override
		public Object getItem(int i) {
			return null;
		}

		@Override
		public long getItemId(int i) {
			return i;
		}

		@Override
		public View getView(int i, View view, ViewGroup viewGroup) {
			if (view == null) {
				view = LayoutInflater.from(SwipeLayoutTest.this).inflate(R.layout.item_swipe, null);
			}
			ImageView delete = (ImageView) view.findViewById(R.id.textView_delete);
			delete.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					Log.i("phoneTest", "click imageView");
				}
			});
			return view;
		}
	}
}
