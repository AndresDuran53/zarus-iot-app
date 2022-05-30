package com.example.zarusiot.ui;

import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.zarusiot.R;
import com.example.zarusiot.component.HttpRequest;

public class ConfigurationDeviceActivity extends AppCompatActivity {

    private final String IP_LOCAL = "192.168.4.1";
    private HttpRequest httpRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration_device);
        httpRequest = new HttpRequest(getApplicationContext());
    }

    public void sendWiFiConfiguration(View view) {
        EditText editSsid = (EditText) findViewById(R.id.edit_text_ssid);
        EditText editPass = (EditText) findViewById(R.id.edit_text_password);
        String ssid = editSsid.getText().toString();
        String pass = editPass.getText().toString();
        if(ssid.isEmpty() || pass.isEmpty()) return;
        String message = getString(R.string.sending_configuration);
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        httpRequest.sendDeviceWiFiConfiguration(new Pair<>(IP_LOCAL, ""),ssid,pass,
                (x)-> true,
                (ip,response)-> {return;});
        finish();
    }
}