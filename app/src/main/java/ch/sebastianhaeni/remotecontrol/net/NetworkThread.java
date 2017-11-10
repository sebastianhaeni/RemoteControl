package ch.sebastianhaeni.remotecontrol.net;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import android.content.Context;
import android.content.Intent;

public class NetworkThread extends Thread {

    private static final int SERVER_PORT = 11000;

    private final Context _context;
    private final String _ipAddress;
    private final String _message;
    private final String _broadcastIntentName;

    public NetworkThread(Context context, String ipAddress, String message, String broadcastIntentName) {
        _context = context;
        _ipAddress = ipAddress;
        _message = message;
        _broadcastIntentName = broadcastIntentName;
    }

    @Override
    public void run() {
        Socket socket;
        try {
            socket = new Socket(_ipAddress, SERVER_PORT);
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            out.write(_message + "<EOF>");
            out.flush();

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            StringBuilder total = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                total.append(line);
            }

            String response = total.toString();

            out.close();
            in.close();

            socket.close();

            if (response.equals("OK")) {
                Intent intent = new Intent(_broadcastIntentName);
                intent.putExtra("sender", _ipAddress);
                _context.sendBroadcast(intent);
            }

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
