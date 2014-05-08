package com.lbconsulting.homework_312_lorenbak;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;

import com.lbconsulting.homework_312_lorenbak.adapters.NewsArticlesPagerAdapter;
import com.lbconsulting.homework_312_lorenbak.database.RSS_ItemsTable;

public class NewsArticleActivity extends FragmentActivity {

	private NewsArticlesPagerAdapter mNewsArticlesPagerAdapter;
	private ViewPager mPager;

	private long mSelectedArticleID = -1;
	private long mSelectedChannelID = -1;
	private int mSelectedArticlePosition = -1;

	private View news_article_pager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyLog.i("NewsArticle_ACTIVITY", "onCreate()");
		setContentView(R.layout.activity_news_articles_pager);

		Bundle args = getIntent().getExtras();
		mSelectedArticleID = args.getLong(MainActivity.STATE_SELECTED_ARTICLE_ID, -1);
		mSelectedChannelID = args.getLong(MainActivity.STATE_SELECTED_CHANNEL_ID, -1);
		mSelectedArticlePosition = args.getInt(MainActivity.STATE_SELECTED_ARTICLE_POSITION, -1);

		SharedPreferences preferences = getSharedPreferences(MainActivity.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
		SharedPreferences.Editor applicationStates = preferences.edit();
		applicationStates.putLong(MainActivity.STATE_SELECTED_ARTICLE_ID, mSelectedArticleID);
		applicationStates.putLong(MainActivity.STATE_SELECTED_CHANNEL_ID, mSelectedChannelID);
		applicationStates.putInt(MainActivity.STATE_SELECTED_ARTICLE_POSITION, mSelectedArticlePosition);
		applicationStates.commit();

		mNewsArticlesPagerAdapter = new NewsArticlesPagerAdapter(getSupportFragmentManager(), this, mSelectedChannelID);
		mPager = (ViewPager) findViewById(R.id.news_article_pager);
		mPager.setAdapter(mNewsArticlesPagerAdapter);
		mPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageScrollStateChanged(int state) {
			}

			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			}

			@Override
			public void onPageSelected(int position) {
				MyLog.d("NewsArticle_ACTIVITY", "onPageSelected() - position = " + position);
				mSelectedArticlePosition = position;
				mSelectedArticleID = mNewsArticlesPagerAdapter.getNewsArticleID(position);
				RSS_ItemsTable.setItemAsRead(NewsArticleActivity.this, mSelectedArticleID); // set Article as read.
			}
		});

	}

	@Override
	protected void onStart() {
		MyLog.i("NewsArticle_ACTIVITY", "onStart()");
		super.onStart();
	}

	@Override
	protected void onResume() {
		MyLog.i("NewsArticle_ACTIVITY", "onResume()");
		SharedPreferences storedStates = getSharedPreferences(MainActivity.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
		mSelectedArticleID = storedStates.getLong(MainActivity.STATE_SELECTED_ARTICLE_ID, -1);
		mSelectedChannelID = storedStates.getLong(MainActivity.STATE_SELECTED_CHANNEL_ID, -1);
		mSelectedArticlePosition = storedStates.getInt(MainActivity.STATE_SELECTED_ARTICLE_POSITION, -1);

		if (mSelectedArticlePosition > -1) {
			mPager.setCurrentItem(mSelectedArticlePosition);
		}
		super.onResume();
	}

	@Override
	protected void onPause() {
		MyLog.i("NewsArticle_ACTIVITY", "onPause()");
		SharedPreferences preferences = getSharedPreferences(MainActivity.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
		SharedPreferences.Editor applicationStates = preferences.edit();

		applicationStates.putLong(MainActivity.STATE_SELECTED_ARTICLE_ID, mSelectedArticleID);
		applicationStates.putLong(MainActivity.STATE_SELECTED_CHANNEL_ID, mSelectedChannelID);
		applicationStates.putInt(MainActivity.STATE_SELECTED_ARTICLE_POSITION, mSelectedArticlePosition);

		applicationStates.commit();
		super.onPause();
	}

	@Override
	protected void onStop() {
		MyLog.i("NewsArticle_ACTIVITY", "onStop()");
		super.onStop();
	}

	@Override
	protected void onRestart() {
		MyLog.i("NewsArticle_ACTIVITY", " onRestart()");
		super.onRestart();
	}

	@Override
	protected void onDestroy() {
		MyLog.i("NewsArticle_ACTIVITY", "onDestroy()");
		super.onDestroy();
	}

}
