package com.example.newsapp.fragments;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.newsapp.R;
import com.example.newsapp.activities.DetailsActivity;
import com.example.newsapp.activities.MainActivity;
import com.example.newsapp.adapters.NewsCursorAdapter;
import com.example.newsapp.adapters.TitlesAdapter;
import com.example.newsapp.provider.Contract.News;
import com.example.newsapp.provider.Contract.NewsCoulms;
import com.example.newsapp.provider.JSONParserFillProvider;



public class TitlesFragment extends Fragment {

	
	public interface onItemClickListener {
		public void imetClick(Bundle myBundle);
	}
	
	
	private onItemClickListener listener;
	public static  final String url = "http://api2.aifby.atservers.net/api/newsQuestionAuthor?rubrickId=all&page=";
	
	
	
	private ListView listView;
	private ProgressBar progressBar;
	private int firstVisibleItem ;       // позиция текущего item
	private boolean onPauseSate = false; 
										 
	
	private static boolean taskIsRunning= false;
	private int onSavePositionListView;
	
	public static final String TAG_SHORT = "short";
	public static final String TAG_ICON = "icon";
	public static final String TAG_ICON_URL ="iconurl";
	public static final String TAG_TIME = "time";
	public static final String TAG_TITLE = "title";
	public static final String TAG_DATE = "date";
	public static final String TAG_ARTICLEID = "articleId"; 
	
	public static final String POSITION = "position";
	
	private int countLoader      = 15;  // для загрузки  ListView  по 15 пунктов
	

	private ActionBar actionBar;
	View _rootview;
	
	private ConnectivityManager cm;
	private NewsCursorAdapter adapter;
//	private SimpleCursorAdapter adapter;
	private static final int LOADER_ID =1;
	private static final String[] PROJECTION = {
		News._ID,
		News.TITLE,
		News.SHORT,
		News.ICON_URL,
		News.TIME,
		News.DATE,
		News.ARTICLE_ID,
		News.ORDER_INDEX
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
		taskIsRunning= ( getActivity().getContentResolver().query(News.CONTENT_URI, null, null, null, null).getCount()>=15);
	}
	
	@Override
	public void onAttach(Activity activity) {
			super.onAttach(activity);
			listener = (onItemClickListener) activity;
			actionBar = ( (MainActivity) activity).actionBar;
	} 
	
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		if(_rootview == null){    	
			_rootview = inflater.inflate(R.layout.titles, container, false);
			progressBar = (ProgressBar) _rootview.findViewById(R.id.progressBar2);
			listView = (ListView) _rootview.findViewById(R.id.listView1);
			adapter = new NewsCursorAdapter(getActivity(), getActivity().getContentResolver().query(News.CONTENT_URI, null, null, null, null), CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
			listView.setAdapter(adapter);
		} else ((ViewGroup)_rootview.getParent()).removeView(_rootview); 
		
		 	listView.setOnItemClickListener(new OnItemClickListener() {
	
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
			
				Bundle bundle = new Bundle();
			
				bundle.putInt(DetailsActivity.TAG_FRAGMENT, 1);
				bundle.putString("actionBarTitle", getResources().getStringArray(R.array.mTitles)[0]);
				bundle.putInt(POSITION,(int)view.getTag());
				listener.imetClick(bundle);
			}
		});
		 
		 	listView.setOnScrollListener(new OnScrollListener() {
				
				@Override
				public void onScrollStateChanged(AbsListView view, int scrollState) {
}
				
				@Override
				public void onScroll(AbsListView view, int firstVisibleItem,
						int visibleItemCount, int totalItemCount) {
		
					/// Мониторим текущию позицию item
					DownloadNewsTask task = new DownloadNewsTask();
					    NetworkInfo netInfo = cm.getActiveNetworkInfo();
					    if(netInfo != null && netInfo.isConnected()){	
					Cursor cursor =getActivity().getContentResolver().query(News.CONTENT_URI, null, null, null, null);
					countLoader = cursor.getCount()<15 ? 15 :cursor.getCount();
					
					TitlesFragment.this.firstVisibleItem = firstVisibleItem;
					if(firstVisibleItem == countLoader-visibleItemCount && taskIsRunning){
					taskIsRunning= false;
					task.execute(url+(int)(cursor.getCount()/15)+1);
					//Log.d("myLogs","url"+((int)(cursor.getCount()/15)+1));
					}
			    }
					    
				}
			});
		 	
		 	getActivity().getSupportLoaderManager().initLoader(LOADER_ID, null, loaderCallbacks);
		 	
		
			    NetworkInfo netInfo = cm.getActiveNetworkInfo();
			    if(netInfo != null && netInfo.isConnected()&& getActivity().getContentResolver().query(News.CONTENT_URI, null, null, null, null).getCount()==0){
			    	DownloadNewsTask task = new DownloadNewsTask();
			    	task.execute(url+"1");
			    } else {
			    	if(netInfo==null)Toast.makeText(getActivity(),"No Internet access", Toast.LENGTH_SHORT).show();
			    	//startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
			    }
		 	
		 	
		return _rootview;
	}
	
	public static void setTaskIsRunning(){
		taskIsRunning = true;
	}
	
	
	
	
// AsyncTasdk for loding text by portion (15)
		  private class DownloadNewsTask extends AsyncTask<String, Void, String> {
		  
			 @Override
			protected void onPreExecute() {
				super.onPreExecute();
				progressBar.setVisibility(View.VISIBLE);
			}

			@Override
		    protected String doInBackground(String... urls) {
				
			new JSONParserFillProvider(getActivity()).FillCP(urls[0]);
	   
		return null;
			}
	
		    @Override
		protected void onPostExecute(String result) {
		   if(!onPauseSate){
		    	
		   	listView.setSelection(firstVisibleItem+1);
		   	}
		    progressBar.setVisibility(View.GONE); 
		    taskIsRunning= true;
		  }
	    
		 }
		  

		 
		public void onPause(){
			super.onPause();
			onPauseSate= true;
			onSavePositionListView = firstVisibleItem+1;
		}
		
		public void onResume(){
			super.onResume();
			
			onPauseSate= false;
			listView.setSelection(onSavePositionListView);
		}

		
		private LoaderCallbacks<Cursor> loaderCallbacks = new LoaderCallbacks<Cursor>() {

			@Override
			public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
			//	Log.d("myLogs", "onCreateLoader");
				return new CursorLoader(getActivity(), News.CONTENT_URI, null, null, null, null);
				
			}

			@Override
			public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) {
				adapter.swapCursor(cursor);
			//	Log.d("myLogs", "onLoadFinished");
			}

			@Override
			public void onLoaderReset(Loader<Cursor> arg0) {
				adapter.swapCursor( null);
				//Log.d("myLogs", "onLoaderReset");
			}
		};
		
		
		

}
