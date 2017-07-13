package se.microo.radioclock;

import se.microo.radioclock.model.SystemUpdater;
import se.microo.radioclock.model.data.Postback;
import se.microo.radioclock.util.Storage;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


/**
 * MainActivity that loads system settings and then
 * redirects to {@link AlarmActivity}.
 * 
 * @author Micro
 */
public class MainActivity extends Activity implements Postback {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		Storage s = new Storage(getApplicationContext());
		if(s.isOutdated()) {
			// Update storage in the background.
			// When update is ready, finished() will be called.
			SystemUpdater updater = new SystemUpdater(this);
			updater.execute(getApplicationContext());
		} else {
			// Update already finished.
			finished();
		}
	}

	@Override
	public void finished() {		
		// Go to next activity
		startActivity(new Intent(MainActivity.this, AlarmActivity.class));
		finish();
	}
}
