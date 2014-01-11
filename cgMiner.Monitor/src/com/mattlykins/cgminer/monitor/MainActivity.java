package com.mattlykins.cgminer.monitor;

import java.util.Timer;
import java.util.TimerTask;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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
    static final int mInterval = 10*1000;
    static final public String TAG = "MAIN";
    private Handler mHandler;
    TextView tvHashRate, tvAccepted, tvRejected, tvAddress;
    //Timer t;
    //TimerTask tt;
    String ip, port;
    Context context;
    cgMiner cgM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        
        
        mHandler = new Handler();

        ip = "192.168.0.83";
        port = "4001";

        context = this;
        // loadPref();

        tvHashRate = (TextView) findViewById(R.id.tvHashRate);
        tvAccepted = (TextView) findViewById(R.id.tvAccepted);
        tvRejected = (TextView) findViewById(R.id.tvRejected);
        tvAddress = (TextView) findViewById(R.id.tvAddress);
        
        callAsynchronousTask();


    }
    
    public void callAsynchronousTask() {
        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {       
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {       
                        try {
                            cgMinerAsync performBackgroundTask = new cgMinerAsync();
                            performBackgroundTask.execute();
                        } catch (Exception e) {
                            Log.e(TAG,Log.getStackTraceString(e));
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, mInterval);
    }
    
    class cgMinerAsync extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub
            try {
                cgM = new cgMiner(ip, port);
            }
            catch (Exception e) {
                // TODO Auto-generated catch block
                Log.e(TAG,Log.getStackTraceString(e));
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            tvHashRate.setText(String.valueOf(cgM.HashRate));
            tvAccepted.setText(String.valueOf(cgM.Accepted));
            tvRejected.setText(String.valueOf(cgM.Rejected));
            tvAddress.setText(ip+":"+port);
        }
        
    }
    
    
//    Runnable mRunner = new Runnable() {
//        @Override
//        public void run() {
//            try {
//                cgMiner cgM = new cgMiner(ip, port);
//                Log.d("TEST", cgM.HashRate + " " + cgM.Accepted + " " + cgM.Rejected);
//                tvHashRate.setText(String.valueOf(cgM.HashRate));
//                tvAccepted.setText(String.valueOf(cgM.Accepted));
//                tvRejected.setText(String.valueOf(cgM.Rejected));
//                tvAddress.setText(ip+":"+port); 
//                mHandler.postDelayed(mRunner, mInterval);
//            }
//            catch (Exception e) {
//                // TODO Auto-generated catch block
//                Log.e(TAG, Log.getStackTraceString(e));
//            }
//        }
//    };    
//    
//    void startRunning(){
//        mRunner.run();
//    }
//    
//    void stopRunning(){
//        mHandler.removeCallbacks(mRunner);
//    }  


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