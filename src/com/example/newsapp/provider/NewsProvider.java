package com.example.newsapp.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Trace;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.newsapp.activities.DetailsActivity;
import com.example.newsapp.fragments.PagerFragment;
import com.example.newsapp.provider.Contract.News;
import com.example.newsapp.provider.Contract.NewsFull;
import com.example.newsapp.provider.Contract.Tracing;

public class NewsProvider extends ContentProvider {

	private static final String TABLE_NEWS = "news";   // the same as Contract.News.CONTENT_PATH
	private static final String TABLE_NEWS_CONTENT = "newscontent";
	private static final String TABLE_TRACE = "trace";
	
	private static final String DB_NAME = TABLE_NEWS + ".db";
	private static final int DB_VERSION = 1;
	
	private static final UriMatcher uriMatcher;
	private static final int PATH_NEWS = 1;
	private static final int PATH_NEWS_ID = 2;
	private static final int FULL_PATH_NEWS = 3;
	private static final int FULL_PATH_NEWS_ID = 4;
	private static final int TRACE_PATH = 5;
	private static final int TRACE_PATH_ID = 6;
	
	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(Contract.AUTHORITY, Contract.News.CONTENT_PATH, PATH_NEWS);
		uriMatcher.addURI(Contract.AUTHORITY, Contract.News.CONTENT_PATH + "/#", PATH_NEWS_ID);
		
		uriMatcher.addURI(Contract.AUTHORITY, Contract.NewsFull.FULL_CONTENT_PATH, FULL_PATH_NEWS);
		uriMatcher.addURI(Contract.AUTHORITY, Contract.NewsFull.FULL_CONTENT_PATH + "/#", FULL_PATH_NEWS_ID);
		
		uriMatcher.addURI(Contract.AUTHORITY, Contract.Tracing.TRACE_PATH, TRACE_PATH);
		uriMatcher.addURI(Contract.AUTHORITY, Contract.Tracing.TRACE_PATH+ "/#", TRACE_PATH_ID);
	}
	
	private DBHelper mDBHelper;
	
	class DBHelper extends SQLiteOpenHelper{

		public DBHelper(Context context, String name, CursorFactory factory,
				int version) {
			super(context, name, factory, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			String sql = 
					"create table " + TABLE_NEWS + " ("+
							News._ID + " integer primary key autoincrement, " +
							News.TITLE + " text UNIQUE NOT NULL, " +
							News.SHORT + " text, " +
							News.ICON_URL + " text, " +
							News.TIME + " text, " +
							News.DATE + " text, " +
							News.ARTICLE_ID + " text,  " +
							News.ORDER_INDEX + " integer "+
							")";
			db.execSQL(sql);
				sql = 
					"create table " + TABLE_NEWS_CONTENT+ " ("+
							NewsFull._ID + " integer, " +
							NewsFull.FULL + " text "+
							")";
			db.execSQL(sql);
				sql = 
					"create table " + TABLE_TRACE+ " ("+
							Tracing._ID + " integer primary key autoincrement, " +
							Tracing.TRACE + " integer "+
							")";
			db.execSQL(sql);
			
			for (int i = 0; i < 300; i++) {
				ContentValues cv = new ContentValues();
				cv.put(Tracing.TRACE, 0);
				db.insert(TABLE_TRACE, null, cv);
				
			}
			
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		}
		
	}
	
	
	
	@Override
	public boolean onCreate() {
		mDBHelper = new DBHelper(getContext(), DB_NAME, null, DB_VERSION);
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		
	//	Log.d("myLogs","query Uri uri = "+ uri );
		
		Cursor cursor;
		switch(uriMatcher.match(uri)){
		case PATH_NEWS:
			sortOrder = News.ORDER_INDEX +" DESC";
			cursor = mDBHelper.getWritableDatabase().query(TABLE_NEWS, projection, selection, selectionArgs, null, null, sortOrder);
			cursor.setNotificationUri(getContext().getContentResolver(), Contract.News.CONTENT_URI);
			break;
			
		case PATH_NEWS_ID:
			String id = uri.getLastPathSegment();
			if(TextUtils.isEmpty(selection)){
				selection = News._ID +" = " + id;
			} else {
				selection = selection + " AND "+ News._ID + " = " + id;  
			}
			cursor = mDBHelper.getWritableDatabase().query(TABLE_NEWS, projection, selection, selectionArgs, null, null, sortOrder);
			cursor.setNotificationUri(getContext().getContentResolver(), Contract.News.CONTENT_URI);
			break;
			
		case FULL_PATH_NEWS:
			cursor = mDBHelper.getWritableDatabase().query(TABLE_NEWS_CONTENT, projection, selection, selectionArgs, null, null, sortOrder);
			break;
			
		case FULL_PATH_NEWS_ID:
			String idF = uri.getLastPathSegment();
			selection = NewsFull._ID +" = " + idF;
			cursor = mDBHelper.getWritableDatabase().query( TABLE_NEWS_CONTENT, projection, selection, selectionArgs, null, null, sortOrder);
			break;
		case TRACE_PATH_ID:
			String idT = uri.getLastPathSegment();
			selection = Tracing._ID +" = " + idT;
			cursor = mDBHelper.getWritableDatabase().query( TABLE_TRACE, projection, selection, selectionArgs, null, null, sortOrder);
			break;
			default:
				throw new IllegalArgumentException("Wron Uri: " + uri);
		}
		return cursor;
	}

	@Override
	public String getType(Uri uri) {
		switch(uriMatcher.match(uri)){
		case PATH_NEWS:
			return Contract.News.CONTENT_TYPE;
		case PATH_NEWS_ID:
			return Contract.News.CONTENT_IMEM_TYPE;
		default: //Uri uri
			return null;
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		//Log.d("myLogs","insert");
		switch(uriMatcher.match(uri)){
		case PATH_NEWS:	
			try{
				mDBHelper.getWritableDatabase().insertOrThrow(TABLE_NEWS, null, values);
				getContext().getContentResolver().notifyChange(Contract.News.CONTENT_URI, null);
			} catch(SQLiteConstraintException e) {
				Log.d("myLogs","Igonring constraint failure");
			}
		break;
		case FULL_PATH_NEWS:
		mDBHelper.getWritableDatabase().insert(TABLE_NEWS_CONTENT, null, values);	
		break;
		}
		return null;
		
	}

	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		switch(uriMatcher.match(uri)){
		case PATH_NEWS:
			return mDBHelper.getWritableDatabase().delete(TABLE_NEWS, selection, selectionArgs);
		default:
			return 0;
		}
		
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		switch (uriMatcher.match(uri)){
		case PATH_NEWS:
			return mDBHelper.getWritableDatabase().update(TABLE_NEWS, values, selection, selectionArgs);
		case TRACE_PATH_ID:
			String idT = uri.getLastPathSegment();
			selection = Tracing._ID +" = " + idT;
			return mDBHelper.getWritableDatabase().update(TABLE_TRACE, values, selection, selectionArgs);
			
		default:
			return 0;
		}
		
	}

}
