package com.mattlykins.cgminer.widget;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.mattlykins.cgminer.monitor.R;

public class ResponseReceiver extends BroadcastReceiver{
    public static final String ACTION_RESP = "com.mattlykins.intent.action.MESSAGE_PROCESSED";

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        
        String mAccepted = intent.getStringExtra(MinerIntentService.KEY_ACCEPTED);
        String mRejected = intent.getStringExtra(MinerIntentService.KEY_REJECTED);
        String mIp = intent.getStringExtra(MinerIntentService.KEY_IP);
        String mPort = intent.getStringExtra(MinerIntentService.KEY_PORT);
        
        
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                R.layout.widget);
        // Set the text with the current time.
        ComponentName cN = new ComponentName(context,MinerAppWidgetProvider.class);
        remoteViews.setTextViewText(R.id.tvWAddress, mIp + ":" + mPort);
        remoteViews.setTextViewText(R.id.tvWAccepted, "A:" + mAccepted);
        remoteViews.setTextViewText(R.id.tvWRejected, "R:" + mRejected);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        appWidgetManager.updateAppWidget(cN, remoteViews);
        
    }
    
}
