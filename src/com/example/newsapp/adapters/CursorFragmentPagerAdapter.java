package com.example.newsapp.adapters;

import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.ViewGroup;

public abstract class CursorFragmentPagerAdapter extends FragmentPagerAdapter {

    private boolean mDataValid;
    private Cursor mCursor;
    private int mRowIDColumn;

    public CursorFragmentPagerAdapter(FragmentManager fm, Cursor cursor) {
        super(fm);
        init(cursor);
    }

    void init(Cursor c) {
    	boolean cursorPresent = c != null;
        mCursor = c;
        mDataValid = cursorPresent;
       mRowIDColumn = cursorPresent ? c.getColumnIndexOrThrow("_id") : -1;
    }



    @Override
    public int getItemPosition(Object object) {
   	Log.d("myLogs","getItemPosition");
  
        return POSITION_NONE;
    }

    @Override
    public Fragment getItem(int position) {
        if (mDataValid) {
            mCursor.moveToPosition(position);
            return getItem(mCursor);
        } else {
            return null;
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
     super.destroyItem(container, position, object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
    	if (!mDataValid) {
            throw new IllegalStateException("this should only be called when the cursor is valid");
        }
        if (!mCursor.moveToPosition(position)) {
            throw new IllegalStateException("couldn't move cursor to position " + position);
        }
        Object obj = super.instantiateItem(container, position);
        return obj;
    }
    
   
    public abstract Fragment getItem(Cursor cursor);

    @Override
    public int getCount() {
    	if (mDataValid) {
            return mCursor.getCount();
        } else {
            return 0;
        }
    }

    @Override
    public long getItemId(int position) {
    	if (!mDataValid || !mCursor.moveToPosition(position)) {
        	Log.d("myLogs","getItemId");
            return super.getItemId(position);
        }
       int rowId = mCursor.getInt(mRowIDColumn);
       return rowId;
    }

}
