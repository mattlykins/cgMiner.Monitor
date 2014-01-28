package com.mattlykins.cgminer.widget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class MinerBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "MINER_BROADCAST_RECEIVER";

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub

        Bundle b = intent.getExtras();
        String mIp = b.getString("IP");
        String mPort = b.getString("PORT");
        
        Log.d(TAG,mIp + ":" + mPort);

        Intent cgMIntent = new Intent(context, MinerIntentService.class);
        cgMIntent.putExtra(MinerIntentService.KEY_IP, mIp);
        cgMIntent.putExtra(MinerIntentService.KEY_PORT, mPort);
        context.startService(cgMIntent);

    }

}
