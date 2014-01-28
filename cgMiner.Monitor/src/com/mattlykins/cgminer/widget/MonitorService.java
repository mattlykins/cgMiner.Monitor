package com.mattlykins.cgminer.widget;

import com.mattlykins.cgminer.monitor.R;
import com.mattlykins.cgminer.monitor.R.id;
import com.mattlykins.cgminer.monitor.R.layout;
import com.mattlykins.cgminer.utility.cgMiner;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

public class MonitorService extends Service {
    static final String TAG = "MONITORSERVICE";
    String mIp, mPort;
    cgMiner cgM;

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        mIp = "192.168.0.83";
        mPort = "4001";
        // try {
        // cgM = new cgMiner(mIp, mPort);
        // cgM.runMonitor();
        // }
        // catch (Exception e) {
        // // TODO Auto-generated catch block
        // Log.e(TAG,Log.getStackTraceString(e));
        // }

        RemoteViews view = new RemoteViews(getPackageName(), R.layout.widget);

        view.setTextViewText(R.id.tvWAddress, mIp + ":" + mPort);
        // view.setTextViewText(R.id.tvWAccepted, cgM.Accepted.toString());
        // view.setTextViewText(R.id.tvWRejected, cgM.Rejected.toString());

        // Push update for this widget to the home screen
        ComponentName thisWidget = new ComponentName(this, MinerAppWidgetProvider.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(this);
        manager.updateAppWidget(thisWidget, view);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

}
