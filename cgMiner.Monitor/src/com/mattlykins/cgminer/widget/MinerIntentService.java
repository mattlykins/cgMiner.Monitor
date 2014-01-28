package com.mattlykins.cgminer.widget;

import com.mattlykins.cgminer.utility.cgMiner;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class MinerIntentService extends IntentService {

    public static final String KEY_IP = "IP";
    public static final String KEY_PORT = "PORT";
    public static final String KEY_ACCEPTED = "ACCEPTED";
    public static final String KEY_REJECTED = "REJECTED";
    private static final String TAG = "MINER_INTENT_SERVICE";

    cgMiner cgM;

    public MinerIntentService() {
        super("MinerIntentService");
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // TODO Auto-generated method stub
        
        Bundle b = intent.getExtras();
        String mIp = b.getString("IP");
        String mPort = b.getString("PORT");
        Log.d(TAG,mIp + ":" + mPort);
        
        try {
            cgM = new cgMiner(mIp, mPort);
            cgM.runMonitor();
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e(TAG,Log.getStackTraceString(e));
        }
        
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(ResponseReceiver.ACTION_RESP);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastIntent.putExtra(KEY_IP, mIp);
        broadcastIntent.putExtra(KEY_PORT, mPort);
        broadcastIntent.putExtra(KEY_ACCEPTED, cgM.Accepted);
        broadcastIntent.putExtra(KEY_REJECTED, cgM.Rejected);
        sendBroadcast(broadcastIntent);
        
    }
}
