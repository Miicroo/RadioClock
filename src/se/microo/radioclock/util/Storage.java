package se.microo.radioclock.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import se.microo.radioclock.R;
import se.microo.radioclock.model.data.Alarm;
import se.microo.radioclock.model.data.Channel;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class Storage {


	private static final Alarm defaultAlarm = new Alarm(-1, -1, "", "");
	private static final long TIMEOUT = 1000*60*60*24; // 24 hours
	
	private Context c;
	private SharedPreferences preferences;

	private ArrayList<Channel> savedList;
	private Alarm alarm;
	private long latestUpdate;

	/**
	 * Creates a new Storage based on given Context c.
	 * @param c Context to access storage from.
	 */
	public Storage(Context c) {
		if (c == null) {
			throw new IllegalArgumentException("Cannot access storage: null");
		}

		this.c = c;

		preferences = PreferenceManager.getDefaultSharedPreferences(c);
		
		savedList = new ArrayList<Channel>();
		latestUpdate = 0;

		initChannelList();
		initLatestUpdate();
		initAlarm();
	}

	/**
	 * Returns all the saved channels in the Storage.
	 * @return
	 */
	public ArrayList<Channel> getList() {
		return new ArrayList<Channel>(savedList);
	}

	/**
	 * Returns the current Alarm. Use alarmIsSet to check if current alarm
	 * is actually set.
	 * 
	 * @return The current alarm.
	 */
	public Alarm getAlarm() {
		return alarm;
	}
	
	public boolean isOutdated() {
		return System.currentTimeMillis() > latestUpdate+TIMEOUT;
	}

	/**
	 * Updates channel list with new data.
	 * 
	 * @param newData
	 */
	public void updateStorage(ArrayList<Channel> newData) {
		boolean isDifferent = isDifferent(newData, savedList);

		Editor edit = preferences.edit();
		
		// Don't save a new list which is equal to the old one :P
		if (isDifferent && !newData.isEmpty()) {
			savedList = new ArrayList<Channel>(newData);
			Collections.sort(savedList); // Save sorted

			Set<String> data = new HashSet<String>();

			for (Channel c : newData) {
				data.add(c.getStorageFormat());
			}

			edit.putStringSet(c.getString(R.string.STORAGE), data);
		}
		latestUpdate = System.currentTimeMillis();
		edit.putLong(c.getString(R.string.STORAGE_UPDATE), latestUpdate);
		edit.commit();
	}

	/**
	 * Checks if an alarm is set.
	 * 
	 * @return true if alarm is set, otherwise false.
	 */
	public boolean alarmIsSet() {
		return !alarm.equals(defaultAlarm);
	}

	/**
	 * Sets current alarm to newAlarm.
	 * @param newAlarm New alarm to set.
	 */
	public void setAlarm(Alarm newAlarm) {
		alarm = new Alarm(newAlarm);

		// Write to storage
		Editor edit = preferences.edit();
		edit.putLong(c.getString(R.string.ALARMTIME), alarm.getFireTime());
		edit.putLong(c.getString(R.string.ALARMVOLUME), alarm.getVolume());
		edit.putString(c.getString(R.string.ALARMCHANNEL), alarm.getChannelName());
		edit.putString(c.getString(R.string.ALARMLINK), alarm.getChannelStream());
		edit.commit();
	}

	/**
	 * Removes current alarm.
	 */
	public void removeAlarm() {
		setAlarm(defaultAlarm);
	}

	// Initiates current alarm
	private void initAlarm() {
		long fireTime = preferences.getLong(c.getString(R.string.ALARMTIME), defaultAlarm.getFireTime());
		long volume = preferences.getLong(c.getString(R.string.ALARMVOLUME), defaultAlarm.getVolume());

		String channel = preferences.getString(c.getString(R.string.ALARMCHANNEL),	defaultAlarm.getChannelName());
		String stream = preferences.getString(c.getString(R.string.ALARMLINK), defaultAlarm.getChannelStream());

		alarm = new Alarm(fireTime, volume, channel, stream);
	}

	// Reads stored channels into savedList
	private void initChannelList() {
		Set<String> prevData = new HashSet<String>();
		prevData = preferences.getStringSet(c.getString(R.string.STORAGE), prevData);

		for (String s : prevData) {
			savedList.add(Channel.createChannel(s));
		}

		Collections.sort(savedList);
	}
	
	// Reads latest update of storage
	private void initLatestUpdate() {
		latestUpdate = preferences.getLong(c.getString(R.string.STORAGE_UPDATE), latestUpdate);
	}

	// Compares if two ArrayLists are different
	private boolean isDifferent(ArrayList<Channel> in1, ArrayList<Channel> in2) {
		ArrayList<Channel> tmp1 = new ArrayList<Channel>(in1);
		ArrayList<Channel> tmp2 = new ArrayList<Channel>(in2);

		ArrayList<Channel> tmp3 = new ArrayList<Channel>(tmp1);
		tmp1.removeAll(tmp2); // tmp1 now contains only unique values or nothing.
		tmp2.removeAll(tmp3); // and so does tmp2.

		tmp1.addAll(tmp2);

		return !tmp1.isEmpty();
	}
}
