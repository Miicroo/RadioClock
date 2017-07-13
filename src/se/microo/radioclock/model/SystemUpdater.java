package se.microo.radioclock.model;

import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;

import se.microo.radioclock.model.data.Channel;
import se.microo.radioclock.model.data.Postback;
import se.microo.radioclock.util.Storage;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

/**
 * Updates the system in the background using AsyncTask (so that it can be called
 * by an {@link Activity}. {@link Storage} will be updated with a new list
 * containing valid {@link Channel}s.
 * 
 * If a {@link Postback} is provided, it will be notified upon task finish.
 * 
 * @author Micro
 */
public class SystemUpdater extends AsyncTask<Context, Void, Void> {
	
	private Postback parent;

	public SystemUpdater(Postback w) {
		this.parent = w;
	}

	@Override
	protected Void doInBackground(Context... x) {
		if (x == null || x.length < 1 || x[0] == null) {
			throw new IllegalArgumentException("Cannot use context: null");
		}
		
		Context context = x[0]; // Context to get URL from.
		
		// Parse channel and m3u.
		ChannelParser cp = new ChannelParser(context);
		ArrayList<AbstractMap.SimpleEntry<String, String>> cls = cp.parse();

		ArrayList<Channel> channels = new ArrayList<Channel>();
		for (SimpleEntry<String, String> entry : cls) {
			M3UParser parser = new M3UParser(entry.getValue());
			String mp3link = parser.parse(); // Parse mp3 link

			// Add channel object.
			channels.add(new Channel(entry.getKey(), entry.getValue(), mp3link));
		}

		Storage storage = new Storage(context);
		storage.updateStorage(channels); // Update storage
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		if (parent != null) {
			// If parent exists, notify.
			parent.finished();
		}
	}
}
