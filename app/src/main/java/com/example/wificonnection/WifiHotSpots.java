/* 
 * Copyright (C) 2013-2014 www.Andbrain.com 
 * Faster and more easily to create android apps
 * 
 * */
package com.example.wificonnection;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.widget.ListView;
import android.widget.Toast;


public  class WifiHotSpots {
	WifiManager mWifiManager;
    WifiInfo  mWifiInfo ;
    Context mContext;

	public static boolean isConnectToHotSpotRunning=false;
	 
	public WifiHotSpots(Context c) {
		  mContext=c;
		  mWifiManager=(WifiManager)  mContext.getSystemService(Context.WIFI_SERVICE);
		  mWifiInfo = mWifiManager.getConnectionInfo();	

	}

   /**
     * Check if The Device Is Connected to Hotspot using wifi
     * 
     * @return true if device connected to Hotspot
     */
    public boolean  isConnectedToAP(){
		ConnectivityManager connectivity = (ConnectivityManager)mContext
		        .getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
		    NetworkInfo info = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		    if (info != null) {
		        if (info.isConnected()) {
			            return true;
		        }
		    }
		}
		return false;
	}


    public boolean startHotSpot(boolean enable) {
    	mWifiManager.setWifiEnabled(false);
        Method[] mMethods = mWifiManager.getClass().getDeclaredMethods();
        for (Method mMethod : mMethods) {
            if (mMethod.getName().equals("setWifiApEnabled")) {
                try {
                    mMethod.invoke(mWifiManager, null, enable);
                    return true;
                } catch (Exception ex) {
                }
                break;
            }
        }
        return false;
    }


    public boolean setAndStartHotSpot(boolean enable, String SSID)
    {
    	//For simple implementation I am creating the open hotspot.
    	Method[] mMethods = mWifiManager.getClass().getDeclaredMethods();
    	for(Method mMethod: mMethods){
    		{
    			if(mMethod.getName().equals("setWifiApEnabled")) {
    				WifiConfiguration netConfig = new WifiConfiguration();
    				netConfig.SSID = SSID;
    				netConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
    				try{
    				mMethod.invoke(mWifiManager, netConfig,true);
    				}catch(Exception e)
    				{
    					return false;
    				}
    				startHotSpot(enable);
    			}
    		}
    	}
    	return enable;
    }

   /**
     * Method to Change SSID and Password of Device Access Point 
     * 
     * @param SSID a new SSID of your Access Point
     * @param passWord a new password you want for your Access Point
     */
    public boolean setHotSpot(String SSID,String passWord){
    	/*
    	 * Before setting the HotSpot with specific Id delete the default AP Name.
    	 */
    	/*
    		List<WifiConfiguration> list = mWifiManager.getConfiguredNetworks();
    	 for( WifiConfiguration i : list ) {
    	  if(i.SSID != null && i.SSID.equals(SSID)) {
    	     //wm.disconnect();
    	     //wm.enableNetwork(i.networkId, true);
    	     //wm.reconnect();                
    		  //mWifiManager.disableNetwork(i.networkId);
    		  mWifiManager.removeNetwork(i.networkId);
    		  mWifiManager.saveConfiguration();
    	     break;
    	  }
    	 }
    	*/
    		mWifiManager.setWifiEnabled(false);
     	    Method[] mMethods = mWifiManager.getClass().getDeclaredMethods();

    	  
    	    for(Method mMethod: mMethods){
    	     
    	        if(mMethod.getName().equals("setWifiApEnabled")) {
    	            WifiConfiguration netConfig = new WifiConfiguration();
    	            if(passWord==""){
    	            	netConfig.SSID = SSID;
        	            netConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
        	            netConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        	            netConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
        	            netConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE); 	
    	            }else{
      	            netConfig.SSID = SSID ;
    	            netConfig.preSharedKey = passWord;
    	            netConfig.hiddenSSID = true;
    	            netConfig.status = WifiConfiguration.Status.ENABLED;
    	            netConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
    	            netConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
    	            netConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
    	            netConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
    	            netConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
    	            netConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
    	            netConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
    	            }
    	            try {    	       
    	                mMethod.invoke(mWifiManager, netConfig,true);
    	                mWifiManager.saveConfiguration();
    	                return true;
    	           
    	            } catch (Exception e) {
    	              
    	            }
    	        }
    	    }
    	    return false; 
      }

 	 /**
     * @return true if Wifi Access Point Enabled
     */
    public boolean isWifiApEnabled() {
        try {
            Method method = mWifiManager.getClass().getMethod("isWifiApEnabled");
            return (Boolean)method.invoke(mWifiManager);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

   /**
     * shred all  Configured wifi Networks
     */
    public boolean shredAllWifi(){
    	Context context =  mContext;
    	 mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    	
    	if( mWifiInfo != null ){
    	    for(WifiConfiguration conn:  mWifiManager.getConfiguredNetworks()){
    	    	 mWifiManager.removeNetwork(conn.networkId);
    	    }
    	    
    	    mWifiManager.saveConfiguration(); 
    	    return true;
    	}
    	return false;
	}


    /**
     * get Connection Info
     * @return WifiInfo
     */
    public WifiInfo getConnectionInfo() {
        return mWifiManager.getConnectionInfo();
    }
}
