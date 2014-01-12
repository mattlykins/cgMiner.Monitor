package com.mattlykins.cgminer.monitor;

import java.net.ConnectException;
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
import android.widget.Toast;

public class MainActivity extends Activity {
    static final int mInterval = 10*1000;
    static final public String TAG = "MAIN";
    TextView tvHashRate, tvAccepted, tvRejected, tvAddress;
    String ip, port;
    Context context;
    cgMiner cgM;
    boolean timerEnabled = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ip = "192.168.0.83";
        port = "4001";

        context = this;        

        tvHashRate = (TextView) findViewById(R.id.tvHashRate);
        tvAccepted = (TextView) findViewById(R.id.tvAccepted);
        tvRejected = (TextView) findViewById(R.id.tvRejected);
        tvAddress = (TextView) findViewById(R.id.tvAddress);
        
        loadPref();
        
        callAsynchronousTask();


    }
    
    public void callAsynchronousTask() {
        final Handler handler = new Handler();
        final Timer timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {       
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {       
                        try {
                            cgMinerAsync performBackgroundTask = new cgMinerAsync();
                            if( timerEnabled ){
                                performBackgroundTask.execute();
                            }
                            else{
                                timer.cancel();
                            }
                            
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
        
        private int err = 0;

        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub
            try {
                cgM = new cgMiner(ip, port);
            }
            catch (ConnectException CE) {
                // TODO Auto-generated catch block
                err = -1;                
            }
            catch (Exception e) {
                // TODO Auto-generated catch block
                Log.e(TAG,Log.getStackTraceString(e));
                err = -2;
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            loadPref();
        }

        @Override
        protected void onPostExecute(Void result){
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if( err == 0){
                timerEnabled = true;
                tvHashRate.setText(String.valueOf(cgM.HashRate));
                tvAccepted.setText(String.valueOf(cgM.Accepted));
                tvRejected.setText(String.valueOf(cgM.Rejected));
                tvAddress.setText(ip+":"+port);                
            }
            else{
                Toast.makeText(context, "Cannot connect to " + ip + ":" + port,
                        Toast.LENGTH_SHORT).show();
                timerEnabled = false;
                resetTextViews();
            }
        }
        
    }
    
    public void resetTextViews(){
        tvHashRate.setText("---");
        tvAccepted.setText("---");
        tvRejected.setText("---");
        tvAddress.setText(ip+":"+port);
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
        
        resetTextViews();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        loadPref();
        if(!timerEnabled){
            timerEnabled = true;
            callAsynchronousTask();
        }
    }

}