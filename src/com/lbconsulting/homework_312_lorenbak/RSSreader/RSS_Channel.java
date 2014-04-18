package com.lbconsulting.homework_312_lorenbak.RSSreader;

import java.util.ArrayList;
import java.util.HashMap;

// Source: http://www.rssboard.org/rss-specification

public class RSS_Channel {

	public RSS_Channel() {

	}

	public RSS_Channel(String Title, String Link, String Description) {
		setTitle(Title);
		setLink(Link);
		setDescription(Description);
	}

	public final static String TAG_CHANNEL = "channel";
	// REQUIRED elements

	/*title
	 * The name of the channel. It's how people refer to your service. 
	 * If you have an HTML website that contains the same information as your RSS file, 
	 * the title of your channel should be the same as the title of your website.	
	 * Example: GoUpstate.com News Headlines*/
	private String ChannelTitle;
	public final static String TAG_CHANNEL_TITLE = "title";

	/*link
	 * The URL to the HTML website corresponding to the channel.
	 * Example: http://www.goupstate.com/ */
	private String ChannelLink;
	public final static String TAG_CHANNEL_LINK = "link";

	/*description
	 * Phrase or sentence describing the channel.	
	 * Example: The latest news from GoUpstate.com, a Spartanburg Herald-Journal Web site.*/
	private String ChannelDescription;
	public final static String TAG_CHANNEL_DESCRIPTION = "description";

	public boolean hasRequiredElements() {
		return (getTitle() != null && getLink() != null && getDescription() != null) &&
				(!getTitle().isEmpty() && !getLink().isEmpty() && !getDescription().isEmpty());
	};

	// OPTIONAL elements
	/*language	
	 * The language the channel is written in. 
	 * This allows aggregators to group all Italian language sites, for example, on a single page. 
	 * A list of allowable values for this element, as provided by Netscape, is here. 
	 * You may also use values defined by the W3C.	
	 * Example: en-us*/
	private String ChannelLanguage;
	public final static String TAG_CHANNEL_LANGUAGE = "language";

	/*copyright	
	 * Copyright notice for content in the channel.
	 * Example: Copyright 2002, Spartanburg Herald-Journal*/
	private String ChannelCopyright;
	public final static String TAG_CHANNEL_COPYRIGHT = "copyright";

	/*managingEditor
	 * Email address for person responsible for editorial content.	
	 * Example: geo@herald.com (George Matesky)*/
	private String ChannelManagingEditor;
	public final static String TAG_CHANNEL_MANAGING_EDITOR = "managingEditor";

	/*webMaster
	 * Email address for person responsible for technical issues relating to channel.	
	 * Example: betty@herald.com (Betty Guernsey)*/
	private String ChannelWebMaster;
	public final static String TAG_CHANNEL_WEBMASTER = "webMaster";

	/*pubDate
	 * The publication date for the content in the channel. 
	 * For example, the New York Times publishes on a daily basis, the publication date flips once every 24 hours. 
	 * That's when the pubDate of the channel changes. All date-times in RSS conform to the 
	 * Date and Time Specification of RFC 822, with the exception that the year may be expressed with 
	 * two characters or four characters (four preferred).	
	 * Example: Sat, 07 Sep 2002 00:00:01 GMT */
	private long ChannelPubDate;
	public final static String TAG_CHANNEL_PUB_DATE = "pubDate";

	/*lastBuildDate
	 * The last time the content of the channel changed.
	 * Example: Sat, 07 Sep 2002 09:42:31 GMT*/
	private long ChannelLastBuildDate;
	public final static String TAG_CHANNEL_LAST_BUILD_DATE = "lastBuildDate";

	/*category
	 * Specify one or more categories that the channel belongs to. 
	 * Follows the same rules as the <item>-level category element. 
	 * More info.	
	 * Example: <category>Newspapers</category>*/
	private String ChannelCategory;
	private HashMap<String, String> ChannelCategoryAttributes;
	public final static String TAG_CHANNEL_CATEGORY = "category";

