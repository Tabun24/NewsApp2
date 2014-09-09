package com.example.newsapp.fragments;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.newsapp.R;
import com.example.newsapp.activities.DetailsActivity;
import com.example.newsapp.activities.MainActivity;
import com.example.newsapp.http.connetion.httpLoader;
import com.example.newsapp.imagegetter.MyImageGetter;
import com.example.newsapp.provider.Contract.News;
import com.example.newsapp.provider.Contract.NewsFull;
import com.example.newsapp.provider.Contract.Tracing;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class PagerFragment extends Fragment {
	
		private	TextView    tvTitle,tvShort,tvFull;
		private	ImageView   ivIcon;
		private	ProgressBar pBar;
		private	ImageLoader imageLoadear = ImageLoader.getInstance();
		private Activity activity;
		private DownloadNewsTask task ;
		private ConnectivityManager cm;	
	    private ContentResolver cr;
			@Override
			public void onAttach(Activity activity) {
				super.onAttach(activity);
				this.activity = activity;
			}

			@Override
			public void onCreate(Bundle savedInstanceState) {
				super.onCreate(savedInstanceState);
				cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
				cr = getActivity().getContentResolver();
				Log.d("myLogs","onCreate "+ getArguments().getInt(TitlesFragment.POSITION));
			}
			
			public static PagerFragment newInstance(Bundle myBundle) {
				PagerFragment details = new PagerFragment();
			    details.setArguments(myBundle);
			    return details;
			  }
			
			
			public Bundle getBundle(){
				return getArguments();
			}
			
			
			@Override
			public View onCreateView(LayoutInflater inflater,
					@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
				Log.d("myLogs" , "onCreateView");
			    View v = inflater.inflate(R.layout.details, container, false);
			  
			    imageLoadear.init(ImageLoaderConfiguration.createDefault(getActivity())); 
			    Uri uriposition = Uri.withAppendedPath(News.CONTENT_URI, String.valueOf(getBundle().getInt(TitlesFragment.POSITION)+1));
			    Cursor c = cr.query(uriposition, null, null, null, null);
				c.moveToFirst();
			    tvTitle = (TextView) v.findViewById(R.id.tvTitle);
				tvShort = (TextView) v.findViewById(R.id.tvShort);
				tvFull = (TextView) v.findViewById(R.id.tvFull);
				ivIcon = (ImageView) v.findViewById(R.id.ivIcon);
				pBar = (ProgressBar) v.findViewById(R.id.progressBar3);
				
				tvTitle.setText(c.getString(c.getColumnIndex(News.TITLE)));
				tvShort.setText(c.getString(c.getColumnIndex(News.SHORT)));
				imageLoadear.displayImage( c.getString(c.getColumnIndex(News.ICON_URL)), ivIcon) ;
				
				
				
				NetworkInfo nInfo = cm.getActiveNetworkInfo();
				
				task = new DownloadNewsTask();
				String params = "http://api2.aifby.atservers.net/details?articleId="+c.getString(c.getColumnIndex(News.ARTICLE_ID));
				
				uriposition = Uri.withAppendedPath(Tracing.TRACE_URI, String.valueOf(getBundle().getInt(TitlesFragment.POSITION)+1));
				c =cr.query(uriposition, null, null, null, null);
				c.moveToFirst();
				
				if(c.getInt(c.getColumnIndex(Tracing.TRACE))==0){
					
					if(nInfo!=null && nInfo.isConnected()){
				c.close();
				ContentValues cv = new ContentValues();
				cv.put(Tracing.TRACE, 1);	
				cr.update(uriposition, cv, null, null);	
				task.execute(params);
					}
				} else {
					onPostHelp();
				}
				return v;
			}
			
			
		  private class DownloadNewsTask extends AsyncTask<String, Void, Void> {
			  protected void onPreExecute() {
					super.onPreExecute();
				  pBar.setVisibility(View.VISIBLE);
			  };
			  	@Override
		        protected Void doInBackground(String... urls) {
			  
			  		try {
					
						ContentValues cv = new ContentValues();
						cv.put(NewsFull._ID,( getBundle().getInt(TitlesFragment.POSITION)+1));
						cv.put(NewsFull.FULL, httpLoader.getJSON(urls[0]).getString("full"));
						Log.d("myLogs",  " doInBackground(String... urls)");
						cr.insert(NewsFull.FULL_CONTENT_URI, cv);
						
					} catch (Exception e) {
						e.printStackTrace();
						}
			  		return null;
			    }
			  	@Override
			    protected void onPostExecute(Void result) {
					super.onPostExecute(result);
			    	try{
			    	Log.d("myLogs","positon onPostExecute = " + getBundle().getInt(TitlesFragment.POSITION));
			    	onPostHelp();
			    	if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
						((MainActivity) activity).itemShowMain.setVisible(true);	
					} else 
					    ((DetailsActivity) activity).itemShow.setVisible(true);
			    	} catch (IllegalStateException e){
			    		e.printStackTrace();
			    		Log.d("myLogs","IllegalStateException : "+e.getMessage());
			    	}
			    }
			    	
			  }
		 
		  @Override
			public void onDestroy() {
				super.onDestroy();
				Log.d("myLogs","onDestroy "+ getArguments().getInt(TitlesFragment.POSITION));
				if(task!=null) task.cancel(true);
				task = null;
			}
			
		
		
	private	void  onPostHelp(){
		Uri uriposition = Uri.withAppendedPath(NewsFull.FULL_CONTENT_URI, String.valueOf(getBundle().getInt(TitlesFragment.POSITION)+1));
		Cursor cursor =cr.query(uriposition, null, null, null, null);
		if(cursor != null){
		if(cursor.moveToFirst()){
		
	    Log.d("myLogs","-------------------------------------- ");
	    Spanned sp = Html.fromHtml(cursor.getString(cursor.getColumnIndex(NewsFull.FULL)),new MyImageGetter(getActivity(), tvFull),null);
	  // 	Spanned sp = Html.fromHtml(cursor.getString(cursor.getColumnIndex(NewsFull.FULL)));
	    tvFull.setText(sp);
	    tvFull.setMovementMethod(LinkMovementMethod.getInstance());
	    pBar.setVisibility(View.GONE);
	    	 	}     
	     	}	
		}
	
}
