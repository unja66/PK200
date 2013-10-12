package com.kukung.app.model;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class TocAdapter extends BaseAdapter {
	Context context;
	int layout;
	List<TOCItem> tocItemList;
	
	public List<TOCItem> getTocItemList() {
		return tocItemList;
	}

	public void setTocItemList(List<TOCItem> tocItemList) {
		this.tocItemList = tocItemList;
	}

	public TocAdapter(Context context, int layout, List<TOCItem> tocItemList) {
		this.context = context;
		this.layout = layout;
		this.tocItemList = tocItemList;
	}
	
	@Override
	public int getCount() {
		return tocItemList.size();
	}

	@Override
	public Object getItem(int position) {
		return tocItemList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertTocItemView, ViewGroup parent) {
		if(convertTocItemView == null) {
			convertTocItemView = LayoutInflater.from(context).inflate(layout, parent, false);
		}
		
		TOCItem tocItem = (TOCItem)getItem(position);
		TextView tocTitle = (TextView)convertTocItemView.findViewById(com.kukung.app.pk200.R.id.tocTitle);
		tocTitle.setText(tocItem.getTitle());
		
		if (tocItem.getLevel() == 1) {
			tocTitle.setBackgroundColor(Color.rgb(163, 148, 128));
		} else {
			tocTitle.setBackgroundColor(Color.rgb(0, 0, 0));
		}
		
		return convertTocItemView;
	}

}