	/*generator
	 * A string indicating the program used to generate the channel.
	 * Example: MightyInHouse Content System v2.3*/
	private String ChannelGenerator;
	public final static String TAG_CHANNEL_GENERATOR = "generator";

	/*docs
	 * A URL that points to the documentation for the format used in the RSS file. 
	 * It's probably a pointer to this page. It's for people who might stumble across 
	 * an RSS file on a Web server 25 years from now and wonder what it is.	http://www.rssboard.org/rss-specification*/
	private String ChannelDocs;
	public final static String TAG_CHANNEL_DOCS = "docs";

	/*cloud
	 * Allows processes to register with a cloud to be notified of updates to the channel,
	 * implementing a lightweight publish-subscribe protocol for RSS feeds. More info here: http://www.rssboard.org/rsscloud-interface.	
	 * Example: <cloud domain="rpc.sys.com" port="80" path="/RPC2" registerProcedure="pingMe" protocol="soap"/>*/
	private String ChannelCloud;
	public final static String TAG_CHANNEL_CLOUD = "cloud";

	/*ttl
	 * ttl stands for time to live. It's a number of minutes that indicates how long a channel 
	 * can be cached before refreshing from the source.	
	 * Example: <ttl>60</ttl>*/
	private String ChannelTTL;
	public final static String TAG_CHANNEL_TTL = "ttl";

	/*image
	 * Specifies a GIF, JPEG or PNG image that can be displayed with the channel.*/
	private RSS_Image ChannelImage = new RSS_Image();
	public final static String TAG_CHANNEL_IMAGE = "image";

	/*rating
	 * The PICS rating for the channel.*/
	private String ChannelRating;
	public final static String TAG_CHANNEL_RATING = "rating";

	/*textInput
	 * Specifies a text input box that can be displayed with the channel.*/
	private RSS_TextInput ChannelTextInput = new RSS_TextInput();
	public final static String TAG_CHANNEL_TEXT_INPUT = "textInput";

	/*skipHours
	 * A hint for aggregators telling them which hours they can skip. 
	 * This element contains up to 24 <hour> sub-elements whose value is a number between 0 and 23, 
	 * representing a time in GMT, when aggregators, if they support the feature, may not read the channel 
	 * on hours listed in the <skipHours> element. The hour beginning at midnight is hour zero.*/
	private String ChannelSkipHours;
	public final static String TAG_CHANNEL_SKIP_HOURS = "skipHours";
	public final static String TAG_SKIP_HOUR = "hour";

	/*skipDays
	 * A hint for aggregators telling them which days they can skip. This element contains up to 
	 * seven <day> sub-elements whose value is Monday, Tuesday, Wednesday, Thursday, Friday, Saturday or Sunday. 
	 * Aggregators may not read the channel during days listed in the <skipDays> element.*/
	private String ChannelSkipDays;
	public final static String TAG_CHANNEL_SKIP_DAYS = "skipDays";
	public final static String TAG_SKIP_DAY = "day";

	private ArrayList<RSS_Item> Items = new ArrayList<RSS_Item>();
	public final static String TAG_CHANNEL_ITEM = "item";

	public String getTitle() {
		return ChannelTitle;
	}

	public void setTitle(String channelTitle) {
		ChannelTitle = channelTitle;
	}

	public String getLink() {
		return ChannelLink;
	}

	public void setLink(String channelLink) {
		ChannelLink = channelLink;
	}

	public String getDescription() {
		return ChannelDescription;
	}

	public void setDescription(String channelDescription) {
		ChannelDescription = channelDescription;
	}

	public String getLanguage() {
		return ChannelLanguage;
	}

	public void setLanguage(String channelLanguage) {
		ChannelLanguage = channelLanguage;
	}

	public String getCopyright() {
		return ChannelCopyright;
	}

	public void setCopyright(String channelCopyright) {
		ChannelCopyright = channelCopyright;
	}

	public String getManagingEditor() {
		return ChannelManagingEditor;
	}

