package ch.sebastianhaeni.remotecontrol;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.ArrayList;

import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ServerSelectionActivity extends ListActivity {

	public static final String AUTHENTICATION_SUCCESS = "AUTHENTICATION_SUCCESS";

	ReceiveMessages _receiver = null;
	Boolean _receiverIsRegistered = false;
	ArrayList<ServerItem> _listItems = new ArrayList<ServerItem>();
	ArrayAdapter<ServerItem> _adapter;

	class ReceiveMessages extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String sender = intent.getStringExtra("sender");
			if (intent.getAction().equals(UDPListenerService.UDP_BROADCAST)) {
				String hostname = intent.getStringExtra("message");
				ServerItem s = new ServerItem(hostname, sender);
				if (!_listItems.contains(s)) {
					_listItems.add(s);
					_adapter.notifyDataSetChanged();
				}
			} else if (intent.getAction().equals(AUTHENTICATION_SUCCESS)) {
				Intent mainActivity = new Intent(ServerSelectionActivity.this, VolumeActivity.class);
				mainActivity.putExtra("SERVER", sender);
				startActivity(mainActivity);
			}
		}
	}

	class ServerItem {

		private String _hostname;
		private String _ipAddress;

		public ServerItem(String hostname, String ipAddress) {
			_hostname = hostname;
			_ipAddress = ipAddress;
		}

		public String toString() {
			return _hostname;
		}

		public String getIpAddress() {
			return _ipAddress;
		}

		public String getHostname() {
			return _hostname;
		}

		public boolean equals(Object other) {
			if (other instanceof ServerItem) {
				ServerItem o = (ServerItem) other;
				return o.getHostname().equals(_hostname) && o.getIpAddress().equals(_ipAddress);
			}
			return other.equals(this);
		}

	}

	PrintWriter out;
	BufferedReader in;

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Log.i("SERVER_LIST", "clicked on item " + id);
		ServerItem s = _listItems.get((int) id);
		Thread thread = new NetworkThread(this, s.getIpAddress(), "password:1234", AUTHENTICATION_SUCCESS);
		thread.start();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_server_selection);

		startService(new Intent(this, UDPListenerService.class));
		_receiver = new ReceiveMessages();

		_adapter = new ArrayAdapter<ServerItem>(this, R.layout.server_list_item, _listItems);
		setListAdapter(_adapter);
	}

	@Override
	protected void onResume() {
		if (!_receiverIsRegistered) {
			registerReceiver(_receiver, new IntentFilter(UDPListenerService.UDP_BROADCAST));
			registerReceiver(_receiver, new IntentFilter(AUTHENTICATION_SUCCESS));
			_receiverIsRegistered = true;
		}
		super.onResume();
	}

	@Override
	protected void onPause() {
		if (_receiverIsRegistered) {
			unregisterReceiver(_receiver);
			_receiverIsRegistered = false;
		}
		super.onPause();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.server_selection, menu);
		return true;
	}

}
