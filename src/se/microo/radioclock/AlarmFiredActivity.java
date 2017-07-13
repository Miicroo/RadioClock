package se.microo.radioclock;

import java.io.IOException;

import se.microo.radioclock.model.MediaData;
import se.microo.radioclock.model.Sdcard;
import se.microo.radioclock.util.Storage;
import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * MainActivity that loads system settings and then redirects to
 * {@link AlarmActivity}.
 * 
 * @author Micro
 */
public class AlarmFiredActivity extends Activity implements OnClickListener {

	private MediaPlayer mediaPlayer;
	private AudioManager audioManager;
	private PowerManager.WakeLock wl;
	private String streamLink, streamChannel;
	private long streamVolume;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alarmfired);

		Context context = getApplicationContext();
		PowerManager pm = (PowerManager) context
				.getSystemService(Context.POWER_SERVICE);
		this.wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
				context.getString(R.string.app_name));

		// Acquire the lock
		wl.acquire();

		TextView output = (TextView) (findViewById(R.id.channelData));

		try {
			Bundle extras = getIntent().getExtras();

			if (extras != null) {

				mediaPlayer = new MediaPlayer();
				audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
				
				streamLink = extras.getString(context.getString(R.string.STREAMING_LINK), "");
				streamChannel = extras.getString(context.getString(R.string.STREAMING_CHANNEL), "");
				streamVolume = extras.getLong(context.getString(R.string.STREAMING_VOLUME), audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));

				output.setText(output.getText() + " " + streamChannel);

				Button stop = (Button) (findViewById(R.id.stopButton));
				stop.setOnClickListener(this);
				
				setVolume(streamVolume);
				
				mediaPlayer.setDataSource(streamLink);
				mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
				mediaPlayer.prepare();
				mediaPlayer.start();
			}
		} catch (Exception e) {
			output.setText("ERROR: " + e.getMessage());
			Log.e("Error playing url", Log.getStackTraceString(e));
			
			try {
				playBackup(output);
			} catch(Exception e1) {
				output.setText("ERROR: " + e1.getMessage());
				Log.e("Error playing backup", Log.getStackTraceString(e1));
			}
		} finally {
			// Release the lock
			wl.release();
		}
	}

	private void playBackup(TextView output) throws IllegalArgumentException, SecurityException, IllegalStateException, IOException {
		mediaPlayer = new MediaPlayer();
		audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		
		Sdcard sd = new Sdcard(getApplicationContext());
		MediaData data = sd.scanSdcard().get(0);
		
		output.setText("Now playing: " + data.getDisplayName());

		Button stop = (Button) (findViewById(R.id.stopButton));
		stop.setOnClickListener(this);
		
		setVolume(streamVolume);
		
		mediaPlayer.setDataSource(data.getPath());
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		mediaPlayer.prepare();
		mediaPlayer.start();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)) {
			if(streamVolume > 0) {
				streamVolume--;
				setVolume(streamVolume);
			}
		} else if ((keyCode == KeyEvent.KEYCODE_VOLUME_UP)) {
			if(streamVolume < audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)) {
				streamVolume++;
				setVolume(streamVolume);
			}
		}
		return true;
	}

	public void setVolume(long streamVolume2) {
		//streamVolume2 *= 2;
		if(streamVolume2 > audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)) {
			streamVolume2 = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		}
		if(streamVolume2 < 0) {
			streamVolume2 = 0;
		}
		
		audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,	(int) streamVolume2, 0);
	}

	@Override
	public void onClick(View v) {
		Storage storage = new Storage(getApplicationContext());
		storage.removeAlarm();

		AlarmManagerBroadcastReceiver alarmManager = new AlarmManagerBroadcastReceiver();
		alarmManager.removeAlarm(getApplicationContext(), storage.getAlarm());

		if (mediaPlayer != null) {
			mediaPlayer.stop();
			mediaPlayer.release();
		}

		if (wl.isHeld()) {
			wl.release();
		}
		finish();
	}
}