	public void setManagingEditor(String channelManagingEditor) {
		ChannelManagingEditor = channelManagingEditor;
	}

	public String getWebMaster() {
		return ChannelWebMaster;
	}

	public void setWebMaster(String channelWebMaster) {
		ChannelWebMaster = channelWebMaster;
	}

	public long getPubDate() {
		return ChannelPubDate;
	}

	public void setPubDate(long channelPubDate) {
		ChannelPubDate = channelPubDate;
	}

	public long getLastBuildDate() {
		return ChannelLastBuildDate;
	}

	public void setLastBuildDate(long channelLastBuildDate) {
		ChannelLastBuildDate = channelLastBuildDate;
	}

	public String getCategory() {
		return ChannelCategory;
	}

	public void setCategory(String channelCategory) {
		ChannelCategory = channelCategory;
	}

	public HashMap<String, String> getChannelCategoryAttributes() {
		return ChannelCategoryAttributes;
	}

	public void setChannelCategoryAttributes(HashMap<String, String> channelCategoryAttributes) {
		ChannelCategoryAttributes = channelCategoryAttributes;
	}

	public String getGenerator() {
		return ChannelGenerator;
	}

	public void setGenerator(String channelGenerator) {
		ChannelGenerator = channelGenerator;
	}

	public String getDocs() {
		return ChannelDocs;
	}

	public void setDocs(String channelDocs) {
		ChannelDocs = channelDocs;
	}

	public String getCloud() {
		return ChannelCloud;
	}

	public void setCloud(String channelCloud) {
		ChannelCloud = channelCloud;
	}

	public String getTTL() {
		return ChannelTTL;
	}

	public void setTTL(String channelTTL) {
		ChannelTTL = channelTTL;
	}

	public RSS_Image getImage() {
		if (ChannelImage.hasRequiredElements()) {
			return ChannelImage;
		}
		return null;
	}

	public void setImage(RSS_Image channelImage) {
		ChannelImage = channelImage;
	}

	public String getRating() {
		return ChannelRating;
	}

	public void setRating(String channelRating) {
		ChannelRating = channelRating;
	}

	public RSS_TextInput getTextInput() {
		if (ChannelTextInput.hasRequiredElements()) {
			return ChannelTextInput;
		}
		return null;
	}

	public void setTextInput(RSS_TextInput channelTextInput) {
		ChannelTextInput = channelTextInput;
	}

	public String getSkipHours() {
		return ChannelSkipHours;
	}

	public void setSkipHours(String channelSkipHours) {
		ChannelSkipHours = channelSkipHours;
	}

	public String getSkipDays() {
		return ChannelSkipDays;
	}

	public void setSkipDays(String channelSkipDays) {
		ChannelSkipDays = channelSkipDays;
	}

	public ArrayList<RSS_Item> getItems() {
		return Items;
	}

	public int getItemsCount() {
		return Items.size();
	}

	/*	public void setItems(ArrayList<RSS_Item> items) {
			Items = items;
		}*/

	public class RSS_Image {

		/*<image> is an optional sub-element of <channel>
		 * It contains three required and three optional sub-elements.
		 * The required elements are:
		 * <url> is the URL of a GIF, JPEG or PNG image that represents the channel.
		 * <title> describes the image, it's used in the ALT attribute of the HTML <img> tag when the channel is rendered in HTML.
		 * <link> is the URL of the site, when the channel is rendered, the image is a link to the site. 
		 * (Note, in practice the image <title> and <link> should have the same value as the channel's <title> and <link>.
		 * 
		 * Optional elements include 
		 * <width> and <height>, numbers, indicating the width and height of the image in pixels. 
		 * 		Maximum value for width is 144, default value is 88.
		 * 		Maximum value for height is 400, default value is 31.
		 * <description> contains text that is included in the TITLE attribute of the link formed around the image in the HTML rendering.*/

		private String Url;
		public final static String TAG_IMAGE_URL = "url";
		private String Title;
		public final static String TAG_IMAGE_TITLE = "title";
		private String Link;
		public final static String TAG_IMAGE_LINK = "link";

