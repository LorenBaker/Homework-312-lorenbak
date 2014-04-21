package com.lbconsulting.homework_312_lorenbak;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;

import com.lbconsulting.homework_312_lorenbak.adapters.NewsArticlesPagerAdapter;

public class NewsArticleActivity extends FragmentActivity {

	private NewsArticlesPagerAdapter mNewsArticlesPagerAdapter;
	private ViewPager mPager;

	private long mActiveArticleID = -1;
	private long mActiveChannelID = -1;
	private int mActivePosition = -1;

	// private NewsArticleFragment mNewsArticleFragment;
	private View news_article_pager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyLog.i("NewsArticle_ACTIVITY", "onCreate()");
		setContentView(R.layout.activity_news_articles_pager);

		/*		news_article_pager = this.findViewById(R.id.news_article_pager);
				if (news_article_pager == null) {
					finish();
				}*/

		Bundle args = getIntent().getExtras();
		mActiveArticleID = args.getLong("ActiveArticleID", -1);
		mActiveChannelID = args.getLong("ActiveChannelID", -1);
		mActivePosition = args.getInt("ActivePosition", -1);

		SharedPreferences preferences = getSharedPreferences("HW312", MODE_PRIVATE);
		SharedPreferences.Editor applicationStates = preferences.edit();
		applicationStates.putLong("ActiveArticleID", mActiveArticleID);
		applicationStates.putLong("ActiveChannelID", mActiveChannelID);
		applicationStates.putInt("ActivePosition", mActivePosition);
		applicationStates.commit();

		mNewsArticlesPagerAdapter = new NewsArticlesPagerAdapter(getSupportFragmentManager(), this, mActiveChannelID);
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
				mActivePosition = position;
				// A list page has been selected
				// setActiveArticleID(position);
			}
		});

		// LoadNewsArticleFragment();

	}

	/*	private void LoadNewsArticleFragment() {
			mNewsArticleFragment = (NewsArticleFragment) this.getSupportFragmentManager()
					.findFragmentByTag("NewsArticleFragment");
			if (mNewsArticleFragment == null) {
				// create NewsArticleFragment
				mNewsArticleFragment = NewsArticleFragment.newInstance(this, mActiveArticleID);
				MyLog.i("NewsArticle_ACTIVITY", "LoadNewsArticleFragment():NewInstance: articleID=" + mActiveArticleID);

				// add the fragment to the Activity
				this.getSupportFragmentManager().beginTransaction()
						.add(R.id.frag_news_article_placeholder, mNewsArticleFragment, "NewsArticleFragment")
						.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
						.commit();
			} else {
				// mNewsArticleFragment exists ... so replace it
				mNewsArticleFragment = NewsArticleFragment.newInstance(this, mActiveArticleID);
				// add the fragment to the Activity
				this.getSupportFragmentManager().beginTransaction()
						.replace(R.id.frag_news_article_placeholder, mNewsArticleFragment, "NewsArticleFragment")
						.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
						.commit();
			}

		}*/

	@Override
	protected void onStart() {
		MyLog.i("NewsArticle_ACTIVITY", "onStart()");
		super.onStart();
	}

	@Override
	protected void onResume() {
		MyLog.i("NewsArticle_ACTIVITY", "onResume()");
		SharedPreferences storedStates = getSharedPreferences("HW312", MODE_PRIVATE);
		mActiveArticleID = storedStates.getLong("ActiveArticleID", -1);
		mActiveChannelID = storedStates.getLong("ActiveChannelID", -1);
		mActivePosition = storedStates.getInt("ActivePosition", -1);

		if (mActivePosition > -1) {
			mPager.setCurrentItem(mActivePosition);
		}
		super.onResume();
	}

	@Override
	protected void onPause() {
		MyLog.i("NewsArticle_ACTIVITY", "onPause()");
		SharedPreferences preferences = getSharedPreferences("HW312", MODE_PRIVATE);
		SharedPreferences.Editor applicationStates = preferences.edit();
		applicationStates.putLong("ActiveArticleID", mActiveArticleID);
		applicationStates.putLong("ActiveChannelID", mActiveChannelID);
		applicationStates.putInt("ActivePosition", mActivePosition);
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
