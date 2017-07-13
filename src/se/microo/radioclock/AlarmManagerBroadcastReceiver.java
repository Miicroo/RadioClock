package se.microo.radioclock;

import se.microo.radioclock.model.data.Alarm;
import se.microo.radioclock.util.Storage;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmManagerBroadcastReceiver extends BroadcastReceiver {
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Storage s = new Storage(context);
		if(!s.alarmIsSet()) {
			return;
		}
		
		Alarm alarm = s.getAlarm();
		
		final String channelKey = context.getString(R.string.STREAMING_CHANNEL);
		final String linkKey = context.getString(R.string.STREAMING_LINK);
		final String volumeKey = context.getString(R.string.STREAMING_VOLUME);
		
		Intent activateIntent = new Intent(context, AlarmFiredActivity.class);
		activateIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		activateIntent.putExtra(channelKey, alarm.getChannelName());
		activateIntent.putExtra(linkKey, alarm.getChannelStream());
		activateIntent.putExtra(volumeKey, alarm.getVolume());
		
		context.startActivity(activateIntent);
	}

	public void setAlarm(Context context, Alarm alarm) {
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

		Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
		
		am.set(AlarmManager.RTC_WAKEUP, alarm.getFireTime(), pi);
	}

	public boolean removeAlarm(Context context, Alarm alarm) {
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

		Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
		PendingIntent pendingUpdateIntent = PendingIntent.getService(context, 0, intent, 0);

		// Cancel alarms
		try {
			am.cancel(pendingUpdateIntent);
			pendingUpdateIntent.cancel();
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
