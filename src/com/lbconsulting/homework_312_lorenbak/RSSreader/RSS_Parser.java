package com.lbconsulting.homework_312_lorenbak.RSSreader;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.ContentValues;
import android.content.Context;
import android.util.Xml;

import com.lbconsulting.homework_312_lorenbak.MyLog;
import com.lbconsulting.homework_312_lorenbak.database.RSS_ChannelsTable;
import com.lbconsulting.homework_312_lorenbak.database.RSS_ImagesTable;
import com.lbconsulting.homework_312_lorenbak.database.RSS_ItemsTable;
import com.lbconsulting.homework_312_lorenbak.database.RSS_SkipDaysTable;
import com.lbconsulting.homework_312_lorenbak.database.RSS_SkipHoursTable;
import com.lbconsulting.homework_312_lorenbak.database.RSS_TextInputsTable;

public class RSS_Parser {

	// We don't use namespaces
	private static final String ns = null;
	// private static RSS_Channel mRSSChannel;

	private static ContentValues channelsOptionalContentValues;
	private static ContentValues channelsRequiredContentValues;
	private static ArrayList<ContentValues> channelItems;
	private static ArrayList<ContentValues> channelImage;
	private static ArrayList<ContentValues> channelSkipDays;
	private static ArrayList<ContentValues> channelSkipHours;
	private static ArrayList<ContentValues> channelTextInputs;
	private static HashMap<String, ArrayList<ContentValues>> itemsImages = new HashMap<String, ArrayList<ContentValues>>();

	public static void parse(Context context, long channelID, InputStream in) throws XmlPullParserException,
			IOException {
		if (channelID > 1) {
			try {
				XmlPullParser parser = Xml.newPullParser();
				parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
				parser.setInput(in, null);
				parser.nextTag();

				while (!parser.getName().equals(RSS_Channel.TAG_CHANNEL)) {
					parser.nextTag();
				}
				readFeed(parser);
			} finally {
				in.close();
			}

			// refreshed channel data is now in various ContentValues ready to be added to the database
			if (channelHasRequiredElements(channelsRequiredContentValues)) {
				RSS_ChannelsTable.UpdateChannelFieldValues(context, channelID, channelsRequiredContentValues);

				if (channelsOptionalContentValues.size() > 0) {
					RSS_ChannelsTable.UpdateChannelFieldValues(context, channelID, channelsOptionalContentValues);
				}

				if (channelItems.size() > 0) {
					long itemID = -1;
					long itemImageID = -1;
					ContentValues imageContentValues;
					ContentValues itemContentValues;
					for (ContentValues cv : channelItems) {
						itemID = RSS_ItemsTable.CreateItem(context, channelID, cv);
						String itemTitle = cv.getAsString(RSS_ItemsTable.COL_TITLE);
						if (itemsImages.containsKey(itemTitle)) {
							ArrayList<ContentValues> images = itemsImages.get(itemTitle);
							imageContentValues = images.get(0);
							itemImageID = RSS_ImagesTable.CreateImage(context, channelID, itemID, imageContentValues);
							if (itemImageID > 0 && itemID > 0) {
								itemContentValues = new ContentValues();
								itemContentValues.put(RSS_ItemsTable.COL_IMAGE_ID, itemImageID);
								RSS_ItemsTable.UpdateItemlFieldValues(context, itemID, itemContentValues);
							}
						}
					}
				}

				if (channelImage.size() > 0) {
					for (ContentValues cv : channelImage) {
						long imageID = RSS_ImagesTable.CreateImage(context, channelID, cv);
						RSS_ChannelsTable.setImageID(context, channelID, imageID);
					}
				}

				if (channelSkipDays.size() > 0) {
					for (ContentValues cv : channelSkipDays) {
						RSS_SkipDaysTable.CreateSkipDays(context, channelID, cv);
					}
				}

				if (channelSkipHours.size() > 0) {
					for (ContentValues cv : channelSkipHours) {
						RSS_SkipHoursTable.CreateSkipHours(context, channelID, cv);
					}
				}

				if (channelTextInputs.size() > 0) {
					for (ContentValues cv : channelTextInputs) {
						RSS_TextInputsTable.CreateTextInputs(context, channelID, cv);
					}
				}

			}
		}
	}

	private static boolean channelHasRequiredElements(ContentValues channelContentValues) {
		boolean result = false;
		if (channelContentValues.containsKey("description") &&
				channelContentValues.containsKey("title") &&
				channelContentValues.containsKey("link")) {
			result = true;
		}
		return result;
	}

