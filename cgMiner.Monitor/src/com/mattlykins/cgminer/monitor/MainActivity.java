package com.mattlykins.cgminer.monitor;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

public class MainActivity extends Activity {

    TextView tvHashRate, tvAccepted, tvRejected;
    Timer t;
    TimerTask tt;
    String ip,port;
    
    double mHashRate = 0;
    long mAccepted = 0, mRejected = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvHashRate = (TextView) findViewById(R.id.tvHashRate);
        tvAccepted = (TextView) findViewById(R.id.tvAccepted);
        tvRejected = (TextView) findViewById(R.id.tvRejected);
        
        ip = "192.168.0.83";
        port = "4001";

        t = new Timer();
        tt = new TimerTask() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                new checkCgminer().execute();
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

    class checkCgminer extends AsyncTask {
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
                    
                    publishProgress(null);
                }
            }
        }

        public void process(String cmd, InetAddress ip, int port) throws Exception {
            StringBuffer sb = new StringBuffer();
            char buf[] = new char[MAXRECEIVESIZE];
            int len = 0;

            Log.d("TEST", "Attempting to send '" + cmd + "' to " + ip.getHostAddress() + ":" + port);

            try {
                socket = new Socket(ip, port);
                Log.d("TEST", socket.getOutputStream().toString());
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
                Log.d("TEST", ioe.toString());
                closeAll();
                return;
            }
            catch (Exception e) {
                Log.d("TEST", "EXCEPTION: " + e);
            }

            String result = sb.toString();

            // Log.d("TEST", "Answer='" + result + "'");

            display(result);
        }

        public void API(String command, String _ip, String _port) throws Exception {
            InetAddress ip;
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

        public void runProgram(String ip,String port) throws Exception {
            String command = "summary";
            API(command, ip, port);
        }

        @Override
        protected Object doInBackground(Object... params) {
            // TODO Auto-generated method stub
            try {
                runProgram(ip,port);
            }
            catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Object... values) {
            // TODO Auto-generated method stub            
            tvHashRate.setText(String.valueOf(mHashRate));
            tvAccepted.setText(String.valueOf(mAccepted));
            tvRejected.setText(String.valueOf(mRejected));            
        }
        
        
        
        
    }

}
