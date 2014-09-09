package com.example.newsapp.imagegetter;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.text.Html;
import android.widget.TextView;

public class MyImageGetter implements Html.ImageGetter{
	Context context;
	TextView tv;
	ConnectivityManager cm;

	public MyImageGetter(Context context, TextView tv){
		this.context = context;
		this.tv= tv;
		cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);	 
		 
		 
	}
	
	@Override
	public Drawable getDrawable(String source) {
		UrlDrawable d = new UrlDrawable();
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if(netInfo!=null){
		ImageGetterAsyncTask task = new ImageGetterAsyncTask(d);
		task.execute(source);}
		return d;
	}
	
	


	private class ImageGetterAsyncTask extends AsyncTask<String, Void, Drawable>{

		UrlDrawable urlDrawbale;
		
		public ImageGetterAsyncTask(UrlDrawable d){
			urlDrawbale = d;
		}
		
		@Override
		protected Drawable doInBackground(String... params) {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpGet request = new HttpGet(params[0]);
			Drawable drawable= null;
			try {
				
				HttpResponse response = httpClient.execute(request);
				drawable = Drawable.createFromStream(response.getEntity().getContent(), "src");
				drawable.setBounds(10, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			return drawable;
		}
		
		@Override
		protected void onPostExecute(Drawable result) {
			super.onPostExecute(result);
			urlDrawbale.setBounds(result.getBounds());
			urlDrawbale.drawable = result;
			tv.invalidate();
			tv.setHeight(tv.getHeight()+ result.getIntrinsicHeight());
			tv.setEllipsize(null);
		}
		
	}


	@SuppressWarnings("deprecation")
	private class  UrlDrawable extends BitmapDrawable{
	private Drawable drawable;
	
	
	@Override
	public void draw(Canvas canvas) {
		if(drawable!= null){
			drawable.draw(canvas);
			}
	 	}
	
	}

	

}
