/*
Copyright (c) 2014 Matt Lykins

This file is part of Remote Monitor for CGMiner.

Remote Monitor for CGMiner is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Remote Monitor for CGMiner is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Remote Monitor for CGMiner.  If not, see <http://www.gnu.org/licenses/>.
*/
package com.mattlykins.cgminer.monitor;

import java.net.ConnectException;
import java.util.Timer;
import java.util.TimerTask;

import com.mattlykins.cgminer.utility.cgMiner;


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
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    static final int mInterval = 10*1000;
    static final public String TAG = "MAIN";
    TextView tvHashRate, tvAccepted, tvRejected, tvAddress;
    Button bQuit;
    String ip, port;
    Context context;
    cgMiner cgM;
    Timer timer;
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
        bQuit = (Button) findViewById(R.id.bQuit);
        
        bQuit.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                timer.cancel();
                finish();                
            }
        });
        
        //loadPref();
        
        //callAsynchronousTask();


    }
    
    
    public void callAsynchronousTask() {
        final Handler handler = new Handler();
        timer = new Timer();
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
                cgM.runMonitor();
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
            case R.id.action_about:
                Intent aboutintent = new Intent(this, AboutActivity.class);
                startActivityForResult(aboutintent, 0);
                break;
        }
        return true;
    }

    private void loadPref() {
        SharedPreferences mySharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);

        String ipPref = mySharedPreferences.getString("prefAddress", "127.0.0.1");
        String portPref = mySharedPreferences.getString("prefPort", "4001");

        //Log.d("TEST", "Loaded Prefs " + ipPref + ":" + portPref);

        ip = ipPref;
        port = portPref;

        //Log.d("TEST", "Loaded Prefs " + ip + ":" + port);
        
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