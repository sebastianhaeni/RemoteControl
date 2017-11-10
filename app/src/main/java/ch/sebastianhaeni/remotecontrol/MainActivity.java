package ch.sebastianhaeni.remotecontrol;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import ch.sebastianhaeni.remotecontrol.control.VolumeActivity;
import ch.sebastianhaeni.remotecontrol.net.NetworkThread;
import ch.sebastianhaeni.remotecontrol.net.UDPListenerService;

public class MainActivity extends AppCompatActivity {
    private static final String AUTHENTICATION_SUCCESS = "AUTHENTICATION_SUCCESS";

    private ReceiveMessages _receiver = null;
    private Boolean _receiverIsRegistered = false;
    private final ArrayList<ServerItem> _listItems = new ArrayList<>();
    private ArrayAdapter<ServerItem> _adapter;

    class ReceiveMessages extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String sender = intent.getStringExtra("sender");

            if (intent.getAction() == null) {
                return;
            }

            if (intent.getAction().equals(UDPListenerService.UDP_BROADCAST)) {
                String hostname = intent.getStringExtra("message");
                ServerItem s = new ServerItem(hostname, sender);
                if (!_listItems.contains(s)) {
                    _listItems.add(s);
                    _adapter.notifyDataSetChanged();
                }
            } else if (intent.getAction().equals(AUTHENTICATION_SUCCESS)) {
                Intent mainActivity = new Intent(MainActivity.this, VolumeActivity.class);
                mainActivity.putExtra("SERVER", sender);
                startActivity(mainActivity);
            }
        }
    }

    class ServerItem {

        private final String _hostname;
        private final String _ipAddress;

        ServerItem(String hostname, String ipAddress) {
            _hostname = hostname;
            _ipAddress = ipAddress;
        }

        public String toString() {
            return _hostname;
        }

        String getIpAddress() {
            return _ipAddress;
        }

        String getHostname() {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startService(new Intent(this, UDPListenerService.class));
        _receiver = new ReceiveMessages();

        ListView listView = findViewById(R.id.list);

        final MainActivity that = this;

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("SERVER_LIST", "clicked on item " + id);
                ServerItem s = _listItems.get((int) id);
                Thread thread = new NetworkThread(that, s.getIpAddress(), "password:1234", AUTHENTICATION_SUCCESS);
                thread.start();
            }
        });

        _adapter = new ArrayAdapter<>(this, R.layout.server_list_item, _listItems);
        listView.setAdapter(_adapter);
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

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//         Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.server_selection, menu);
//        return true;
//}
}
