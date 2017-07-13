package se.microo.radioclock;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import se.microo.radioclock.model.data.Alarm;
import se.microo.radioclock.model.data.Channel;
import se.microo.radioclock.util.Storage;
import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class AlarmActivity extends Activity implements OnClickListener {

	private Storage storage;
	private AlarmManagerBroadcastReceiver alarmManager;
	private long fireTime = -1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setalarm);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		storage = new Storage(getApplicationContext());
		alarmManager = new AlarmManagerBroadcastReceiver();
		
		if(storage.alarmIsSet()) {
			loadExistingAlarm();
		} else {
			loadNonExistingAlarm();
		}
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.removeAlarm:
				removeAlarm();
				break;
			case R.id.next:
				if(setAlarmTime()) {
					loadRadio();
				}
				break;
			case R.id.setAlarm:
				setAlarm();
				break;
		}
	}
	
	@Override
	public void onBackPressed() {
	    if(fireTime > -1 && !storage.alarmIsSet()) {
	    	// We are in setradio
	    	loadNonExistingAlarm();
	    } else {
	    	super.onBackPressed();
	    }
	}
	
	private void removeAlarm() {
		alarmManager.removeAlarm(getApplicationContext(), storage.getAlarm());
		storage.removeAlarm();
		
		loadNonExistingAlarm();
	}
	
	private boolean setAlarmTime() {
		DatePicker datePicker = (DatePicker)(findViewById(R.id.datePicker));
		TimePicker timePicker = (TimePicker)(findViewById(R.id.timePicker));
		
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
		cal.set(Calendar.MONTH, datePicker.getMonth());
		cal.set(Calendar.YEAR, datePicker.getYear());
		cal.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
		cal.set(Calendar.MINUTE, timePicker.getCurrentMinute());
		cal.set(Calendar.SECOND, 0);
		
		long fire = cal.getTimeInMillis();
		
		if(fire < System.currentTimeMillis()) {
			Toast.makeText(getApplicationContext(), "Please choose a date and time that is in the future!" , Toast.LENGTH_LONG).show();
			return false;
		}
		
		fireTime = fire;
		
		return true;
	}
	
	private void setAlarm() {
		Spinner channels = (Spinner)(findViewById(R.id.channels));
		
		long index = channels.getSelectedItemId();
		String channel = storage.getList().get((int) index).getName();
		String streamLink = storage.getList().get((int) index).getMp3();
		

		SeekBar volume = (SeekBar)(findViewById(R.id.volume));
		long v = volume.getProgress();
		
		Alarm alarm = new Alarm(fireTime, v, channel, streamLink);
		
		storage.setAlarm(alarm);
		alarmManager.setAlarm(getApplicationContext(), alarm);
		
		loadExistingAlarm();
	}
	
	private void setSpinnerChannels() {
		Spinner ch = (Spinner)(findViewById(R.id.channels));
		List<String> list = new ArrayList<String>();
		
		ArrayList<Channel> channels = storage.getList();
		for(Channel c : channels) {
			list.add(c.getName());
		}

		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		ch.setAdapter(dataAdapter);
	}
	
	private void loadNonExistingAlarm() {
		setContentView(R.layout.setalarm);
		(findViewById(R.id.next)).setOnClickListener(this);
		fireTime = -1;
	}

	private void loadExistingAlarm() {
		setContentView(R.layout.alarmexists);
		(findViewById(R.id.removeAlarm)).setOnClickListener(this);

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.UK);
		long fireTime = storage.getAlarm().getFireTime();
		
		TextView alarmTime = (TextView)(findViewById(R.id.alarmTime));
		alarmTime.setText(alarmTime.getText()+" "+formatter.format(new Date(fireTime)));
	}
	
	private void loadRadio() {
		setContentView(R.layout.setradio);
		(findViewById(R.id.setAlarm)).setOnClickListener(this);
		
		SeekBar volume = (SeekBar)(findViewById(R.id.volume));
		AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		int max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		volume.setMax(max);
		volume.setProgress(max);
		
		setSpinnerChannels();
	}
}
