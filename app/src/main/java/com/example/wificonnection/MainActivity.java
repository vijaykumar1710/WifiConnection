package com.example.wificonnection;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity implements View.OnClickListener , WiFiConnecter.ActionListener {

    private TextView mtextView;
    private String ssid;
    private String password;
    private WifiManager wifiManager;
    private EditText mssid,mpassowrd;
    private Button mconnect,mcreate;
    private WiFiConnecter mWiFiConnecter;
    private ProgressBar mProgressBar;
    private WifiConfiguration wifiConfiguration;
    private WifiHotSpots wifiHotSpots;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mtextView = findViewById(R.id.wifiDetails);
        mssid = findViewById(R.id.ssid);
        mpassowrd = findViewById(R.id.password);
        mconnect = findViewById(R.id.connect);
        mcreate = findViewById(R.id.create);
        mconnect.setOnClickListener(this);
        mcreate.setOnClickListener(this);
        mProgressBar = findViewById(R.id.progressbar);
        mProgressBar.setVisibility(View.GONE);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        wifiConfiguration = new WifiConfiguration();


        requestPermissions();
        checkIFAlreadyConnected();


    }

    private void checkIFAlreadyConnected() {
        if(!wifiManager.isWifiEnabled()){
            wifiManager.setWifiEnabled(true);
        }else{
            setCurrentSsid();
        }
    }

    private void requestPermissions() {

        if (getApplicationContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)){
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }else{
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults){
        switch (requestCode){
            case 1: {
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if (getApplicationContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch(id){
            case R.id.connect :
                if(!mssid.getText().toString().isEmpty() && !mpassowrd.getText().toString().isEmpty()){
                    ssid = mssid.getText().toString();
                    password = mpassowrd.getText().toString();
                    mWiFiConnecter = new WiFiConnecter(this);
                    mWiFiConnecter.connect(ssid,password,this);
                }else{
                    showError();
                }
                break;

            case R.id.create:
                if(!mssid.getText().toString().isEmpty() && !mpassowrd.getText().toString().isEmpty()){
                        wifiHotSpots = new WifiHotSpots(getApplicationContext());
                       // wifiHotSpots.setHotSpot(mssid.getText().toString(),mpassowrd.getText().toString());
                        wifiHotSpots.setHotSpot("vijay","1234567890");
                        System.out.println(wifiHotSpots.setHotSpot("vijay","1234567890"));
                        wifiHotSpots.startHotSpot(true);
                        System.out.println(wifiHotSpots.startHotSpot(true));

                }else{
                    showError();
                }




                break;


        }
    }

    private void changeStateWifiAp(boolean activated) {
        Method method;
        try {
            method = wifiManager.getClass().getDeclaredMethod("setWifiApEnabled", WifiConfiguration.class, Boolean.TYPE);
            method.invoke(wifiManager, wifiConfiguration, activated);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void setCurrentSsid() {
        WifiInfo info = wifiManager.getConnectionInfo();
        String s = (info == null) ? "null" : info.getSSID();
        mtextView.setText(String.format(s));
    }

    private void showError() {
        mssid.setError("Please fill valid ssid");
        mpassowrd.setError("Please fill valid passowrd");
    }


    /**
     * The operation started
     *
     * @param ssid
     */
    @Override
    public void onStarted(String ssid) {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    /**
     * The operation succeeded
     *
     * @param info
     */
    @Override
    public void onSuccess(WifiInfo info) {
        mProgressBar.setVisibility(View.GONE);
        setCurrentSsid();
    }

    /**
     * The operation failed
     */
    @Override
    public void onFailure() {
        mProgressBar.setVisibility(View.GONE);
        Toast.makeText(this,R.string.failed,Toast.LENGTH_LONG).show();
    }

    /**
     * The operation finished
     *
     * @param isSuccessed
     */
    @Override
    public void onFinished(boolean isSuccessed) {
        Toast.makeText(this,R.string.success,Toast.LENGTH_LONG).show();
    }
}
