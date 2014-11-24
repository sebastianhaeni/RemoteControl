package ch.sebastianhaeni.remotecontrol;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MotionEventCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.RelativeLayout;

public class MouseActivity extends Activity {

	private float mLastTouchX;
	private float mLastTouchY;
	private String _ipAddress;
	private long _lastUpdate = 0;
	private long _downTime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mouse);

		Intent intent = getIntent();
		_ipAddress = intent.getStringExtra("SERVER");

		Button btnLeft = (Button) findViewById(R.id.left_click);
		Button btnRight = (Button) findViewById(R.id.right_click);

		btnLeft.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					(new NetworkThread(MouseActivity.this, _ipAddress, "mouse_left:down", "CLICK_DONE")).start();
					break;
				case MotionEvent.ACTION_UP:
					(new NetworkThread(MouseActivity.this, _ipAddress, "mouse_left:up", "CLICK_DONE")).start();
					break;
				}
				return true;
			}
		});
		btnRight.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					(new NetworkThread(MouseActivity.this, _ipAddress, "mouse_right:down", "CLICK_DONE")).start();
					break;
				case MotionEvent.ACTION_UP:
					(new NetworkThread(MouseActivity.this, _ipAddress, "mouse_right:up", "CLICK_DONE")).start();
					break;
				}
				return true;
			}
		});

		RelativeLayout trackpad = (RelativeLayout) findViewById(R.id.trackpad);
		trackpad.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getActionMasked()) {
				case MotionEvent.ACTION_DOWN: {
					final int pointerIndex = MotionEventCompat.getActionIndex(event);
					final float x = MotionEventCompat.getX(event, pointerIndex);
					final float y = MotionEventCompat.getY(event, pointerIndex);
					mLastTouchX = x;
					mLastTouchY = y;
					_downTime = System.currentTimeMillis();
					break;
				}
				case MotionEvent.ACTION_UP: {
					final int pointerIndex = MotionEventCompat.getActionIndex(event);
					final float x = MotionEventCompat.getX(event, pointerIndex);
					final float y = MotionEventCompat.getY(event, pointerIndex);

					// Calculate the distance moved
					final float dx = x - mLastTouchX;
					final float dy = y - mLastTouchY;
					if (System.currentTimeMillis() - _downTime <= 200 && Math.abs(dx) < 1 && Math.abs(dy) < 1) {
						(new NetworkThread(MouseActivity.this, _ipAddress, "mouse_left:down", "CLICK_DONE")).start();
						(new NetworkThread(MouseActivity.this, _ipAddress, "mouse_left:up", "CLICK_DONE")).start();
					}
					break;
				}
				case MotionEvent.ACTION_MOVE: {
					final int pointerIndex = MotionEventCompat.getActionIndex(event);
					final float x = MotionEventCompat.getX(event, pointerIndex);
					final float y = MotionEventCompat.getY(event, pointerIndex);

					// Calculate the distance moved
					final float dx = x - mLastTouchX;
					final float dy = y - mLastTouchY;

					if (System.currentTimeMillis() - _lastUpdate > 20) {

						if (Math.abs(dx) >= 1 && Math.abs(dy) >= 1) {
							(new NetworkThread(MouseActivity.this, _ipAddress, "mouse:" + (int) dx + "," + (int) dy, "MOUSE_SET")).start();
						}

						// Remember this touch position for the next move event
						mLastTouchX = x;
						mLastTouchY = y;
						_lastUpdate = System.currentTimeMillis();
					}
					break;
				}
				}
				return true;
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
