package com.automessaging.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class CustomMsgListAdapter extends BaseAdapter {
	
	private Context mContext;
	private LayoutInflater inflator;
//	final int[] images = { R.drawable.magnolia,
//			R.drawable.orchid, R.drawable.rose,
//			 };

	public CustomMsgListAdapter(Context context) {
		// TODO Auto-generated constructor stub
		this.mContext=context;
		this.inflator= (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		return null;
	}

	
}
