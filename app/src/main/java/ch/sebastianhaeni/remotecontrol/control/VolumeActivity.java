package ch.sebastianhaeni.remotecontrol.control;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import ch.sebastianhaeni.remotecontrol.R;
import ch.sebastianhaeni.remotecontrol.net.NetworkThread;

public class VolumeActivity extends AppCompatActivity {

    private String _ipAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volume);

        Intent intent = getIntent();
        _ipAddress = intent.getStringExtra("SERVER");

        SeekBar volumeController = findViewById(R.id.volume);
        volumeController.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

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
    public boolean onOptionsItemSelected(MenuItem item) {
        handleMenuItemSelected(this, _ipAddress, item);

        return super.onOptionsItemSelected(item);
    }

    public static void handleMenuItemSelected(Context context, String ipAddress, MenuItem item) {
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
            return;
        }

        intent.putExtra("SERVER", ipAddress);
        context.startActivity(intent);
    }
}
