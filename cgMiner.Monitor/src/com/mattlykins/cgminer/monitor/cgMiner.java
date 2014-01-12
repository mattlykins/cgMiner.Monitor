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


import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import android.util.Log;

public class cgMiner {
    public Double HashRate = 0.0;
    public Long Accepted = (long) 0;
    public Long Rejected = (long) 0;
    static final int MAXRECEIVESIZE = 65535;
    static final String SUMMARY_CMD = "summary";
    Socket mSocket = null;

    public cgMiner(String _ip, String _port) throws Exception {
        mSocket = makeConnection(_ip, _port);
        if (mSocket != null) {
            String tempString = receiveData();
            closeSocket();
            processString(tempString);
        }
        else{
            //Log.d("TEST","Could not connect to server");
            
        }
    }
    
    private void closeSocket() throws Exception{
        mSocket.close();
        mSocket=null;
    }

    private Socket makeConnection(String _ip, String _port) throws Exception {
        InetAddress mIp = null;
        int mPort = 0;

        mIp = InetAddress.getByName(_ip);
        mPort = Integer.parseInt(_port);
        
        //Log.d("TEST", "ip = " + mIp + " port = " + mPort);

        // Log.d("TEST", "Attempting to send '" + cmd + "' to " +
        // ip.getHostAddress() + ":" + port);

        mSocket = new Socket();
        InetSocketAddress iSA = new InetSocketAddress(mIp, mPort);

        mSocket.connect(iSA, 5000);

        return mSocket;
    }

    private String receiveData() throws Exception {
        String mResult = "";
        StringBuffer mStringBuffer = new StringBuffer();
        char mBuffer[] = new char[MAXRECEIVESIZE];
        int mBufferLength = 0;

        PrintStream mPrintStream = null;
        
        mPrintStream = new PrintStream(mSocket.getOutputStream());
        
        mPrintStream.print(SUMMARY_CMD.toLowerCase().toCharArray());
        mPrintStream.flush();

        InputStreamReader mInputReader = null;
        
        mInputReader = new InputStreamReader(mSocket.getInputStream());
        
        while (true) {            
            mBufferLength = mInputReader.read(mBuffer, 0, MAXRECEIVESIZE);
            if (mBufferLength < 1) {
                break;
            }
            mStringBuffer.append(mBuffer, 0, mBufferLength);
            if (mBuffer[mBufferLength - 1] == '\0') {
                break;
            }
        }

        mResult = mStringBuffer.toString();

        //Log.d("TEST", "Answer='" + mResult + "'");

        return mResult;
    }
    
    private void processString(String _string){
        String mValue = null;
        String mName = null;
        String[] mSections = _string.split("\\|", 0);
        
        if (mSections.length == 3 && mSections[1].trim().length() > 0) {
            String[] mData = mSections[1].split(",", 0);
            
            for (int j = 0; j < mData.length; j++) {
                String[] mNameValue = mData[j].split("=", 2);
                if (mNameValue.length > 1) {
                    mName = mNameValue[0];
                    mValue = mNameValue[1];
                    
                    //Log.d("TEST", mName + " " + mValue);                    
                    
                    
                    if (mName.equals("MHS 5s")) {
                        HashRate = Double.valueOf(mValue);
                    }
                    else if (mName.equals("Accepted")) {
                        Accepted = Long.valueOf(mValue);
                    }
                    else if (mName.equals("Rejected")) {
                       Rejected = Long.valueOf(mValue);
                    }
                }
            }
        }
    }
}
