package com.example.newsapp.service;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.widget.Toast;

import com.example.newsapp.provider.JSONParserFillProvider;

public class RefreshService extends Service {
	
	private DownloaderTask task;
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		String url = intent.getStringExtra("url");
		task = new DownloaderTask();
		task.execute(url);
		
		return Service.START_FLAG_REDELIVERY;
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	
	
	private class DownloaderTask extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {
			new JSONParserFillProvider(getApplicationContext()).FillCP(params[0]);
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			Toast.makeText(getApplicationContext(), "Обновлено", Toast.LENGTH_SHORT).show();
			stopSelf();
			
		}
		
	}
	
	
	
}
