package com.example.newsapp.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.newsapp.R;
import com.example.newsapp.fragments.DetailsFragment;
import com.example.newsapp.fragments.NumberFragment;
import com.example.newsapp.fragments.PagerFragment;
import com.example.newsapp.fragments.TitlesFragment;
import com.example.newsapp.fragments.TitlesFragment.onItemClickListener;
import com.example.newsapp.service.RefreshService;


public class MainActivity extends  ActionBarActivity implements onItemClickListener {

	private boolean withDetails ;
		
	//// Creating  a Navigation Drawer
	private String[] mTitles;
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	public ActionBar actionBar;   // using in method onResume in Fragments ,  onDrawerOpened and onItemClick  for DrawerLayout 
	private String currentTitle ;
	private boolean itemOfDrawerWasClicked = false;
	
	private boolean containerIsEmpty = false;
	private Fragment titlesF;
	private Fragment numberF;
	private String FRAGMENT_TAG_TITLE = "titles";
	private String FRAGMENT_TAG_NUMBER = "numbers";
	
	public View _rootView1 = null;
	public MenuItem itemShowMain;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
			
		
		if((getResources().getConfiguration().screenLayout  &
		Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE){
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		} else {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
			
		
		titlesF = new TitlesFragment();
		getSupportFragmentManager().beginTransaction().add(R.id.TitlesCont, titlesF,FRAGMENT_TAG_TITLE).addToBackStack(null).commit();
		withDetails = (findViewById(R.id.Detailscont) !=null);
		if(withDetails){
			//showDetails(null);
		}
		
		
		mTitles = getResources().getStringArray(R.array.mTitles);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);
		mDrawerList.setAdapter(new ArrayAdapter<>(this,R.layout.drawer_list_item , mTitles));
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_drawer, R.string.open, R.string.close) {

          		
			/** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                if(!itemOfDrawerWasClicked)
                actionBar.setTitle(currentTitle);
                Log.d("myLogs","close");
                 
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                currentTitle= (String) actionBar.getTitle();
                actionBar.setTitle(getResources().getString(R.string.draw_title));
                Log.d("myLogs","open");
                itemOfDrawerWasClicked= false;
            }
            
            
        };

		mDrawerLayout.setDrawerListener(mDrawerToggle);
		
		
		mDrawerList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				itemOfDrawerWasClicked= true;
				FragmentManager fm= getSupportFragmentManager();
				switch(position){
				case 0:
					Log.d("myLogs","click on Drawer");
					titlesF = getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG_TITLE);
					if(titlesF !=null )
					Log.d("myLogs","titlesF.getTag() = " + titlesF.getTag()+ titlesF.isVisible());
					if(titlesF ==null ){ 
						Log.d("myLogs","titlesF == null");
						titlesF = new TitlesFragment();
						fm.beginTransaction().replace(R.id.TitlesCont, titlesF,FRAGMENT_TAG_TITLE).addToBackStack(null).commit();
					} else {
						fm.beginTransaction().replace(R.id.TitlesCont, titlesF,FRAGMENT_TAG_TITLE).commit(); 
						}
					if(containerIsEmpty){
						fm.beginTransaction().replace(R.id.TitlesCont, titlesF,FRAGMENT_TAG_TITLE).commit();
						containerIsEmpty = false;
					}
					actionBar.setTitle(mTitles[position]);
					break;
				case 1:
					numberF  = getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG_NUMBER);
					if(numberF  ==null ){
						numberF  = new NumberFragment();
						fm.beginTransaction().replace(R.id.TitlesCont, numberF ,FRAGMENT_TAG_NUMBER).addToBackStack(null).commit();
					} else {
						fm.beginTransaction().replace(R.id.TitlesCont, numberF ,FRAGMENT_TAG_NUMBER).commit();}
					if(containerIsEmpty){
						fm.beginTransaction().replace(R.id.TitlesCont, numberF ,FRAGMENT_TAG_NUMBER).commit();
						containerIsEmpty = false;}
					actionBar.setTitle(mTitles[position]);
			break;
					}
				mDrawerLayout.closeDrawer(mDrawerList);
		
			}
		});
				
		actionBar = getSupportActionBar();
		actionBar.setLogo(getResources().getDrawable(R.drawable.icon));
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
}
	
	
	
	@Override
	public void onBackPressed() {
		
		Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.TitlesCont);
		if(fragment.isVisible()){
			getSupportFragmentManager().beginTransaction().remove(fragment).commit(); 
			fragment= null;
			containerIsEmpty = true;
		} else{
			finish();
		  }
	}

	
	protected void onPostCreate(Bundle savedInstanceState){
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}
	
	public void onConfigurationChanged(Configuration newConfig){
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}
	
	
	public boolean onOptionsItemSelected(MenuItem item){
		 
		if(mDrawerToggle.onOptionsItemSelected(item)){
			return true;
		}
		
	switch(item.getItemId()){
	case R.id.toShare:
		String message="";
		if(actionBar.getTitle().equals(getResources().getString(R.string.number_title))){
			message = getResources().getString(R.string.toShare_message_number);
		} else { message = getResources().getString(R.string.toShare_message_news);
			}
		Toast.makeText(this,message , Toast.LENGTH_SHORT).show();
		return true;
	case R.id.refresh:
		this.startService(item.getIntent());
		TitlesFragment.setTaskIsRunning();
		return true;
	
	}
	return super.onOptionsItemSelected(item);
	}
	

	public boolean onCreateOptionsMenu(Menu menu){
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.details_menu_actions, menu);
		itemShowMain = menu.findItem(R.id.toShare);
		itemShowMain.setVisible(false);
		
		Intent intent = new Intent(this,RefreshService.class);
		intent.putExtra("url", TitlesFragment.url+"1");
		MenuItem refresh = menu.findItem(R.id.refresh);
		refresh.setIntent(intent);
		return true;
	}
	

	private void showDetails(Bundle myBundle) {
		if(withDetails){
			
			
			if(myBundle.getInt(DetailsActivity.TAG_FRAGMENT)==1){
				PagerFragment detailsP;
				try{
				 detailsP = (PagerFragment) getSupportFragmentManager().findFragmentById(R.id.Detailscont);
			} catch(ClassCastException e){
				 detailsP = PagerFragment.newInstance(myBundle);
				 getSupportFragmentManager().beginTransaction().replace(R.id.Detailscont, detailsP).commit();
			}
				
				if(detailsP == null||detailsP.getBundle() != myBundle){
					detailsP = PagerFragment.newInstance(myBundle);
					getSupportFragmentManager().beginTransaction().replace(R.id.Detailscont, detailsP).commit();
				}
			} else{
				DetailsFragment detailsF;
				try{
					 detailsF = (DetailsFragment) getSupportFragmentManager().findFragmentById(R.id.Detailscont);
				} catch(ClassCastException e){
					detailsF = DetailsFragment.newInstance(myBundle);
					getSupportFragmentManager().beginTransaction().replace(R.id.Detailscont, detailsF).commit();
				}
				
				if(detailsF == null||detailsF.getBundle() != myBundle){
					detailsF = DetailsFragment.newInstance(myBundle);
					getSupportFragmentManager().beginTransaction().replace(R.id.Detailscont, detailsF).commit();
				}
			}
		} else{
			Intent intent = new Intent(this, DetailsActivity.class);
			intent.putExtra("myBundle", myBundle);
			startActivity(intent);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.TitlesCont);
		if(fragment !=null){
			if(fragment.getTag().equalsIgnoreCase(FRAGMENT_TAG_TITLE)){
				actionBar.setTitle(getResources().getStringArray(R.array.mTitles)[0]);
			}  else if(fragment.getTag().equalsIgnoreCase(FRAGMENT_TAG_NUMBER)){
				actionBar.setTitle(getResources().getStringArray(R.array.mTitles)[1]);
				
			}
		}
	}
	
	@Override
	public void imetClick(Bundle myBundle) {
		showDetails( myBundle);
	}
}
