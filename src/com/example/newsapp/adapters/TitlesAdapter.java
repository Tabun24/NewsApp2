package com.example.newsapp.adapters;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.newsapp.R;
import com.example.newsapp.fragments.TitlesFragment;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class TitlesAdapter extends BaseAdapter {

	private Context context;
	private LayoutInflater lInflater;
	private ArrayList<HashMap<String,Object>> myArrayList;
	private String date;
	private ImageLoader imageLoadear = ImageLoader.getInstance();
	private DisplayImageOptions options = new DisplayImageOptions.Builder().cacheOnDisc().build();
	
	
	public TitlesAdapter(Context context, ArrayList<HashMap<String,Object>> myArrayList) {
		
		this.context = context;
		this.myArrayList = myArrayList;
		lInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		date = sdf.format(System.currentTimeMillis());
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View view = convertView;
		if(view == null){
			view = lInflater.inflate(R.layout.myitem, parent, false);
		}
		((ImageView) view.findViewById(R.id.tvImage))
		.setImageBitmap(null);
		
		((TextView) view.findViewById(R.id.tvTitle))
		.setText((String) myArrayList.get(position).get(TitlesFragment.TAG_TITLE));
		
		((TextView) view.findViewById(R.id.tvShort))
		.setText((String) myArrayList.get(position).get(TitlesFragment.TAG_SHORT));
		
		String s="";
		if(((String) myArrayList.get(position).get(TitlesFragment.TAG_DATE)).equals(date)){
			 s = "Сегодня \n" + (String) myArrayList.get(position).get(TitlesFragment.TAG_TIME);
		} else{
			s = "  "+(String) myArrayList.get(position).get(TitlesFragment.TAG_TIME)+"\n"+(String) myArrayList.get(position).get(TitlesFragment.TAG_DATE);
		}
		((TextView) view.findViewById(R.id.tvDate))
		.setText(s);
		
		imageLoadear.displayImage((String) myArrayList.get(position).get(TitlesFragment.TAG_ICON_URL), (ImageView) view.findViewById(R.id.tvImage),options);
		
		return view;
	}

}
