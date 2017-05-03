package com.phone.test;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.phone.swipelayout.SwipeListView;

import custom.phone.com.mycustomview.R;

/**
 * Created by Phone on 2017/5/2.
 */

public class EnableSwipeListViewTest extends Activity {

    private SwipeListView swipeListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enable_swipelistview);
        swipeListView= (SwipeListView) findViewById(R.id.swipeListView);
        swipeListView.setAdapter(new MyAdapter());
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
                view = LayoutInflater.from(EnableSwipeListViewTest.this).inflate(R.layout.item_swipe, null);
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
