package com.lbconsulting.homework_312_lorenbak.fragments;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lbconsulting.homework_312_lorenbak.MainActivity;
import com.lbconsulting.homework_312_lorenbak.MyLog;
import com.lbconsulting.homework_312_lorenbak.R;
import com.lbconsulting.homework_312_lorenbak.database.RSS_ImagesTable;
import com.lbconsulting.homework_312_lorenbak.database.RSS_ItemsTable;

public class NewsArticleFragment extends Fragment {

	private long mSelectedArticleID;
	private long mSelectedChannelID;
	private LinearLayout fragNewsArticleLinearLayout;
	private int mImageWidth_px;

	public NewsArticleFragment() {
		// Empty constructor
	}

	public static NewsArticleFragment newInstance(Context context, long articleID) {
		NewsArticleFragment f = new NewsArticleFragment();
		// Supply channelID and articleID inputs as an argument.
		long channelID = RSS_ItemsTable.getChannelID(context, articleID);
		Bundle args = new Bundle();
		args.putLong(MainActivity.STATE_SELECTED_CHANNEL_ID, channelID);
		args.putLong(MainActivity.STATE_SELECTED_ARTICLE_ID, articleID);
		f.setArguments(args);
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		MyLog.i("NewsArticleFragment", "onCreate()");
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		MyLog.i("NewsArticleFragment", "onActivityCreated()");
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onAttach(Activity activity) {
		MyLog.i("NewsArticleFragment", "onAttach()");
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		MyLog.i("NewsArticleFragment", "onCreateView()");

		if (savedInstanceState != null && savedInstanceState.containsKey(MainActivity.STATE_SELECTED_CHANNEL_ID)
				&& savedInstanceState.containsKey(MainActivity.STATE_SELECTED_ARTICLE_ID)) {
			mSelectedChannelID = savedInstanceState.getLong(MainActivity.STATE_SELECTED_CHANNEL_ID, 1);
			mSelectedArticleID = savedInstanceState.getLong(MainActivity.STATE_SELECTED_ARTICLE_ID, -1);
		} else {
			Bundle bundle = getArguments();
			if (bundle != null) {
				mSelectedChannelID = bundle.getLong(MainActivity.STATE_SELECTED_CHANNEL_ID, 0);
				mSelectedArticleID = bundle.getLong(MainActivity.STATE_SELECTED_ARTICLE_ID, 0);
			}
		}

		DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
		int padding_dp = 5;
		int padding_px = Math.round(padding_dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
		mImageWidth_px = (displayMetrics.widthPixels / 2) - padding_px;

		View view = inflater.inflate(R.layout.frag_news_article, container, false);
		fragNewsArticleLinearLayout = (LinearLayout) view.findViewById(R.id.fragNewsArticleLinearLayout);

		if (mSelectedArticleID > 0) {
			Cursor articleCursor = RSS_ItemsTable.getItem(getActivity(), mSelectedArticleID);

			if (fragNewsArticleLinearLayout != null) {
				fragNewsArticleLinearLayout.setVisibility(View.VISIBLE);
			}

			if (articleCursor != null) {
				articleCursor.moveToFirst();

				// set the article's title
				TextView tvArticleTitle = (TextView) view.findViewById(R.id.tvArticleTitle);
				if (tvArticleTitle != null) {
					String title = articleCursor.getString(articleCursor
							.getColumnIndexOrThrow(RSS_ItemsTable.COL_TITLE));
					tvArticleTitle.setText(title);
				}

				// set the article's pub date
				long pubDateValue = articleCursor.getLong(articleCursor
						.getColumnIndexOrThrow(RSS_ItemsTable.COL_PUB_DATE));
				TextView tvArticlePubDate = (TextView) view.findViewById(R.id.tvArticlePubDate);
				if (pubDateValue > 0) {
					int isUpdated = articleCursor.getInt(articleCursor
							.getColumnIndexOrThrow(RSS_ItemsTable.COL_ITEM_UPDATED));
					Calendar cal = Calendar.getInstance();
					cal.setTimeInMillis(pubDateValue);

					SimpleDateFormat format = new SimpleDateFormat("cccc, MMMM d, yyyy,  h:mm a",
							java.util.Locale.getDefault());
					String pubDate = format.format(cal.getTime());
					if (isUpdated == 1) {
						pubDate = "Updated: " + pubDate;
					}

					tvArticlePubDate.setText(pubDate);
					tvArticlePubDate.setVisibility(View.VISIBLE);
				} else {
					// there is no publish date
					tvArticlePubDate.setVisibility(View.GONE);
				}

				// set the article's source
				String articleSource = articleCursor.getString(articleCursor
						.getColumnIndexOrThrow(RSS_ItemsTable.COL_SOURCE));
				TextView tvArticleSource = (TextView) view.findViewById(R.id.tvArticleSource);
				if (articleSource != null && !articleSource.isEmpty()) {
					if (tvArticleSource != null) {
						tvArticleSource.setText(articleSource);
						tvArticleSource.setVisibility(View.VISIBLE);
					}
				} else {
					// there is no source
					if (tvArticleSource != null) {
						tvArticleSource.setVisibility(View.GONE);
					}
				}

				// set the article's author
				String articleAuthor = articleCursor.getString(articleCursor
						.getColumnIndexOrThrow(RSS_ItemsTable.COL_AUTHOR));
				TextView tvArticleAuthor = (TextView) view.findViewById(R.id.tvArticleAuthor);
				if (articleAuthor != null && !articleAuthor.isEmpty()) {
					if (tvArticleAuthor != null) {
						articleAuthor = "By: " + articleAuthor;
						tvArticleAuthor.setText(articleAuthor);
						tvArticleAuthor.setVisibility(View.VISIBLE);
					}
				} else {
					// there is no author
					if (tvArticleAuthor != null) {
						tvArticleAuthor.setVisibility(View.GONE);
					}
				}

				// set the article's image
				long articleImageID = articleCursor.getLong(articleCursor
						.getColumnIndexOrThrow(RSS_ItemsTable.COL_IMAGE_ID));
				LinearLayout articleImageLinearLayout = (LinearLayout) view.findViewById(R.id.articleImageLinearLayout);

				if (articleImageID > 0) {
					Cursor imageCursor = RSS_ImagesTable
							.getImage(getActivity(), mSelectedChannelID, mSelectedArticleID);
					if (imageCursor != null) {
						imageCursor.moveToFirst();
						String imageDescription = imageCursor.getString(imageCursor
								.getColumnIndexOrThrow(RSS_ImagesTable.COL_DESCRIPTION));
						int imageHeight = imageCursor.getInt(imageCursor
								.getColumnIndexOrThrow(RSS_ImagesTable.COL_HEIGHT));
						int imageWidth = imageCursor.getInt(imageCursor
								.getColumnIndexOrThrow(RSS_ImagesTable.COL_WIDTH));
						long imageID = imageCursor.getLong(imageCursor
								.getColumnIndexOrThrow(RSS_ImagesTable.COL_IMAGES_ID));
						String imageKey = String.valueOf(imageID);

						ImageView ivArticleImage = (ImageView) view.findViewById(R.id.ivArticleImage);
						if (ivArticleImage != null) {
							// scale the article image
							int scale = mImageWidth_px / imageWidth;
							ivArticleImage.getLayoutParams().height = imageHeight * scale;
							ivArticleImage.getLayoutParams().width = mImageWidth_px;

							// get the article image bitmap
							Bitmap imageBitmap = MainActivity.getDiskCache().getBitmap(imageKey);
							if (imageBitmap != null) {
								ivArticleImage.setImageBitmap(imageBitmap);

								// set article description
								TextView tvArticleImageDescription = (TextView) view
										.findViewById(R.id.tvArticleImageDescription);
								if (tvArticleImageDescription != null) {
									if (imageDescription != null && !imageDescription.isEmpty()) {
										tvArticleImageDescription.setText(imageDescription);
									} else {
										tvArticleImageDescription.setVisibility(View.INVISIBLE);
									}
								}
							} else {
								// there is no image for this article ...
								if (articleImageLinearLayout != null) {
									articleImageLinearLayout.setVisibility(View.GONE);
								}
							}

						} else {
							// there is no image for this article ...
							if (articleImageLinearLayout != null) {
								articleImageLinearLayout.setVisibility(View.GONE);
							}
						}
						imageCursor.close();
					} else {
						// there is no image for this article ...
						if (articleImageLinearLayout != null) {
							articleImageLinearLayout.setVisibility(View.GONE);
						}

					}
				} else {
					// there is no image for this article ...
					if (articleImageLinearLayout != null) {
						articleImageLinearLayout.setVisibility(View.GONE);
					}

				}

				TextView tvArticleDescription = (TextView) view.findViewById(R.id.tvArticleDescription);
				String articleDescripton = articleCursor.getString(articleCursor
						.getColumnIndexOrThrow(RSS_ItemsTable.COL_DESCRIPTION));

				if (articleDescripton != null && !articleDescripton.isEmpty()) {
					if (tvArticleDescription != null) {
						tvArticleDescription.setText(articleDescripton);
						tvArticleDescription.setVisibility(View.VISIBLE);
					}
				} else {
					// there is no article description
					if (tvArticleDescription != null) {
						tvArticleDescription.setVisibility(View.GONE);
					}
				}

				// show the article web site button
				Button btnShowArticleWebsite = (Button) view.findViewById(R.id.btnShowArticleWebsite);
				if (btnShowArticleWebsite != null) {

					String articleLink = articleCursor.getString(articleCursor
							.getColumnIndexOrThrow(RSS_ItemsTable.COL_LINK));
					if (articleLink != null && !articleLink.isEmpty()) {
						btnShowArticleWebsite.setTag(articleLink);
						btnShowArticleWebsite.setVisibility(View.VISIBLE);

					} else {
						// the article does not have a link for further info
						btnShowArticleWebsite.setVisibility(View.GONE);
					}

					btnShowArticleWebsite.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							String url = (String) v.getTag();
							Intent i = new Intent(Intent.ACTION_VIEW);
							i.setData(Uri.parse(url));
							startActivity(i);
						}
					});
				}
			}

			if (articleCursor != null) {
				articleCursor.close();
			}

		} else {
			// there is no article to show
			if (fragNewsArticleLinearLayout != null) {
				fragNewsArticleLinearLayout.setVisibility(View.GONE);
			}
			/*			if (tvEmptyNewsArticle != null) {
							tvEmptyNewsArticle.setVisibility(View.VISIBLE);
						}*/
		}
		return view;
	}

	public static int dpToPx(Context context, int dp) {
		DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
		int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
		return px;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		MyLog.i("NewsArticleFragment", "onSaveInstanceState()");
		outState.putLong("ActiveChannelID", mSelectedChannelID);
		outState.putLong("ActiveArticleID", mSelectedArticleID);

		super.onSaveInstanceState(outState);
	}

	@Override
	public void onDestroy() {
		MyLog.i("NewsArticleFragment", "onDestroy()");
		super.onDestroy();
	}

	@Override
	public void onDestroyView() {
		MyLog.i("NewsArticleFragment", "onDestroyView()");
		super.onDestroyView();
	}

	@Override
	public void onDetach() {
		MyLog.i("NewsArticleFragment", "onDetach()");
		super.onDetach();
	}

	@Override
	public void onPause() {
		MyLog.i("NewsArticleFragment", "onPause()");
		super.onPause();
	}

	@Override
	public void onResume() {
		MyLog.i("NewsArticleFragment", "onResume()");
		Bundle bundle = getArguments();
		if (bundle != null) {
			mSelectedChannelID = bundle.getLong("ActiveChannelID", 0);
			mSelectedArticleID = bundle.getLong("ActiveArticleID", 0);
		}
		super.onResume();
	}

	@Override
	public void onStart() {
		MyLog.i("NewsArticleFragment", "onStart()");
		super.onStart();
	}

	@Override
	public void onStop() {
		MyLog.i("NewsArticleFragment", "onStop()");
		super.onStop();
	}

}
