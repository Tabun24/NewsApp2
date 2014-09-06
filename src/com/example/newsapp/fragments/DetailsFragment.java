package com.example.newsapp.fragments;
import org.json.JSONException;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Spanned;
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
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;


public class DetailsFragment extends Fragment {

private	TextView    tvTitle,tvShort,tvFull;
private	ImageView   ivIcon;
private	ProgressBar pBar;
private	ImageLoader imageLoadear = ImageLoader.getInstance();
private Activity activity;
private DownloadNewsTask task ;
private ConnectivityManager cm;	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
		
	}
	
	
	
	public static DetailsFragment newInstance(Bundle myBundle) {
	    DetailsFragment details = new DetailsFragment();
	    details.setArguments(myBundle);
	    return details;
	  }
	
	
	public Bundle getBundle(){
		return getArguments();
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

	    View v = inflater.inflate(R.layout.details, container, false);
	  
	    imageLoadear.init(ImageLoaderConfiguration.createDefault(getActivity())); 
	    tvTitle = (TextView) v.findViewById(R.id.tvTitle);
		tvShort = (TextView) v.findViewById(R.id.tvShort);
		tvFull = (TextView) v.findViewById(R.id.tvFull);
		ivIcon = (ImageView) v.findViewById(R.id.ivIcon);
		pBar = (ProgressBar) v.findViewById(R.id.progressBar3);
		tvTitle.setText(getBundle().getString(TitlesFragment.TAG_TITLE));
		tvShort.setText(getBundle().getString(TitlesFragment.TAG_SHORT));
		imageLoadear.displayImage( getBundle().getString(TitlesFragment.TAG_ICON_URL), ivIcon) ;
		
		NetworkInfo nInfo = cm.getActiveNetworkInfo();
		if(nInfo!=null && nInfo.isConnected()){
		task = new DownloadNewsTask();
		String params = "http://api2.aifby.atservers.net/details?articleId="+getBundle().getString(TitlesFragment.TAG_ARTICLEID);
		task.execute(params);
		}
	    return v;
	}
	
	
  private class DownloadNewsTask extends AsyncTask<String, Void, String> {
		  		  
		  String 		 full;
		  @Override
  
	    protected String doInBackground(String... urls) {
	  
	  		try {
				full = httpLoader.getJSON(urls[0]).getString("full");
			} catch (JSONException e) {
				e.printStackTrace();
				}
	  		return full;
	    }

		  
		  
	    @Override
	    protected void onPostExecute(String result) {
	    	
	    // Spanned sp = Html.fromHtml(result,new ImageGetter(),null);

	    	Spanned sp = Html.fromHtml(result);
	      tvFull.setText(sp);
	      pBar.setVisibility(View.GONE);
	      
			if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
			((MainActivity) activity).itemShowMain.setVisible(true);	
		} else 
		    ((DetailsActivity) activity).itemShow.setVisible(true);
	
	    }
	  }
	
  
  
  
  
  @Override
	public void onDestroy() {
		super.onDestroy();
		if(task!=null) task.cancel(true);
		task = null;
	}
	
 private class ImageGetter implements Html.ImageGetter{
	 BitmapDrawable  db;

	 
	@Override
	public Drawable getDrawable(String source) {
		db = new BitmapDrawable(getResources(), httpLoader.getInputStream(source));
		return db ; 
	}
	 
 }
  
	
}
