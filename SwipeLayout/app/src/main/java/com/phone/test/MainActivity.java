package com.phone.test;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.Arrays;

public class MainActivity extends ListActivity {

	private String name[] = { "单个SwipeLayout","SwipeLayout嵌入普通ListView",
			"SwipeLayout嵌入SwipeListView" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Arrays.asList(name)));
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		switch (position) {
			case 0:
				startActivity(new Intent(this, SwipeLayoutTest.class));
				break;
			case 1:
				startActivity(new Intent(this,EnableListViewTest.class));
				break;
			case 2:
				startActivity(new Intent(this,EnableSwipeListViewTest.class));
				break;
		}
	}
}