		private int Width = 88;
		public final static String TAG_IMAGE_WIDTH = "width";
		private int Height = 31;
		public final static String TAG_IMAGE_HEIGHT = "height";
		private String Description;
		public final static String TAG_IMAGE_DESCRIPTION = "description";

		public RSS_Image() {
		}

		public RSS_Image(String Url, String Title, String Link, int Width, int Height, String Description) {
			setUrl(Url);
			setTitle(Title);
			setLink(Link);
			setWidth(Width);
			setHeight(Height);
			setDescription(Description);
		}

		public boolean hasRequiredElements() {
			return (getUrl() != null && getTitle() != null && getLink() != null) &&
					(!getUrl().isEmpty() && !getTitle().isEmpty() && !getLink().isEmpty());
		}

		public String getUrl() {
			return Url;
		}

		public void setUrl(String url) {
			Url = url;
		}

		public String getTitle() {
			return Title;
		}

		public void setTitle(String title) {
			Title = title;
		}

		public String getLink() {
			return Link;
		}

		public void setLink(String link) {
			Link = link;
		}

		public int getWidth() {
			return Width;
		}

		public void setWidth(int width) {
			if (width > 144) {
				Width = 144;
			} else {
				Width = width;
			}
		}

		public int getHeight() {
			return Height;
		}

		public void setHeight(int height) {
			if (height > 400) {
				Height = 400;
			} else {
				Height = height;
			}
		}

		public String getDescription() {
			return Description;
		}

		public void setDescription(String description) {
			Description = description;
		}

	}

	public class RSS_TextInput {

		/*A channel may optionally contain a <textInput> sub-element, which contains four required sub-elements.
		 * <title> -- The label of the Submit button in the text input area.
		 * <description> -- Explains the text input area.
		 * <name> -- The name of the text object in the text input area.
		 * <link> -- The URL of the CGI script that processes text input requests.
		 * 
		 * The purpose of the <textInput> element is something of a mystery. 
		 * You can use it to specify a search engine box. Or to allow a reader to provide feedback. 
		 * Most aggregators ignore it.*/
		private String Title;
		public final static String TAG_TEXT_INPUT_TITLE = "title";
		private String Link;
		public final static String TAG_TEXT_INPUT_LINK = "link";
		private String Description;
		public final static String TAG_TEXT_INPUT_DESCRIPTION = "description";
		private String Name;
		public final static String TAG_TEXT_INPUT_NAME = "name";

		public RSS_TextInput() {

		}

		public RSS_TextInput(String Title, String Link, String Description, String Name) {
			setTitle(Title);
			setLink(Link);
			setDescription(Description);
			setName(Name);
		}

		public boolean hasRequiredElements() {
			return (getTitle() != null && getLink() != null && getDescription() != null && getName() != null)
					&&
					(!getTitle().isEmpty() && !getLink().isEmpty() && !getDescription().isEmpty() && !getName()
							.isEmpty());
		}

		public String getTitle() {
			return Title;
		}

		public void setTitle(String title) {
			Title = title;
		}

		public String getLink() {
			return Link;
		}

		public void setLink(String link) {
			Link = link;
		}

		public String getDescription() {
			return Description;
		}

		public void setDescription(String description) {
			Description = description;
		}

		public String getName() {
			return Name;
		}

		public void setName(String name) {
			Name = name;
		}

	}

	public class RSS_Item {

