package se.microo.radioclock.model;

import java.io.IOException;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import se.microo.radioclock.R;
import android.content.Context;
import android.util.Log;

/**
 * Links a radio channel to a .m3u playlist.
 * 
 * @author Micro
 */
public class ChannelParser {

	private String url;
	
	/**
	 * New ChannelParser.
	 * 
	 * @param c Context to fetch download URL from.
	 */
	public ChannelParser(Context c) {
		if(c == null) {
			throw new IllegalArgumentException("Cannot get link from context: null!");
		}
		this.url = c.getString(R.string.M3U_PAGE);
	}
	
	/**
	 * Parse!
	 * 
	 * @return The parsed radio channels and their playlist links.
	 */
	public ArrayList<SimpleEntry<String, String>> parse() {
		
		ArrayList<SimpleEntry<String, String>> channelLinks = new ArrayList<SimpleEntry<String,String>>();
		
		Document doc;
		try {
			doc = Jsoup.connect(url).get();
			Elements links = doc.getElementsByTag("a"); // Find all links <a>
			
			for(Element link : links) {
				if(link.attr("href").endsWith(".m3u")) {
					// Found text that contains playlists
					String data = link.parent().text();
					data = data.replaceAll("\u00a0"," "); // Replace &nbsp; with space
					
					String extension = ".m3u";
					int startHttp = 0, start = 0;
					/*
					 * Search text for "http://".
					 * Text before "http://" is the channel, text after is the playlist link.
					 */
					while((startHttp = data.indexOf("http://", startHttp+1)) != -1) {
						try {
							int linkEnd = data.indexOf(extension, startHttp)+extension.length();
							String channel = data.substring(start, startHttp).trim();
							String httplink = data.substring(startHttp, linkEnd).trim();
							
							SimpleEntry<String, String> entry = new SimpleEntry<String, String>(channel, httplink);
							
							// Check if channel and link exists. If not, add to list.
							if(!channelLinks.contains(entry)) {
								channelLinks.add(entry);
							}
							
							start = linkEnd; // Set start to be the old linkEnd.
						} catch(Exception e) {
							onException(e);
							break;
						}
					}
				}
			}
		} catch (IOException e) {
			onException(e); // Log exception
		}
		
		return channelLinks;
	}
	
	// Logs an exception with full log.
	private void onException(Exception e) {
		Log.e("Error when receiving data", Log.getStackTraceString(e));
	}
}
