package se.microo.radioclock.model;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Class that parses an .m3u-file and finds
 * the .mp3-stream inside the .m3u.
 * 
 * @author Micro
 */
public class M3UParser {

	private String link;
	
	/**
	 * Creates a new parser from an m3ulink.
	 * 
	 * @param m3ulink Link to m3u.
	 */
	public M3UParser(String m3ulink) {
		if(m3ulink == null) {
			throw new IllegalArgumentException("Cannot connect to null");
		}
		
		this.link = m3ulink;
	}
	
	/**
	 * Parses .mp3 from .m3u.
	 * @return The .mp3 stream.
	 */
	public String parse() {
		String result = "";
		try {
			StringBuilder data = new StringBuilder();
			byte[] buffer = new byte[1024];
			
			URL url = new URL(link);
			InputStream input = url.openStream();
			
			while(input.read(buffer) != -1) {
				data.append(new String(buffer));
			}
			input.close();
			
			// Find mp3-link in file data
			int index = data.indexOf("http://");
			result = data.substring(index, data.indexOf("\n", index));
		} catch(IOException e) {
			//Log.e("Error when receiving data", Log.getStackTraceString(e));
		}
		
		return result;
	}
}
