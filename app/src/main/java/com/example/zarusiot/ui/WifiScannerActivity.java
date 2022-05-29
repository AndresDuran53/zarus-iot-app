package com.example.zarusiot.ui;

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
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkSpecifier;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.zarusiot.R;
import com.example.zarusiot.data.model.WiFiNetwork;

import java.util.ArrayList;
import java.util.List;

public class WifiScannerActivity extends AppCompatActivity {

    private static final String LOG_TAG = "AndroidExample";
    private static final int MY_REQUEST_CODE = 123;
    private WifiManager wifiManager;
    private WifiBroadcastReceiver wifiReceiver;
    private Button buttonScan;
    private TextView messageTexView;
    private ListView listWifi;
    private ConnectivityManager connectivityManager;
    private ConnectivityManager.NetworkCallback networkCallback;
    private List<WiFiNetwork> wiFiNetworks = new ArrayList<>();
    private ActivityResultLauncher<Intent> configActivityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_scanner);

        this.wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        // Instantiate broadcast receiver
        this.wifiReceiver = new WifiBroadcastReceiver();
        // Register the receiver
        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        listWifi = findViewById(R.id.listViewWifi);
        buttonScan = findViewById(R.id.buttonWiFiScan);
        messageTexView = findViewById(R.id.textViewWiFiScanner);
        messageTexView.setText("Press the button to find you new device.");
        buttonScan.setEnabled(true);
        this.buttonScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messageTexView.setText("Searching Device Networks...");
                buttonScan.setEnabled(false);
                requestsWifiPermission();
            }
        });

        configActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        Toast.makeText(getApplicationContext(), "Device configured", Toast.LENGTH_LONG).show();
                        ((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE))
                                .bindProcessToNetwork(null);
                        try{
                            final ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                            connectivityManager.unregisterNetworkCallback(new ConnectivityManager.NetworkCallback() {
                                @Override
                                public void onAvailable(@NonNull Network network) {
                                    super.onAvailable(network);
                                }
                            });
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                        finish();
                    }
                });
    }


    private void requestsWifiPermission() {
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

        buttonScan.setEnabled(true);
        messageTexView.setText("");
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
            networkRequestBuilder.removeCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
            networkRequestBuilder.setNetworkSpecifier(wifiNetworkSpecifier);
            NetworkRequest networkRequest = networkRequestBuilder.build();
            connectivityManager = (ConnectivityManager) getBaseContext().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager != null) {
                connectivityManager.requestNetwork(networkRequest, new ConnectivityManager.NetworkCallback() {
                    @Override
                    public void onAvailable(@NonNull Network network) {
                        super.onAvailable(network);
                        ((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE))
                                .bindProcessToNetwork(network);
                        Intent intent = new Intent(getApplicationContext(), ConfigurationDeviceActivity.class);
                        configActivityResultLauncher.launch(intent);
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