package ch.sebastianhaeni.remotecontrol;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;

public class KeyboardActivity extends Activity {

	private String _ipAddress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_keyboard);

		Intent intent = getIntent();
		_ipAddress = intent.getStringExtra("SERVER");

		final MyEditText text = (MyEditText) findViewById(R.id.keyboard_input);
		text.setIpAddress(_ipAddress);

		text.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				if (s.toString().isEmpty()) {
					return;
				}
				(new NetworkThread(KeyboardActivity.this, _ipAddress, "keyboard:" + s.toString(), "KEYBOARD_SET")).start();
				text.setText("");
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
		return VolumeActivity.handleMenuItemSelected(this, _ipAddress, featureId, item);
	}

}