		/* A channel may contain any number of <item>s. 
		 * An item may represent a "story" -- much like a story in a newspaper or magazine; 
		 * if so its description is a synopsis of the story, and the link points to the full story. 
		 * An item may also be complete in itself, if so, the description contains the text 
		 * (entity-encoded HTML is allowed; see examples), and the link and title may be omitted. 
		 * 
		 * All elements of an item are optional, however at least one of title or description must be present.
		 * 	title - The title of the item.	
		 * 		Example: Venice Film Festival Tries to Quit Sinking
		 * 
		 *  link - The URL of the item.	
		 *  	Example: http://nytimes.com/2004/12/07FEST.html
		 *  
		 *  description - The item synopsis.	
		 *  	Example: <description>Some of the most heated chatter at the Venice Film Festival this week was about 
		 *  			 the way that the arrival of the stars at the Palazzo del Cinema was being staged.</description>
		 *  
		 *  author - Email address of the author of the item.
		 *  	It's the email address of the author of the item. For newspapers and magazines syndicating via RSS, 
		 *  	the author is the person who wrote the article that the <item> describes. For collaborative weblogs, 
		 *  	the author of the item might be different from the managing editor or webmaster. For a weblog authored 
		 *  	by a single individual it would make sense to omit the <author> element.
		 *  
		 *  category - Includes the item in one or more categories.
		 *  	Category has one optional attribute, domain, a string that identifies a categorization taxonomy.
		 *  	The value of the element is a forward-slash-separated string that identifies a hierarchic location 
		 *  	in the indicated taxonomy. Processors may establish conventions for the interpretation of categories. 
		 *  	Two examples are provided below:
		 *  		<category>Grateful Dead</category>
		 *  		<category domain="http://www.fool.com/cusips">MSFT</category>
		 * 		You may include as many category elements as you need to, for different domains, and to have an item 
		 * 		cross-referenced in different parts of the same domain.
		 *  
		 *  comments - URL of a page for comments relating to the item.
		 *  	If present, it is the url of the comments page for the item.
		 *  	Example: <comments>http://ekzemplo.com/entry/4403/comments</comments>
		 *  	More about comments here: http://www.rssboard.org/rss-weblog-comments-use-case
		 *  
		 *  enclosure - Describes a media object that is attached to the item.
		 *  	It has three required attributes. 
		 *  		url says where the enclosure is located, 
		 *  		length says how big it is in bytes, and 
		 *  		type says what its type is, a standard MIME type.
		 *  	The url must be an http url.
		 *  	Example: <enclosure url="http://www.scripting.com/mp3s/weatherReportSuite.mp3" length="12216320" type="audio/mpeg" />
		 *  	A use-case narrative for this element is here: http://www.rssboard.org/rss-enclosures-use-case
		 *  
		 *  guid - A string that uniquely identifies the item.
		 *  	guid stands for globally unique identifier. It's a string that uniquely identifies the item. When present, 
		 *  	an aggregator may choose to use this string to determine if an item is new.
		 *  	Example: <guid>http://some.server.com/weblogItem3207</guid>
		 *  
		 *  	There are no rules for the syntax of a guid. Aggregators must view them as a string. It's up to the source of the feed 
		 *  	to establish the uniqueness of the string. 
		 *  
		 *  	If the guid element has an attribute named isPermaLink with a value of true, 
		 *  	the reader may assume that it is a permalink to the item, that is, a url that can be opened in a Web browser, that points 
		 *  	to the full item described by the <item> element. 
		 *  	An example: <guid isPermaLink="true">http://inessential.com/2002/09/01.php#a2</guid>
		 *  	isPermaLink is optional, its default value is true. If its value is false, the guid may not be assumed to be a url, 
		 *  	or a url to anything in particular.

		 *  
		 *  pubDate - Indicates when the item was published.
		 *  	Its value is a date, indicating when the item was published. If it's a date in the future, aggregators may choose to 
		 *  	not display the item until that date.
		 *  	Example: <pubDate>Sun, 19 May 2002 15:21:36 GMT</pubDate>
		 *  
		 *  source - The RSS channel that the item came from. More.
		 *  	Its value is the name of the RSS channel that the item came from, derived from its <title>. 
		 *  	It has one required attribute, url, which links to the XMLization of the source.
		 *  	Example: <source url="http://www.tomalak.org/links2.xml">Tomalak's Realm</source>
		 *  
		 *  	The purpose of this element is to propagate credit for links, to publicize the sources of news items. 
		 *  	It can be used in the Post command of an aggregator. It should be generated automatically when forwarding an item 
		 *  	from an aggregator to a weblog authoring tool.
		 *  
		 *  */