	private static void readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
		// mRSSChannel = new RSS_Channel();
		channelsOptionalContentValues = new ContentValues();
		channelsRequiredContentValues = new ContentValues();
		channelItems = new ArrayList<ContentValues>();
		channelImage = new ArrayList<ContentValues>();
		channelSkipDays = new ArrayList<ContentValues>();
		channelSkipHours = new ArrayList<ContentValues>();
		channelTextInputs = new ArrayList<ContentValues>();

		parser.require(XmlPullParser.START_TAG, ns, RSS_Channel.TAG_CHANNEL);
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}

			String name = parser.getName();

			if (name.equals(RSS_Channel.TAG_CHANNEL_ITEM)) {
				readChannelItem(parser);

			} else if (name.equals(RSS_Channel.TAG_CHANNEL_TITLE)) {
				readChannelTitle(parser);

			} else if (name.equals(RSS_Channel.TAG_CHANNEL_LINK)) {
				readChannelLink(parser);

			} else if (name.equals(RSS_Channel.TAG_CHANNEL_DESCRIPTION)) {
				readChannelDescription(parser);

			} else if (name.equals(RSS_Channel.TAG_CHANNEL_CATEGORY)) {
				readChannelCategory(parser);

			} else if (name.equals(RSS_Channel.TAG_CHANNEL_CLOUD)) {
				readChannelCloud(parser);

			} else if (name.equals(RSS_Channel.TAG_CHANNEL_COPYRIGHT)) {
				readChannelCopyright(parser);

			} else if (name.equals(RSS_Channel.TAG_CHANNEL_DOCS)) {
				readChannelDocs(parser);

			} else if (name.equals(RSS_Channel.TAG_CHANNEL_GENERATOR)) {
				readChannelGenerator(parser);

			} else if (name.equals(RSS_Channel.TAG_CHANNEL_IMAGE)) {
				readChannelImage(parser);

			} else if (name.equals(RSS_Channel.TAG_CHANNEL_LANGUAGE)) {
				readChannelLanguage(parser);

			} else if (name.equals(RSS_Channel.TAG_CHANNEL_LAST_BUILD_DATE)) {
				readChannelLastBuildDate(parser);

			} else if (name.equals(RSS_Channel.TAG_CHANNEL_MANAGING_EDITOR)) {
				readChannelManagingEditor(parser);

			} else if (name.equals(RSS_Channel.TAG_CHANNEL_PUB_DATE)) {
				readChannelPubDate(parser);

			} else if (name.equals(RSS_Channel.TAG_CHANNEL_RATING)) {
				readChannelRating(parser);

			} else if (name.equals(RSS_Channel.TAG_CHANNEL_SKIP_DAYS)) {
				readChannelSkipDays(parser);

			} else if (name.equals(RSS_Channel.TAG_CHANNEL_SKIP_HOURS)) {
				readChannelSkipHours(parser);

			} else if (name.equals(RSS_Channel.TAG_CHANNEL_TEXT_INPUT)) {
				readChannelTextInput(parser);

			} else if (name.equals(RSS_Channel.TAG_CHANNEL_TTL)) {
				readChannelTTL(parser);

			} else if (name.equals(RSS_Channel.TAG_CHANNEL_WEBMASTER)) {
				readChannelWebMaster(parser);

			} else {
				skip(parser);
			}
		}

		// return mRSSChannel;
	}

	private static void readChannelItem(XmlPullParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, ns, RSS_Channel.TAG_CHANNEL_ITEM);

		ContentValues itemsContentValues = new ContentValues();

		String title = null;
		String link = null;
		String description = null;
		String author = null;
		String category = null;
		HashMap<String, String> categoryAttributes = null;
		String comments = null;
		HashMap<String, String> enclosureAttributes = null;
		String guid = null;
		HashMap<String, String> guidAttributes = null;
		long pubDate = -1;
		String source = null;
		HashMap<String, String> sourceAttributes = null;

		String imageURL;
		String imageHeight;
		int height;
		String imageWidth;
		int width;
		String alt;
		String imageDescription;
		ArrayList<ContentValues> itemImages = new ArrayList<ContentValues>();

		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();

			if (name.equals(RSS_Channel.RSS_Item.TAG_TEXT_ITEM_TITLE)) {
				title = readText(parser);
				itemsContentValues.put(RSS_ItemsTable.COL_TITLE, title);

			} else if (name.equals(RSS_Channel.RSS_Item.TAG_TEXT_ITEM_LINK)) {
				link = readText(parser);
				itemsContentValues.put(RSS_ItemsTable.COL_LINK, link);

			} else if (name.equals(RSS_Channel.RSS_Item.TAG_TEXT_ITEM_DESCRIPTION)) {
				description = readText(parser);
				StringBuilder descriptionStringBuilder = new StringBuilder();
				if (description.contains("<p>")) {

					Document doc = Jsoup.parse(description);
					Elements media = doc.select("[src]");
					Elements paragraphs = doc.select("p");

					for (Element eParagraph : paragraphs) {
						descriptionStringBuilder.append(eParagraph.text());
						descriptionStringBuilder.append(System.getProperty("line.separator"));
					}

					for (Element src : media) {
						if (src.tagName().equals("img")) {
							imageURL = src.attr("abs:src");
							imageHeight = src.attr("height");
							height = Integer.parseInt(imageHeight);
							imageWidth = src.attr("width");
							width = Integer.parseInt(imageWidth);
							imageDescription = src.attr("title");
							ContentValues cvImage = new ContentValues();
							cvImage.put(RSS_ImagesTable.COL_URL, imageURL);
							cvImage.put(RSS_ImagesTable.COL_DESCRIPTION, imageDescription);

							// Maximum value for height is 400, default value is 31.
							if (height > 0) {
								if (height > 400) {
									height = 400;
								}
								cvImage.put(RSS_ImagesTable.COL_HEIGHT, height);
							}

							// Maximum value for width is 144, default value is 88.
							if (width > 0) {
								if (width > 144) {
									width = 144;
								}
								cvImage.put(RSS_ImagesTable.COL_WIDTH, width);
							}

							itemImages.add(cvImage);

						}
					}

				} else if (description.contains("<table ")) {
					Document doc = Jsoup.parse(description);
					Elements media = doc.select("[src]");
					Elements paragraphs = doc.select("table");
					/*					Elements hyperLinks = doc.select("a");

										int i = 0;
										for (Element hyperLink : hyperLinks) {
											String text = i + ":" + hyperLink.text();
											MyLog.d("RSS_Parser", text);
											int temp = 0;
											i++;
										}*/

					for (Element eParagraph : paragraphs) {
						descriptionStringBuilder.append(eParagraph.text());
						descriptionStringBuilder.append(System.getProperty("line.separator"));
					}

					for (Element src : media) {
						if (src.tagName().equals("img")) {
							imageURL = "http:" + src.attr("src");
							imageHeight = src.attr("height");
							height = Integer.parseInt(imageHeight);
							imageWidth = src.attr("width");
							width = Integer.parseInt(imageWidth);
							imageDescription = src.attr("title");
							ContentValues cvImage = new ContentValues();
							cvImage.put(RSS_ImagesTable.COL_URL, imageURL);
							cvImage.put(RSS_ImagesTable.COL_DESCRIPTION, imageDescription);

							// Maximum value for height is 400, default value is 31.
							if (height > 0) {
								if (height > 400) {
									height = 400;
								}
								cvImage.put(RSS_ImagesTable.COL_HEIGHT, height);
							}

							// Maximum value for width is 144, default value is 88.
							if (width > 0) {
								if (width > 144) {
									width = 144;
								}
								cvImage.put(RSS_ImagesTable.COL_WIDTH, width);
							}
							itemImages.add(cvImage);
						}
					}

				} else {
					// description does not contain HTTP elements
					descriptionStringBuilder.append(description);
				}

				itemsContentValues.put(RSS_ItemsTable.COL_DESCRIPTION, descriptionStringBuilder.toString());
				if (itemImages.size() > 0) {
					itemsImages.put(title, itemImages);
				}

			} else if (name.equals(RSS_Channel.RSS_Item.TAG_TEXT_ITEM_AUTHOR)) {
				author = readText(parser);
				itemsContentValues.put(RSS_ItemsTable.COL_AUTHOR, author);

			} else if (name.equals(RSS_Channel.RSS_Item.TAG_TEXT_ITEM_CATEGORY)) {
				categoryAttributes = getAttributes(parser);
				if (categoryAttributes != null && categoryAttributes.size() > 0) {
					if (categoryAttributes.containsKey("domain")) {
						itemsContentValues.put(RSS_ItemsTable.COL_CATEGORY_DOMAIN, categoryAttributes.get("domain"));
					}
				}

				category = readText(parser);
				if (category != null && !category.isEmpty()) {
					itemsContentValues.put(RSS_ItemsTable.COL_CATEGORY, category);
				}

			} else if (name.equals(RSS_Channel.RSS_Item.TAG_TEXT_ITEM_COMMENTS)) {
				comments = readText(parser);
				itemsContentValues.put(RSS_ItemsTable.COL_COMMENTS, comments);

			} else if (name.equals(RSS_Channel.RSS_Item.TAG_TEXT_ITEM_ENCLOSURE)) {
				enclosureAttributes = getAttributes(parser);
				if (enclosureAttributes != null && enclosureAttributes.size() > 0) {
					if (enclosureAttributes.containsKey("url") &&
							enclosureAttributes.containsKey("length") &&
							enclosureAttributes.containsKey("type")) {
						itemsContentValues.put(RSS_ItemsTable.COL_ENCLOSURE_URL, enclosureAttributes.get("url"));
						long enclosureLenght = Long.parseLong(enclosureAttributes.get("length"));
						itemsContentValues.put(RSS_ItemsTable.COL_ENCLOSURE_LENGTH, enclosureLenght);
						itemsContentValues.put(RSS_ItemsTable.COL_ENCLOSURE_TYPE, enclosureAttributes.get("type"));
					}
				}

			} else if (name.equals(RSS_Channel.RSS_Item.TAG_TEXT_ITEM_GUID)) {
				guidAttributes = getAttributes(parser);
				if (guidAttributes != null && guidAttributes.size() > 0) {
					if (guidAttributes.containsKey("isPermaLink")) {
						String isPermaLinkStringValue = guidAttributes.get("isPermaLink");
						int isPermaLinkIntValue = 0;
						if (isPermaLinkStringValue.equals("true")) {
							isPermaLinkIntValue = 1;
						}
						itemsContentValues.put(RSS_ItemsTable.COL_GUID_PERMALINK, isPermaLinkIntValue);
					}
				}
				guid = readText(parser);
				itemsContentValues.put(RSS_ItemsTable.COL_GUID, guid);

			} else if (name.equals(RSS_Channel.RSS_Item.TAG_TEXT_ITEM_PUB_DATE)) {
				String strPubDate = readText(parser);
				pubDate = dateStringToMills(strPubDate);
				itemsContentValues.put(RSS_ItemsTable.COL_PUB_DATE, pubDate);

			} else if (name.equals(RSS_Channel.RSS_Item.TAG_TEXT_ITEM_SOURCE)) {
				sourceAttributes = getAttributes(parser);
				if (sourceAttributes != null && sourceAttributes.size() > 0) {
					if (sourceAttributes.containsKey("url")) {
						itemsContentValues.put(RSS_ItemsTable.COL_SOURCE_URL, sourceAttributes.get("url"));
					}
				}
				source = readText(parser);
				itemsContentValues.put(RSS_ItemsTable.COL_SOURCE, source);

			} else {
				skip(parser);
			}
		}

		if (itemHasRequiredElements(itemsContentValues)) {
			channelItems.add(itemsContentValues);
		}

	}

	private static boolean itemHasRequiredElements(ContentValues itemContentValues) {
		boolean result = false;
		if (itemContentValues.containsKey("title") || itemContentValues.containsKey("description")) {
			result = true;
		}
		return result;
	}

	private static void readChannelTitle(XmlPullParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, ns, RSS_Channel.TAG_CHANNEL_TITLE);
		String channelTitle = readText(parser);
		channelsRequiredContentValues.put(RSS_ChannelsTable.COL_TITLE, channelTitle);
		// mRSSChannel.setTitle(channelTitle);
	}

	private static void readChannelLink(XmlPullParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, ns, RSS_Channel.TAG_CHANNEL_LINK);
		String channelLink = readText(parser);
		channelsRequiredContentValues.put(RSS_ChannelsTable.COL_LINK, channelLink);
		// mRSSChannel.setLink(channelLink);
	}

	private static void readChannelDescription(XmlPullParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, ns, RSS_Channel.TAG_CHANNEL_DESCRIPTION);
		String channelDescription = readText(parser);
		channelsRequiredContentValues.put(RSS_ChannelsTable.COL_DESCRIPTION, channelDescription);
		// mRSSChannel.setDescription(channelDescription);
	}

	private static void readChannelCategory(XmlPullParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, ns, RSS_Channel.TAG_CHANNEL_CATEGORY);
		HashMap<String, String> channelCategoryAttributes = getAttributes(parser);
		String channelCategory = readText(parser);
		if (channelCategoryAttributes.containsKey("domain")) {
			channelsOptionalContentValues.put(RSS_ChannelsTable.COL_CATEGORY_DOMAIN,
					channelCategoryAttributes.get("domain"));
		}
		if (!channelCategory.isEmpty()) {
			channelsOptionalContentValues.put(RSS_ChannelsTable.COL_CATEGORY, channelCategory);
		}

		// mRSSChannel.setCategory(channelCategory);
	}

	private static void readChannelCloud(XmlPullParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, ns, RSS_Channel.TAG_CHANNEL_CLOUD);
		String channelCloud = readText(parser);
		channelsOptionalContentValues.put(RSS_ChannelsTable.COL_CLOUD, channelCloud);
		// mRSSChannel.setCloud(channelCloud);
	}

	private static void readChannelCopyright(XmlPullParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, ns, RSS_Channel.TAG_CHANNEL_COPYRIGHT);
		String channelCopyright = readText(parser);
		channelsOptionalContentValues.put(RSS_ChannelsTable.COL_COPYRIGHT, channelCopyright);
		// mRSSChannel.setCopyright(channelCopyright);
	}

	private static void readChannelDocs(XmlPullParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, ns, RSS_Channel.TAG_CHANNEL_DOCS);
		String channelDocs = readText(parser);
		channelsOptionalContentValues.put(RSS_ChannelsTable.COL_DOCS, channelDocs);
		// mRSSChannel.setDocs(channelDocs);
	}

	private static void readChannelGenerator(XmlPullParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, ns, RSS_Channel.TAG_CHANNEL_GENERATOR);
		String channelGenerator = readText(parser);
		channelsOptionalContentValues.put(RSS_ChannelsTable.COL_GENERATOR, channelGenerator);
		// mRSSChannel.setGenerator(channelGenerator);
	}

	private static void readChannelImage(XmlPullParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, ns, RSS_Channel.TAG_CHANNEL_IMAGE);
		String Url = null;
		String Title = null;
		String Link = null;
		int Width = 88;
		int Height = 31;
		String Description = null;

		ContentValues imagesContentValues = new ContentValues();

		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();

			if (name.equals(RSS_Channel.RSS_Image.TAG_IMAGE_URL)) {
				Url = readText(parser);
				imagesContentValues.put(RSS_ImagesTable.COL_URL, Url);

			} else if (name.equals(RSS_Channel.RSS_Image.TAG_IMAGE_LINK)) {
				Link = readText(parser);
				imagesContentValues.put(RSS_ImagesTable.COL_LINK, Link);

			} else if (name.equals(RSS_Channel.RSS_Image.TAG_IMAGE_TITLE)) {
				Title = readText(parser);
				imagesContentValues.put(RSS_ImagesTable.COL_TITLE, Title);

			} else if (name.equals(RSS_Channel.RSS_Image.TAG_IMAGE_WIDTH)) {
				Width = readInt(parser);
				imagesContentValues.put(RSS_ImagesTable.COL_WIDTH, Width);

			} else if (name.equals(RSS_Channel.RSS_Image.TAG_IMAGE_HEIGHT)) {
				Height = readInt(parser);
				imagesContentValues.put(RSS_ImagesTable.COL_HEIGHT, Height);

			} else if (name.equals(RSS_Channel.RSS_Image.TAG_IMAGE_DESCRIPTION)) {
				Description = readText(parser);
				imagesContentValues.put(RSS_ImagesTable.COL_DESCRIPTION, Description);

			} else {
				skip(parser);
			}
		}

		if (imageHasRequiredElements(imagesContentValues)) {
			channelImage.add(imagesContentValues);
		}
		/*		RSS_Image newImage = mRSSChannel.new RSS_Image(Url, Title, Link, Width, Height, Description);
				if (newImage.hasRequiredElements()) {
					mRSSChannel.setImage(newImage);
				}*/

	}

	private static boolean imageHasRequiredElements(ContentValues imageContentValues) {
		boolean result = false;
		if (imageContentValues.containsKey(RSS_ImagesTable.COL_URL) &&
				imageContentValues.containsKey(RSS_ImagesTable.COL_TITLE) &&
				imageContentValues.containsKey(RSS_ImagesTable.COL_LINK)) {
			result = true;
		}
		return result;
	}

	private static void readChannelLanguage(XmlPullParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, ns, RSS_Channel.TAG_CHANNEL_LANGUAGE);
		String channelLanguage = readText(parser);
		channelsOptionalContentValues.put(RSS_ChannelsTable.COL_LANGUAGE, channelLanguage);
		// mRSSChannel.setLanguage(channelLanguage);
	}

	private static void readChannelLastBuildDate(XmlPullParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, ns, RSS_Channel.TAG_CHANNEL_LAST_BUILD_DATE);
		String lastBuildDate = readText(parser);
		long channelLastBuildDate = dateStringToMills(lastBuildDate);
		channelsOptionalContentValues.put(RSS_ChannelsTable.COL_LAST_BUILD_DATE, channelLastBuildDate);
		// mRSSChannel.setLastBuildDate(channelLastBuildDate);
	}

	private static void readChannelManagingEditor(XmlPullParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, ns, RSS_Channel.TAG_CHANNEL_MANAGING_EDITOR);
		String channelManagingEditor = readText(parser);
		channelsOptionalContentValues.put(RSS_ChannelsTable.COL_MANAGING_EDITOR, channelManagingEditor);
		// mRSSChannel.setManagingEditor(channelManagingEditor);
	}

	private static void readChannelPubDate(XmlPullParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, ns, RSS_Channel.TAG_CHANNEL_PUB_DATE);
		String pubDate = readText(parser);
		long channelPubDate = dateStringToMills(pubDate);
		channelsOptionalContentValues.put(RSS_ChannelsTable.COL_PUB_DATE, channelPubDate);
		// mRSSChannel.setPubDate(channelPubDate);
	}

	private static void readChannelRating(XmlPullParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, ns, RSS_Channel.TAG_CHANNEL_RATING);
		String channelRating = readText(parser);
		channelsOptionalContentValues.put(RSS_ChannelsTable.COL_RATING, channelRating);
		// mRSSChannel.setRating(channelRating);
	}

	private static void readChannelSkipDays(XmlPullParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, ns, RSS_Channel.TAG_CHANNEL_SKIP_DAYS);
		ContentValues skipDayslContentValues = new ContentValues();
		String channelSkipDay = null;

		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();

			if (name.equals(RSS_Channel.TAG_SKIP_DAY)) {
				channelSkipDay = readText(parser);
				if (channelSkipDay.equals("Sunday")) {
					skipDayslContentValues.put(RSS_SkipDaysTable.COL_SUNDAY, 1);

				} else if (channelSkipDay.equals("Monday")) {
					skipDayslContentValues.put(RSS_SkipDaysTable.COL_MONDAY, 1);

				} else if (channelSkipDay.equals("Tuesday")) {
					skipDayslContentValues.put(RSS_SkipDaysTable.COL_TUESDAY, 1);

				} else if (channelSkipDay.equals("Wednesday")) {
					skipDayslContentValues.put(RSS_SkipDaysTable.COL_WEDNESDAY, 1);

				} else if (channelSkipDay.equals("Thursday")) {
					skipDayslContentValues.put(RSS_SkipDaysTable.COL_THURSDAY, 1);

				} else if (channelSkipDay.equals("Friday")) {
					skipDayslContentValues.put(RSS_SkipDaysTable.COL_FRIDAY, 1);

				} else if (channelSkipDay.equals("Saturday")) {
					skipDayslContentValues.put(RSS_SkipDaysTable.COL_SATURDAY, 1);
				}

			} else {
				skip(parser);
			}
		}
		channelSkipDays.add(skipDayslContentValues);
	}

	private static void readChannelSkipHours(XmlPullParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, ns, RSS_Channel.TAG_CHANNEL_SKIP_HOURS);
		ContentValues skipHourslContentValues = new ContentValues();
		String channelSkipHour = null;
		int hourOfDayValue = -1;
		String col = null;

		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();

			if (name.equals(RSS_Channel.TAG_SKIP_HOUR)) {
				channelSkipHour = readText(parser);
				hourOfDayValue = Integer.parseInt(channelSkipHour);
				col = "HR" + String.format("%0d2", hourOfDayValue);
				skipHourslContentValues.put(col, 1);

			} else {
				skip(parser);
			}
		}
		channelSkipHours.add(skipHourslContentValues);
	}

	private static void readChannelTextInput(XmlPullParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, ns, RSS_Channel.TAG_CHANNEL_TEXT_INPUT);
		ContentValues textInputsContentValues = new ContentValues();

		String Title = null;
		String Link = null;
		String Description = null;
		String Name = null;

		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();

			if (name.equals(RSS_Channel.RSS_TextInput.TAG_TEXT_INPUT_TITLE)) {
				Title = readText(parser);
				textInputsContentValues.put(RSS_TextInputsTable.COL_TITLE, Title);

			} else if (name.equals(RSS_Channel.RSS_TextInput.TAG_TEXT_INPUT_LINK)) {
				Link = readText(parser);
				textInputsContentValues.put(RSS_TextInputsTable.COL_LINK, Link);

			} else if (name.equals(RSS_Channel.RSS_TextInput.TAG_TEXT_INPUT_DESCRIPTION)) {
				Description = readText(parser);
				textInputsContentValues.put(RSS_TextInputsTable.COL_DESCRIPTION, Description);

			} else if (name.equals(RSS_Channel.RSS_TextInput.TAG_TEXT_INPUT_NAME)) {
				Name = readText(parser);
				textInputsContentValues.put(RSS_TextInputsTable.COL_NAME, Name);

			} else {
				skip(parser);
			}
		}

		channelTextInputs.add(textInputsContentValues);
	}

	private static void readChannelTTL(XmlPullParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, ns, RSS_Channel.TAG_CHANNEL_TTL);
		String channelTTL = readText(parser);
		channelsOptionalContentValues.put(RSS_ChannelsTable.COL_TTL, channelTTL);
		// mRSSChannel.setTTL(channelTTL);
	}

	private static void readChannelWebMaster(XmlPullParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, ns, RSS_Channel.TAG_CHANNEL_WEBMASTER);
		String channelWebMaster = readText(parser);
		channelsOptionalContentValues.put(RSS_ChannelsTable.COL_WEBMASTER, channelWebMaster);
		// mRSSChannel.setWebMaster(channelWebMaster);
	}

	private static void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
		if (parser.getEventType() != XmlPullParser.START_TAG) {
			throw new IllegalStateException();
		}
		int depth = 1;
		while (depth != 0) {
			switch (parser.next()) {
				case XmlPullParser.END_TAG:
					depth--;
					break;
				case XmlPullParser.START_TAG:
					depth++;
					break;
				default:
					break;
			}
		}
	}

	// extracts text values.
	private static String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
		String result = null;
		if (parser.next() == XmlPullParser.TEXT) {
			result = parser.getText();
			parser.nextTag();
		}
		return result;
	}

	// extracts int values
	private static int readInt(XmlPullParser parser) throws IOException, XmlPullParserException {
		int result = -1;
		String strResult = null;
		if (parser.next() == XmlPullParser.TEXT) {
			strResult = parser.getText();
			result = Integer.valueOf(strResult);
			parser.nextTag();
		}
		return result;
	}

	private static HashMap<String, String> getAttributes(XmlPullParser parser) {
		HashMap<String, String> attrs = null;
		int attributeCount = parser.getAttributeCount();
		if (attributeCount > 0) {
			// MyLog.d("RSS_Parser:getAttributes", "Attributes for [" + parser.getName() + "]");
			attrs = new HashMap<String, String>(attributeCount);
			for (int x = 0; x < attributeCount; x++) {
				/*MyLog.d("RSS_Parser:getAttributes", "\t[" + parser.getAttributeName(x) + "]=" +
						"[" + parser.getAttributeValue(x) + "]");*/
				String name = parser.getAttributeName(x);
				String value = parser.getAttributeValue(x);
				if (!name.isEmpty()) {
					attrs.put(name, value);
				}
			}
		}
		else {
			// MyLog.d("RSS_Parser:getAttributes", "Ther are NO ATTRIBUTES for [" + parser.getName() + "]");
		}
		return attrs;
	}

	private static long dateStringToMills(String pubDate) {
		long result = -1;
		SimpleDateFormat formatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH);
		try {
			Date date = formatter.parse(pubDate);
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			result = cal.getTimeInMillis();

		} catch (ParseException e) {
			MyLog.e("RSS_Parser:dateStringToMills", "ParseException wile parsing: " + pubDate);
			e.printStackTrace();
		}

		return result;
	}

}
