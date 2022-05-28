package com.example.zarusiot.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.zarusiot.R;
import com.example.zarusiot.component.HttpRequest;

public class ConfigurationDeviceActivity extends AppCompatActivity {

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

        Toast.makeText(getApplicationContext(), "Sending...", Toast.LENGTH_SHORT).show();
        httpRequest.sendDeviceWiFiConfiguration("192.168.4.1",ssid,pass,
                (x)-> true,
                (ip,response)-> {
                    Toast.makeText(getApplicationContext(), ip+" : "+response, Toast.LENGTH_SHORT).show();
            return;
        });
        finish();
    }
}