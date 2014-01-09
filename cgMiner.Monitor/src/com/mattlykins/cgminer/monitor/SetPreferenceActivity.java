package com.mattlykins.cgminer.monitor;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class SetPreferenceActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
     // TODO Auto-generated method stub
     super.onCreate(savedInstanceState);
     
     Log.d("TEST","FERRET!");
     
     getFragmentManager().beginTransaction().replace(android.R.id.content,
                   new PrefsFragment()).commit();    
    }
}



