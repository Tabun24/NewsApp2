package com.example.newsapp.fragments;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.newsapp.R;
import com.example.newsapp.activities.DetailsActivity;
import com.example.newsapp.activities.MainActivity;
import com.example.newsapp.adapters.NumberAdapter;
import com.example.newsapp.fragments.TitlesFragment.onItemClickListener;
import com.example.newsapp.http.connetion.httpLoader;

public class NumberFragment extends  Fragment {

    private	onItemClickListener listener;
	private final String api_1_host ="http://aifby.atservers.net/api/";
	private final String urlListOfNumbers = api_1_host+ "newspapers.php?sort=desc&page=1";
		
	private ArrayList<HashMap<String,String>>  questionsList ;   // заполняем в фоновом патоке
	private ArrayList<HashMap<String,String>>  socialList;
	private ArrayList<HashMap<String,String>>  pressCentrList;
	private ArrayList<HashMap<String,String>>  vyborList;
	private ArrayList<HashMap<String,String>> sumOfList;
	private ArrayList<HashMap<String,String>> timefreeList;
	private ArrayList<HashMap<String,String>> healthList;
	
	private ListView listView;
	private ProgressBar progressBar;
		
	// таги для новостей	
	public static final String TAG_TITLE = "title";
	public static final String TAG_ICON = "icon";
	public static final String TAG_DATE = "date";
	public static final String TAG_DESC ="desc";
	public static final String TAG_ARTICLEID  = "articleId";
	public static final String TAG_ICON_URL ="iconurl";
	
	// таги для статей
	public static final String TAG_QUESTIONS = "questions";
	public static final String TAG_SOCIAL ="social";
	public static final String TAG_PRESS_CENTR="press-centr";
	public static final String TAG_TIME_FREE = "timefree";
	public static final String TAG_HEALTH = "health";
	public static final String TAG_VYBOR ="vybor";
	
	private final String  TAG_ARCTICLES_COUNTS = "arcticlesCount";
	
	private MainActivity activity;
	private ActionBar actionBar;
	
