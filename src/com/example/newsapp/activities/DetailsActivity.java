package com.example.newsapp.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.newsapp.R;
import com.example.newsapp.fragments.DetailsFragment;
import com.example.newsapp.fragments.PagerFragment;
import com.example.newsapp.fragments.TitlesFragment;
import com.example.newsapp.provider.Contract.News;

public class DetailsActivity extends ActionBarActivity {

	private  ActionBar  actionBar;
	public	MenuItem itemShow;
	private ViewPager  pager;
	private  PagerAdapter pagerAdapter;
	
	public static final String TAG_FRAGMENT ="fragment";
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		
		Bundle bundle = getIntent().getBundleExtra("myBundle");
		if(bundle.getInt(TAG_FRAGMENT)==1){
			setContentView(R.layout.pagerlayout);
			pager = (ViewPager) findViewById(R.id.pager);
			pagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
			pager.setAdapter(pagerAdapter);
			
			pager.setCurrentItem(getIntent().getBundleExtra("myBundle").getInt(TitlesFragment.POSITION),true);
		} else{
			DetailsFragment detailsF = DetailsFragment.newInstance(getIntent().getBundleExtra("myBundle"));
			getSupportFragmentManager().beginTransaction().add(android.R.id.content, detailsF).commit();
		}
		
		 actionBar = getSupportActionBar();
		 actionBar.setTitle(getIntent().getBundleExtra("myBundle").getString("actionBarTitle"));
		 actionBar.setLogo(R.drawable.icon);
		 actionBar.setDisplayHomeAsUpEnabled(true);
		 actionBar.getDisplayOptions();
	}

	
	public boolean onCreateOptionsMenu(Menu menu){
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.details_menu_actions, menu);
		itemShow = menu.findItem(R.id.toShare);
		itemShow.setVisible(false);
	    return true;
	}
	
	
	public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()){
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		case R.id.toShare:
			int messageId;
			if(actionBar.getTitle().equals(getResources().getString(R.string.number_title))){
				     messageId = R.string.toShare_message_number;
			} else { messageId = R.string.toShare_message_news;
			}
			Toast.makeText(this,getResources().getString(messageId) , Toast.LENGTH_SHORT).show();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private class MyFragmentPagerAdapter extends FragmentStatePagerAdapter{

		public MyFragmentPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			Bundle bundle = new Bundle();
			bundle.putInt(TitlesFragment.POSITION, position);
			return PagerFragment.newInstance(bundle);
		}

		
		@Override
		public int getCount() {
			//this.notifyDataSetChanged();
			return getContentResolver().query(News.CONTENT_URI, null, null, null, null).getCount();
		}
		
		
	}
	
	
	
}



