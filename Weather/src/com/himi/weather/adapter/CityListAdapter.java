package com.himi.weather.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.himi.weather.R;

public class CityListAdapter extends BaseAdapter{
	
	private List<String> list;
	private LayoutInflater mInflater;
	
	public CityListAdapter(Context context, List<String> list) {
		this.list = list;
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		// TODO �Զ����ɵķ������
		return list.size();
	}

	@Override
	public String getItem(int position) {
		// TODO �Զ����ɵķ������
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO �Զ����ɵķ������
		return position;
	}

	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	    
		View rootView = null;
		if(convertView == null) {
			rootView = mInflater.inflate(R.layout.item_city_list, null);
		} else {
			rootView = convertView;
		}
		
		TextView tv_city_name = (TextView) rootView.findViewById(R.id.tv_city_name);
		tv_city_name.setText(getItem(position));
		
		
		return rootView;
	}

}
