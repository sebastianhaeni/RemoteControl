package ch.sebastianhaeni.remotecontrol;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class VolumeActivity extends Activity {

	private String _ipAddress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_volume);

		Intent intent = getIntent();
		_ipAddress = intent.getStringExtra("SERVER");

		SeekBar volumeControler = (SeekBar) findViewById(R.id.volume);
		volumeControler.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				(new NetworkThread(VolumeActivity.this, _ipAddress, "volume:" + progress, "VOLUME_SET")).start();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		return handleMenuItemSelected(this, _ipAddress, featureId, item);
	}

	public static boolean handleMenuItemSelected(Context context, String ipAddress, int featureId, MenuItem item) {
		Intent intent = null;
		switch (item.getItemId()) {
		case R.id.action_mouse:
			intent = new Intent(context, MouseActivity.class);
			break;
		case R.id.action_volume:
			intent = new Intent(context, VolumeActivity.class);
			break;
		case R.id.action_keyboard:
			intent = new Intent(context, KeyboardActivity.class);
			break;
		}
		if (intent == null) {
			return false;
		}
		intent.putExtra("SERVER", ipAddress);
		context.startActivity(intent);
		return true;
	}

}
