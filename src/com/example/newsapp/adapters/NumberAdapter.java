package com.example.newsapp.adapters;


import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.newsapp.R;
import com.example.newsapp.fragments.NumberFragment;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class NumberAdapter extends BaseAdapter {
	
private	Context context;
private	LayoutInflater lInflater;
private	ArrayList<HashMap<String,String>> myArrayList;
private	String Title;
private	ViewHolder viewHolder ;
private	View viewTitle;
private	View view;
private	ImageLoader imageLoadear = ImageLoader.getInstance();
private	DisplayImageOptions options = new DisplayImageOptions.Builder().cacheOnDisc().build();
	

	public NumberAdapter(Context context, ArrayList<HashMap<String,String>> myArrayList) {
		
		this.context = context;
		this.myArrayList = myArrayList;
		lInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		imageLoadear.init(ImageLoaderConfiguration.createDefault(context)); 
		
	}

	@Override
	public int getCount() {
		return myArrayList.size();
	}

	@Override
	public Object getItem(int position) {
		return myArrayList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	static class ViewHolder{
		TextView tvTitle;
		TextView tvShort;
		TextView tvDate;
		ImageView tvImage;
	}
	
	
	
	
	private void cleanView(View view){
		viewHolder.tvShort.setText("");
		viewHolder.tvDate.setText("");
		viewHolder.tvImage.setImageBitmap(null);
		view.setClickable(true);
	}
	
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		view = convertView;
		
		if(view == null||view == viewTitle ||((ViewHolder) view.getTag())==null){
			view = lInflater.inflate(R.layout.myitem, parent, false);
			
			viewHolder = new ViewHolder();
			viewHolder.tvTitle = (TextView) view.findViewById(R.id.tvTitle);
			viewHolder.tvShort = (TextView) view.findViewById(R.id.tvShort);
			viewHolder.tvDate  = (TextView) view.findViewById(R.id.tvDate);
			viewHolder.tvImage = (ImageView) view.findViewById(R.id.tvImage);
			view.setTag(viewHolder);
		} else{
			viewHolder = (ViewHolder) view.getTag();
		}
		if(viewHolder!=null)
		(viewHolder.tvImage).setImageBitmap(null);
		else Log.d("myLogs","viewHolder =null");
		view.setBackgroundColor(Color.WHITE);
		view.setClickable(false);
		
		Title = (String) myArrayList.get(position).get(NumberFragment.TAG_TITLE);
		
		
		if(checkTitle(parent,NumberFragment.TAG_QUESTIONS, R.color.question, R.string.questions)){}
		
		else if(checkTitle(parent, NumberFragment.TAG_SOCIAL, R.color.social, R.string.social)){}	
		
		else if(checkTitle(parent, NumberFragment.TAG_VYBOR, R.color.vybor, R.string.vybor)){}
			
		else if(checkTitle(parent, NumberFragment.TAG_PRESS_CENTR, R.color.press, R.string.press_centr)){} 
		
		else if(checkTitle(parent, NumberFragment.TAG_HEALTH, R.color.health, R.string.health)){}
		
		else if(checkTitle(parent, NumberFragment.TAG_TIME_FREE,R.color.timefree, R.string.timefree)){}
			
		else{
			
			viewHolder.tvTitle
			.setText((String) myArrayList.get(position).get(NumberFragment.TAG_TITLE));
			
			viewHolder.tvShort
			.setText((String) myArrayList.get(position).get(NumberFragment.TAG_DESC));
			
			viewHolder.tvDate
			.setText((String) myArrayList.get(position).get(NumberFragment.TAG_DATE));
			
			imageLoadear.displayImage((String) myArrayList.get(position).get(NumberFragment.TAG_ICON), viewHolder.tvImage,options);
		}
		
		
		
		return view;
	}

	private boolean checkTitle(ViewGroup parent ,String tag, int colorId, int textId){
		if(Title.equalsIgnoreCase(tag)){
			viewTitle = lInflater.inflate(R.layout.list_item_section, parent,false);
			((TextView) viewTitle.findViewById(R.id.list_item_section_text)).setText(context.getResources().getString(textId));
			viewTitle.setBackgroundColor(context.getResources().getColor(colorId));
			//cleanView(view);
			viewTitle.setClickable(true);
			view = viewTitle;
			return true;
		}
		
		return false;
	}
	
	
	
}

