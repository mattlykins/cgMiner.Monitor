package com.mattlykins.cgminer.monitor;

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
    
    static final public String TAG = "MAIN";
    
    TextView tvHashRate, tvAccepted, tvRejected, tvAddress;
    //Timer t;
    //TimerTask tt;
    String ip, port;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ip = "192.168.0.83";
        port = "4001";

        context = this;
        // loadPref();

        tvHashRate = (TextView) findViewById(R.id.tvHashRate);
        tvAccepted = (TextView) findViewById(R.id.tvAccepted);
        tvRejected = (TextView) findViewById(R.id.tvRejected);
        tvAddress = (TextView) findViewById(R.id.tvAddress);
        
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    cgMiner cgM = new cgMiner(ip, port);
                    Log.d("TEST", cgM.HashRate + " " + cgM.Accepted + " " + cgM.Rejected);
                    tvHashRate.setText(String.valueOf(cgM.HashRate));
                    tvAccepted.setText(String.valueOf(cgM.Accepted));
                    tvRejected.setText(String.valueOf(cgM.Rejected));
                    tvAddress.setText(ip+":"+port);   
                }
                catch (Exception e) {
                    // TODO Auto-generated catch block
                    Log.e(TAG, Log.getStackTraceString(e));
                }
            }
        };
        new Thread(runnable).start();
        
        

//        t = new Timer();
//        tt = new TimerTask() {
//
//            @Override
//            public void run() {
//                try {
//                    //loadPref();
//                    //runProgram(ip, port);
//                    
//                    runOnUiThread(new Runnable() {
//                        
//                        @Override
//                        public void run() {
//                            // TODO Auto-generated method stub
//                            tvHashRate.setText(String.valueOf(mHashRate));
//                            tvAccepted.setText(String.valueOf(mAccepted));
//                            tvRejected.setText(String.valueOf(mRejected));
//                            tvAddress.setText(ip+":"+port);                            
//                        }
//                    });
//                }
//                catch (Exception e) {
//                    // TODO Auto-generated catch block
//                    Log.d("TEST", "Timer:" + e.toString());
//                }
//                t.purge();
//            }
//
//        };
//
//        t.scheduleAtFixedRate(tt, 0, 10 * 1000);

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

        Log.d("TEST", "Loaded Prefs " + ip + ":" + port);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //loadPref();
    }

}