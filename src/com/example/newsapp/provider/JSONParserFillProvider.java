package com.example.newsapp.provider;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.example.newsapp.http.connetion.httpLoader;
import com.example.newsapp.provider.Contract.News;

public class JSONParserFillProvider {

	public static final String TAG_SHORT = "short";
	public static final String TAG_ICON = "icon";
	public static final String TAG_ICON_URL ="iconurl";
	public static final String TAG_TIME = "time";
	public static final String TAG_TITLE = "title";
	public static final String TAG_DATE = "date";
	public static final String TAG_ARTICLEID = "articleId"; 
	
	private ContentValues[] newsValues;
	private Context context;
	
	SimpleDateFormat format ;
	public JSONParserFillProvider(Context context) {
		this.context = context;
		format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
	}
	
	
	public void FillCP(String url){
	
	    try {
			JSONObject json = httpLoader.getJSON(url);
			JSONArray jsonArray = json.getJSONArray("news");
			newsValues = new ContentValues[jsonArray.length()] ;
			for(int i = 0; i <jsonArray.length(); i++){
				JSONObject c= jsonArray.getJSONObject(i);
				ContentValues cv = new ContentValues();
				cv.put(News.TITLE, c.getString(TAG_TITLE));
				cv.put(News.SHORT, c.getString(TAG_SHORT));
				cv.put(News.ICON_URL, c.getString(TAG_ICON));
				
				String  date = c.getString(TAG_DATE);
				String  time =  c.getString(TAG_TIME);
				
				cv.put(News.TIME, time);
				cv.put(News.DATE, date);
				
				cv.put(News.ARTICLE_ID,c.getString(TAG_ARTICLEID));
				cv.put(News.ORDER_INDEX,getMinutes(date, time));
			//	Log.d("myLogs","ORDER_INDEX : ");
				newsValues[i] = cv;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	    
//	try{    
	context.getContentResolver().bulkInsert(Contract.News.CONTENT_URI, newsValues) ;
//	}catch(Exception e){
//		Toast.makeText(context, "Ошибка загрузки",Toast.LENGTH_SHORT).show();
//	}
	newsValues= null;
	}

	private int getMinutes(String date, String time){
		Date date3;
		try {
			date3  = format.parse(date+" "+ time);
		} catch (ParseException e) {
			e.printStackTrace();
			date3 = new Date();
			Log.d("myLogs","wrong date!!!!!!!!");
		}
		
		return (int)(date3.getTime()/6000);
	}
	
	
}
