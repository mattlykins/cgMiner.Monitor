package com.mattlykins.cgminer.monitor;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends Activity {

    TextView tvHashRate, tvAccepted, tvRejected, tvAddress;
    Timer t;
    TimerTask tt;
    String ip, port;
    Context context;

    double mHashRate = 0;
    long mAccepted = 0, mRejected = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ip = "192.168.0.201";
        port = "4001";

        context = this;
        // loadPref();

        tvHashRate = (TextView) findViewById(R.id.tvHashRate);
        tvAccepted = (TextView) findViewById(R.id.tvAccepted);
        tvRejected = (TextView) findViewById(R.id.tvRejected);
        tvAddress = (TextView) findViewById(R.id.tvAddress);

        t = new Timer();
        tt = new TimerTask() {

            @Override
            public void run() {
                try {
                    loadPref();
                    runProgram(ip, port);
                    
                    runOnUiThread(new Runnable() {
                        
                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            tvHashRate.setText(String.valueOf(mHashRate));
                            tvAccepted.setText(String.valueOf(mAccepted));
                            tvRejected.setText(String.valueOf(mRejected));
                            tvAddress.setText(ip+":"+port);                            
                        }
                    });
                }
                catch (Exception e) {
                    // TODO Auto-generated catch block
                    Log.d("TEST", "Timer:" + e.toString());
                }
                t.purge();
            }

        };

        t.scheduleAtFixedRate(tt, 0, 10 * 1000);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent();
                intent.setClass(context, SetPreferenceActivity.class);
                startActivityForResult(intent, 0);
                break;
        }
        return true;
    }

    private void loadPref() {
        SharedPreferences mySharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);

        String ipPref = mySharedPreferences.getString("prefAddress", "127.0.0.1");
        String portPref = mySharedPreferences.getString("prefPort", "4001");

        Log.d("TEST", "Loaded Prefs " + ipPref + ":" + portPref);

        ip = ipPref;
        port = portPref;
        
        mHashRate=0;
        mAccepted=0;
        mRejected=0;

        Log.d("TEST", "Loaded Prefs " + ip + ":" + port);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        loadPref();
    }

    int MAXRECEIVESIZE = 65535;

    Socket socket = null;

    private void closeAll() throws Exception {
        if (socket != null) {
            socket.close();
            socket = null;
        }
    }

    public void display(String result) throws Exception {
        String value = "Accessing ...";
        String name = "Accessing ...";
        String[] sections = result.split("\\|", 0);

        if (sections.length == 3 && sections[1].trim().length() > 0) {
            String[] data = sections[1].split(",", 0);

            for (int j = 0; j < data.length; j++) {
                String[] nameval = data[j].split("=", 2);

                if (nameval.length > 1) {
                    name = nameval[0];
                    value = nameval[1];
                }

                Log.d("TEST", name + " " + value);

                if (name.equals("MHS 5s")) {
                    mHashRate = Double.valueOf(value);
                }
                else if (name.equals("Accepted")) {
                    mAccepted = Long.valueOf(value);
                }
                else if (name.equals("Rejected")) {
                    mRejected = Long.valueOf(value);
                }
            }
        }
    }

    public void process(String cmd, InetAddress ip, int port) throws Exception {
        StringBuffer sb = new StringBuffer();
        char buf[] = new char[MAXRECEIVESIZE];
        int len = 0;

        Log.d("TEST", "Attempting to send '" + cmd + "' to " + ip.getHostAddress() + ":" + port);
        
        socket = new Socket();
        InetSocketAddress iSA = new InetSocketAddress(ip, port);

        try {
            socket.connect(iSA,5000);
            PrintStream ps = new PrintStream(socket.getOutputStream());
            ps.print(cmd.toLowerCase().toCharArray());
            ps.flush();

            InputStreamReader isr = new InputStreamReader(socket.getInputStream());
            while (true) {
                len = isr.read(buf, 0, MAXRECEIVESIZE);
                if (len < 1)
                    break;
                sb.append(buf, 0, len);
                if (buf[len - 1] == '\0')
                    break;
            }

            closeAll();
        }
        catch (IOException ioe) {
            Log.d("TEST", "IOException:" + ioe.toString());
            closeAll();
            return;
        }

        String result = sb.toString();

        // Log.d("TEST", "Answer='" + result + "'");

        display(result);
    }

    public void API(String command, String _ip, String _port) throws Exception {
        InetAddress ip = null;
        int port;

        try {
            ip = InetAddress.getByName(_ip);
        }
        catch (UnknownHostException uhe) {
            Log.d("TEST", "Unknown host " + _ip + ": " + uhe);
            return;
        }

        try {
            port = Integer.parseInt(_port);
        }
        catch (NumberFormatException nfe) {
            Log.d("TEST", "Invalid port " + _port + ": " + nfe);
            return;
        }

        Log.d("TEST", "ip = " + ip + " port = " + port);

        process(command, ip, port);
    }

    public void runProgram(String ip, String port) throws Exception {
        String command = "summary";
        API(command, ip, port);

    }
}