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

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class SetPreferenceActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
     // TODO Auto-generated method stub
     super.onCreate(savedInstanceState);
     
     getFragmentManager().beginTransaction().replace(android.R.id.content,
                   new PrefsFragment()).commit();    
    }
}



