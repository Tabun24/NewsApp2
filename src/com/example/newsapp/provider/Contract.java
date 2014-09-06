package com.example.newsapp.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public final class Contract {
	public static final String AUTHORITY ="com.example.newsapp";
	
	public static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);
	
	
	public interface NewsCoulms {
		public static final String TITLE = "title";
		public static final String SHORT = "short";
		public static final String ICON_URL = "iconurl";
		public static final String TIME = "time";
		public static final String DATE = "date";
		public static final String ARTICLE_ID = "articleId";
		public static final String ORDER_INDEX = "orderindex";
		
	}
	
	
	public static final class News implements BaseColumns, NewsCoulms{
		public static final String CONTENT_PATH ="news";
		public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, CONTENT_PATH);
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + AUTHORITY+ "." + CONTENT_PATH;
		public static final String CONTENT_IMEM_TYPE = "vnd.android.cursor.item/vnd."  + AUTHORITY+ "." + CONTENT_PATH;
		
	}
	
	public interface NewsFullCoulms{
		public static final String FULL = "full";
	}
	
	public static final class NewsFull implements BaseColumns, NewsFullCoulms{
		public static final String FULL_CONTENT_PATH ="newscontent";
		public static final Uri FULL_CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, FULL_CONTENT_PATH);
		
	}
	
	
	public interface TraceCoulms{
		public static final String TRACE = "traced";
	}
	
	public static final class Tracing implements BaseColumns,TraceCoulms{
		public static final String TRACE_PATH ="trace";
		public static final Uri TRACE_URI = Uri.withAppendedPath(AUTHORITY_URI, TRACE_PATH);
		
	}
	
	
	
}
