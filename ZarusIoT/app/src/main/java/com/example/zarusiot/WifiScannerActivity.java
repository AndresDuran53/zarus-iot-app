package com.example.zarusiot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkSpecifier;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zarusiot.data.model.WiFiNetwork;
import com.example.zarusiot.ui.ListViewItemAdapter;
import com.example.zarusiot.ui.ListViewWiFiItemAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class WifiScannerActivity extends AppCompatActivity {

    private static final String LOG_TAG = "AndroidExample";
    private static final int MY_REQUEST_CODE = 123;
    private WifiManager wifiManager;
    private List<ScanResult> results;
    private WifiBroadcastReceiver wifiReceiver;
    private boolean searching;
    private Button buttonScan;
    private ListView listWifi;
    private List<WiFiNetwork> wiFiNetworks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_scanner);

        this.wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        // Instantiate broadcast receiver
        this.wifiReceiver = new WifiBroadcastReceiver();
        // Register the receiver
        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        searching = false;
        listWifi = findViewById(R.id.listViewWifi);
        buttonScan = findViewById(R.id.buttonWiFiScan);
        this.buttonScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searching = true;
                requestsWifiPermission();
            }
        });
    }


    private void requestsWifiPermission() {
        // With Android Level >= 23, you have to ask the user
        // for permission to Call.
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) { // 23
            int permission1 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

            // Check for permissions
            if (permission1 != PackageManager.PERMISSION_GRANTED) {

                Log.d(LOG_TAG, "Requesting Permissions");

                // Request permissions
                ActivityCompat.requestPermissions(this,
                        new String[]{
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_WIFI_STATE,
                                Manifest.permission.ACCESS_NETWORK_STATE
                        }, MY_REQUEST_CODE);
                return;
            }
            Log.d(LOG_TAG, "Permissions Already Granted");
        }
        this.doStartScanWifi();
    }

    private void doStartScanWifi() {
        this.wifiManager.startScan();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(LOG_TAG, "onRequestPermissionsResult");

        switch (requestCode) {
            case MY_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    Log.d(LOG_TAG, "Permission Granted: " + permissions[0]);

                    // Start Scan Wifi.
                    this.doStartScanWifi();
                } else {
                    // Permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.d(LOG_TAG, "Permission Denied: " + permissions[0]);
                }
                break;
            }
            // Other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    @Override
    protected void onStop() {
        try{
            unregisterReceiver(this.wifiReceiver);
        }catch (Exception e){
            e.printStackTrace();
        }
        super.onStop();
    }

    // Define class to listen to broadcasts
    class WifiBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            searching = false;
            Log.d(LOG_TAG, "onReceive()");

            Toast.makeText(context, "Scan Complete!", Toast.LENGTH_SHORT).show();

            boolean ok = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false);

            if (ok) {
                Log.d(LOG_TAG, "Scan OK");

                List<ScanResult> list = wifiManager.getScanResults();

                WifiScannerActivity.this.showNetworks(list);
                //WifiScannerActivity.this.showNetworksDetails(list);
            } else {
                Log.d(LOG_TAG, "Scan not OK");
            }
            unregisterReceiver(wifiReceiver);
        }
    }

    private void showNetworks(List<ScanResult> results) {

        for (final ScanResult result : results) {
            wiFiNetworks.add(new WiFiNetwork(result.SSID,result.BSSID,result.capabilities));
        }

        ListViewWiFiItemAdapter listViewItemAdapter = new ListViewWiFiItemAdapter(this,wiFiNetworks);
        listWifi.setAdapter(listViewItemAdapter);
        listWifi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                connectToNetwork(wiFiNetworks.get(position).getCapabilities(),
                                wiFiNetworks.get(position).getSsid());
            }
        });
    }

    private void connectToNetwork(String networkCapabilities, String networkSSID) {
        Toast.makeText(this, "Connecting to network: " + networkSSID, Toast.LENGTH_SHORT).show();

        String networkPass = "password123";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            WifiNetworkSpecifier.Builder builder = new WifiNetworkSpecifier.Builder();
            builder.setSsid(networkSSID);
            builder.setWpa2Passphrase(networkPass);

            WifiNetworkSpecifier wifiNetworkSpecifier = builder.build();
            NetworkRequest.Builder networkRequestBuilder = new NetworkRequest.Builder();
            networkRequestBuilder.addTransportType(NetworkCapabilities.TRANSPORT_WIFI);
            networkRequestBuilder.addCapability(NetworkCapabilities.NET_CAPABILITY_NOT_RESTRICTED);
            networkRequestBuilder.addCapability(NetworkCapabilities.NET_CAPABILITY_TRUSTED);
            networkRequestBuilder.setNetworkSpecifier(wifiNetworkSpecifier);
            NetworkRequest networkRequest = networkRequestBuilder.build();
            ConnectivityManager connectivityManager = (ConnectivityManager) getBaseContext().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager != null) {
                connectivityManager.requestNetwork(networkRequest, new ConnectivityManager.NetworkCallback() {
                    @Override
                    public void onAvailable(@NonNull Network network) {
                        super.onAvailable(network);
                        Uri uri = Uri.parse("http://192.168.4.1");
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }
                });
            }
        }else{
            WifiConfiguration wifiConfig = new WifiConfiguration();
            wifiConfig.SSID = "\"" + networkSSID + "\"";

            if (networkCapabilities.toUpperCase().contains("WEP")) { // WEP Network.
                //Toast.makeText(this, "WEP Network", Toast.LENGTH_SHORT).show();
                wifiConfig.wepKeys[0] = "\"" + networkPass + "\"";
                wifiConfig.wepTxKeyIndex = 0;
                wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            } else if (networkCapabilities.toUpperCase().contains("WPA")) { // WPA Network
                //Toast.makeText(this, "WPA Network", Toast.LENGTH_SHORT).show();
                wifiConfig.preSharedKey = "\"" + networkPass + "\"";
            } else { // OPEN Network.
                //Toast.makeText(this, "OPEN Network", Toast.LENGTH_SHORT).show();
                wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            }

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            this.wifiManager.addNetwork(wifiConfig);

            List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
            for( WifiConfiguration config : list ) {
                if(config.SSID != null && config.SSID.equals("\"" + networkSSID + "\"")) {
                    this.wifiManager.disconnect();
                    this. wifiManager.enableNetwork(config.networkId, true);
                    this.wifiManager.reconnect();
                    break;
                }
            }
        }
    }
}