		private String title;
		public final static String TAG_TEXT_ITEM_TITLE = "title";
		private String link;
		public final static String TAG_TEXT_ITEM_LINK = "link";

		private String description;
		private HashMap<String, String> descriptionAttributes;
		public final static String TAG_TEXT_ITEM_DESCRIPTION = "description";
		/*public final static String TAG_TEXT_HTML_PARAGRAPH = "p";
		public final static String TAG_TEXT_HTML_HYPERLINK = "a";*/

		private String author;
		public final static String TAG_TEXT_ITEM_AUTHOR = "author";

		private String category;
		private HashMap<String, String> categoryAttributes;
		public final static String TAG_TEXT_ITEM_CATEGORY = "category";

		private String comments;
		public final static String TAG_TEXT_ITEM_COMMENTS = "comments";

		// private String enclosure;
		private HashMap<String, String> enclosureAttributes;
		public final static String TAG_TEXT_ITEM_ENCLOSURE = "enclosure";

		private String guid;
		private HashMap<String, String> guidAttributes;
		public final static String TAG_TEXT_ITEM_GUID = "guid";

		private long pubDate;
		public final static String TAG_TEXT_ITEM_PUB_DATE = "pubDate";

		private String source;
		private HashMap<String, String> sourceAttributes;
		public final static String TAG_TEXT_ITEM_SOURCE = "source";

		public RSS_Item(String title, String link, String description, String author, String category,
				String comments, String guid, long pubDate, String source) {
			setTitle(title);
			setLink(link);
			setDescription(description);
			setAuthor(author);
			setCategory(category);
			setComments(comments);
			setGuid(guid);
			setPubDate(pubDate);
			setSource(source);

		}

		public boolean hasRequiredElements() {
			return ((getTitle() != null && !getTitle().isEmpty())
			|| (getDescription() != null && !getDescription().isEmpty()));
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getLink() {
			return link;
		}

		public void setLink(String link) {
			this.link = link;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public String getAuthor() {
			return author;
		}

		public void setAuthor(String author) {
			this.author = author;
		}

		public String getCategory() {
			return category;
		}

		public void setCategory(String category) {
			this.category = category;
		}

		public HashMap<String, String> getCategoryAttributes() {
			return categoryAttributes;
		}

		public void setCategoryAttributes(HashMap<String, String> categoryAttributes) {
			this.categoryAttributes = categoryAttributes;
		}

		public String getComments() {
			return comments;
		}

		public void setComments(String comments) {
			this.comments = comments;
		}

		/*		public String getEnclosure() {
					return enclosure;
				}

				public void setEnclosure(String enclosure) {
					this.enclosure = enclosure;
				}*/

		public HashMap<String, String> getEnclosureAttributes() {
			return enclosureAttributes;
		}

		public void setEnclosureAttributes(HashMap<String, String> enclosureAttributes) {
			this.enclosureAttributes = enclosureAttributes;
		}

		public String getGuid() {
			return guid;
		}

		public void setGuid(String guid) {
			this.guid = guid;
		}

		public HashMap<String, String> getGuidAttributes() {
			return guidAttributes;
		}

		public void setGuidAttributes(HashMap<String, String> guidAttributes) {
			this.guidAttributes = guidAttributes;
		}

		public long getPubDate() {
			return pubDate;
		}

		public void setPubDate(long pubDate) {
			this.pubDate = pubDate;
		}

		public String getSource() {
			return source;
		}

		public void setSource(String source) {
			this.source = source;
		}

		public HashMap<String, String> getSourceAttributes() {
			return sourceAttributes;
		}

		public void setSourceAttributes(HashMap<String, String> sourceAttributes) {
			this.sourceAttributes = sourceAttributes;
		}

		public HashMap<String, String> getDescriptionAttributes() {
			return descriptionAttributes;
		}

		public void setDescriptionAttributes(HashMap<String, String> descriptionAttributes) {
			this.descriptionAttributes = descriptionAttributes;

		}

	}

}
