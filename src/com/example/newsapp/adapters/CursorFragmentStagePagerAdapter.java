package com.example.newsapp.adapters;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.newsapp.fragments.PagerFragment;
import com.example.newsapp.fragments.TitlesFragment;

public class CursorFragmentStagePagerAdapter  extends FragmentStatePagerAdapter {
	private boolean mDataValid;
	private Cursor mCursor;
	
	public CursorFragmentStagePagerAdapter(FragmentManager fm, Cursor cursor ) {
		super(fm);
		init(cursor);
	}

	void init(Cursor c){
		mCursor = c;
		mDataValid = c != null;
	}
	
	
	@Override
	public Fragment getItem(int position) {
		Bundle bundle = new Bundle();
		bundle.putInt(TitlesFragment.POSITION,position);
		return PagerFragment.newInstance(bundle);
	}

	@Override
	public int getCount() {
		if(mDataValid)
		return	mCursor.getCount();
		else
		return 0;
	}

}