	//@Override
	public void onAttach(Activity activity) {
			super.onAttach(activity);
			listener = (onItemClickListener) activity;
			this.activity = (MainActivity) activity;
			actionBar = ( (MainActivity) activity).actionBar;
	} 
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		 if(activity._rootView1 == null)  { 	
			 activity._rootView1 = inflater.inflate(R.layout.titles, container, false);
		
			 progressBar = (ProgressBar)activity._rootView1.findViewById(R.id.progressBar2);
			 listView = (ListView) activity._rootView1.findViewById(R.id.listView1); 
		 
			 questionsList = new ArrayList<HashMap<String,String>>();
			 socialList  = new ArrayList<HashMap<String,String>>();
			 pressCentrList = new ArrayList<HashMap<String,String>>();
     		 vyborList = new ArrayList<HashMap<String,String>>();
     	     sumOfList = new ArrayList<HashMap<String,String>>();
     	     timefreeList =new ArrayList<HashMap<String,String>>();
     	     healthList =new ArrayList<HashMap<String,String>>();
    			
		
		 	 listView.setOnItemClickListener(new OnItemClickListener() {
	
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				Bundle bundle = new Bundle();
				bundle.putString(TAG_TITLE, ((String) sumOfList.get(position).get(TAG_TITLE)) );
				bundle.putString(TitlesFragment.TAG_SHORT, ((String) sumOfList.get(position).get(TAG_DESC)) );
				bundle.putString(TitlesFragment.TAG_ICON_URL, ((String) sumOfList.get(position).get(TAG_ICON)) );
				bundle.putString(TAG_ARTICLEID, ((String) sumOfList.get(position).get(TAG_ARTICLEID)) );
				bundle.putString("actionBarTitle", getResources().getStringArray(R.array.mTitles)[1]);
				bundle.putInt(DetailsActivity.TAG_FRAGMENT, 2);
				listener.imetClick(bundle);
		
			}
		});   
		
		 	ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);	 
			 NetworkInfo netInfo = cm.getActiveNetworkInfo();
			 if(netInfo!=null){
			 DownloadNewsTask task = new DownloadNewsTask();
			 task.execute(urlListOfNumbers);
			 } else Toast.makeText(getActivity(),"No Internet access", Toast.LENGTH_SHORT).show();
		 
		 }else ((ViewGroup) activity._rootView1.getParent()).removeView(activity._rootView1);
		 
		 
		 
		 
		return activity._rootView1;
	}
	
		
		  private class DownloadNewsTask extends AsyncTask<String, Void, String> {
		  
			 @Override
			protected void onPreExecute() {
				super.onPreExecute();
			progressBar.setVisibility(View.VISIBLE);
			}

			 
			 @Override
			 protected String doInBackground(String... urls) {
				  
			        String response = "";
			        JSONObject json = httpLoader.getJSON(urls[0]);
					String npId="580";
					try {
						JSONObject  jsons = json.getJSONObject("newspapers");
						npId = jsons.getJSONObject(String.valueOf(1)).getString("npId");
					} catch (JSONException e) {
						Log.d("myLogs" , " Error " + e.getMessage());
						e.printStackTrace();
					} 
					boolean next= true; int i=1;
					while(next){
						json = httpLoader.getJSON(api_1_host+"articles.php?npId="+npId+"&sort=desc&page="+i);
						try {
							response = json.toString(1);
							} catch (JSONException e) {	e.printStackTrace();}
			
							questionsList = fillList(json, questionsList, TAG_QUESTIONS);
							socialList = fillList(json, socialList, TAG_SOCIAL);
							pressCentrList = fillList(json, pressCentrList, TAG_PRESS_CENTR);
							vyborList = fillList(json, vyborList, TAG_VYBOR);
					        timefreeList = fillList(json, timefreeList, TAG_TIME_FREE);
					        healthList = fillList(json, healthList, TAG_HEALTH);
					     try {
							if(json.getString(TAG_ARCTICLES_COUNTS).equalsIgnoreCase("0"))
							next= false;
							} catch (JSONException e) {	e.printStackTrace(); Log.d("myLogs", "Error getJSONObject "+ TAG_ARCTICLES_COUNTS);}
						i++;
				}
			return null;
		    }

			    @Override
			protected void onPostExecute(String result) {
				setAdapterOnListView();
			    progressBar.setVisibility(View.GONE); 
			    }
			  }
		  
			 
		  private ArrayList<HashMap<String,String>> fillList(JSONObject JSONObj, ArrayList<HashMap<String, String>> list, String tag){
			  
				 ArrayList<HashMap<String, String>> arrayList = new ArrayList<HashMap<String,String>>();
				 JSONArray array =null;
				try {
					array = JSONObj.getJSONArray(tag);
				} catch (JSONException e1) {e1.printStackTrace();  Log.d("myLogs", "Error getJSONArray "+ tag); }
				 if(array!=null)
				 for(int j = 0; j < array.length(); j++){
					try {
						JSONObject	c = array.getJSONObject(j);
						String title = c.getString(TAG_TITLE);
						String desc  = c.getString(TAG_DESC);
						String date  = c.getString(TAG_DATE);
						String icon  = c.getString(TAG_ICON);
						String articleid = c.getString(TAG_ARTICLEID);
				
						HashMap<String, String>	map = new HashMap<String,String>();
					  	map.put(TAG_TITLE, title);
					  	map.put(TAG_DESC, desc);
					  	map.put(TAG_DATE, date);
					  	map.put(TAG_ICON, icon);
					  	map.put(TAG_ARTICLEID,articleid);
					  	arrayList.add(map);
					} catch (JSONException e) {	e.printStackTrace();  Log.d("myLogs", "array"+ array.toString());}
				} 
				list.addAll(arrayList);
				return list;
			 }	
		  
	
		  
		private void setAdapterOnListView(){
			fillSumOfList(questionsList, TAG_QUESTIONS);
			fillSumOfList(socialList, TAG_SOCIAL);
			fillSumOfList(pressCentrList, TAG_PRESS_CENTR);
			fillSumOfList(vyborList, TAG_VYBOR);
			fillSumOfList(healthList, TAG_HEALTH);
			fillSumOfList(timefreeList, TAG_TIME_FREE);
			BaseAdapter adapter = new  NumberAdapter(getActivity(), sumOfList);
	   		adapter.notifyDataSetChanged();
	       	listView.setAdapter(adapter);
	       	
		}
		  
	private void fillSumOfList(ArrayList<HashMap<String, String>> locolList,String tag ){
		if(locolList.size()!=0){
		HashMap<String,String> map = new HashMap<String,String>();
		map.put(TAG_TITLE, tag);
		sumOfList.add(map);
		sumOfList.addAll(locolList);
		locolList = null;
		Runtime.getRuntime().gc();  
		}
	}

}
