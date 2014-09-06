package com.example.newsapp.adapters;

import java.text.SimpleDateFormat;

import com.example.newsapp.R;
import com.example.newsapp.fragments.TitlesFragment;
import com.example.newsapp.provider.Contract.News;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class NewsCursorAdapter extends CursorAdapter {

	private LayoutInflater mInflater;
	private String date;
	private ImageLoader imageLoadear = ImageLoader.getInstance();
	private DisplayImageOptions options = new DisplayImageOptions.Builder().cacheOnDisc().build();
	
	public NewsCursorAdapter(Context context, Cursor c, int flags) {
		super(context, c, flags);
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		date = sdf.format(System.currentTimeMillis());
		imageLoadear.init(ImageLoaderConfiguration.createDefault(context)); 
	}

	@Override
	public void bindView(View view, Context context, Cursor c) {
		((ImageView) view.findViewById(R.id.tvImage))
		.setImageBitmap(null);
		
		((TextView) view.findViewById(R.id.tvTitle))
		.setText(c.getString(c.getColumnIndex(News.TITLE)));
		
		((TextView) view.findViewById(R.id.tvShort))
		.setText(c.getString(c.getColumnIndex(News.SHORT)));
		
		String s="";
		if(c.getString(c.getColumnIndex(News.DATE)).equals(date)){
			 s = "Сегодня \n" + c.getString(c.getColumnIndex(News.TIME));
		} else{
			s = "  "+c.getString(c.getColumnIndex(News.TIME))+"\n"+c.getString(c.getColumnIndex(News.DATE));
		}
		((TextView) view.findViewById(R.id.tvDate))
		.setText(s);
		view.setTag(c.getInt(c.getColumnIndex(News._ID))-1);
		imageLoadear.displayImage( c.getString(c.getColumnIndex(News.ICON_URL)), (ImageView) view.findViewById(R.id.tvImage),options);
	}

	@Override
	public View newView(Context arg0, Cursor arg1, ViewGroup parent) {
		return mInflater.inflate(R.layout.myitem, parent, false);
	}

	

	

}
