package com.example.newsapp.http.connetion;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class httpLoader {

	
	private static InputStream content;
	private static JSONObject json;
	
	public httpLoader() {
		
	}

	
	public  static InputStream getInputStream(String url){
		
		 DefaultHttpClient client = new DefaultHttpClient();
	     HttpGet httpGet = new HttpGet(url);
	        
	        try {
	          HttpResponse getResponse = client.execute(httpGet);
	          final int statusCode = getResponse.getStatusLine().getStatusCode();
	          if(statusCode != HttpStatus.SC_OK){
	        	  Log.d("myLogs"," status code isn't OK"  );
	        	  return null;
	          }
	          
	          content = getResponse.getEntity().getContent();
	        
	        } catch (Exception e) {
	          e.printStackTrace();
	        }
		return content;
		
	}
	
	
	
	public static JSONObject getJSON(String url){
		
		String response = "";
		   
    	BufferedReader buffer = new BufferedReader(new InputStreamReader(getInputStream(url)));
    	String s = "";
    	try {
			while ((s= buffer.readLine()) != null){
				response +=s;
					}
			} catch (IOException e1) {
				e1.printStackTrace();
		}
    
        try {
        	json = new JSONObject(response);
        	
        	
        	} catch (JSONException e) {
        		e.printStackTrace();
        }
        	return json;
	
	}	
}





