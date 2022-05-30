package com.example.zarusiot.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zarusiot.R;
import com.example.zarusiot.data.IoTDeviceType;
import com.example.zarusiot.data.model.IotDevice;

public class DeviceInformation extends AppCompatActivity {

    private final String IOT_DEVICE_INFORMATION = "IOT_DEVICE_INFORMATION";
    private final String ACTION_NAME = "ACTION_NAME";
    private final String EDIT_ACTION_NAME = "EDIT_ACTION_NAME";
    private final String DELETE_ACTION_NAME = "DELETE_ACTION_NAME";
    private final String DEVICE_ACTION_NAME = "DEVICE_ACTION_NAME";

    TextView tvDeviceId;
    TextView tvDeviceType;
    TextView tvDeviceName;
    TextView tvDeviceStatus;
    TextView tvDeviceIp;
    TextView tvDeviceMac;
    Button btDeviceControls;
    Button btDeviceEdit;
    Button btDeviceDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_information);

        setUIBindings();

        Intent intent = getIntent();
        IotDevice iotDevice = (IotDevice) intent.getSerializableExtra(IOT_DEVICE_INFORMATION);
        setUIValuesByDevice(iotDevice);
        setOnClickValuesByDevice(iotDevice);
    }

    private void setUIBindings() {
        tvDeviceId = (TextView)findViewById(R.id.textViewDeviceID);
        tvDeviceType = (TextView)findViewById(R.id.textViewDeviceType);
        tvDeviceName = (TextView)findViewById(R.id.textViewDeviceName);
        tvDeviceStatus = (TextView)findViewById(R.id.textViewDeviceStatus);
        tvDeviceIp = (TextView)findViewById(R.id.textViewDeviceIP);
        tvDeviceMac = (TextView)findViewById(R.id.textViewDeviceMAC);
        btDeviceControls = (Button)findViewById(R.id.buttonDeviceControls);
        btDeviceEdit = (Button)findViewById(R.id.buttonDeviceEdit);
        btDeviceDelete = (Button)findViewById(R.id.buttonDeviceDelete);
    }

    private void setUIValuesByDevice(IotDevice iotDevice) {
        tvDeviceId.setText(iotDevice.getId());
        tvDeviceType.setText(IoTDeviceType.findByValue(iotDevice.getType()).getName());
        tvDeviceName.setText(iotDevice.getName());
        tvDeviceIp.setText(iotDevice.getIp());
        tvDeviceMac.setText(iotDevice.getMac());
        if(iotDevice.isLastStatusConnected()) tvDeviceStatus.setText("Online");
        else tvDeviceStatus.setText("Offline");
    }

    private void setOnClickValuesByDevice(IotDevice iotDevice){
        btDeviceControls.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openWebPage(iotDevice.getIp());
                    }
                }
        );
        btDeviceEdit.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getApplication(), "Edit Button not enable yet", Toast.LENGTH_SHORT).show();
                        /*getIntent().putExtra(ACTION_NAME,EDIT_ACTION_NAME);
                        getIntent().putExtra(DEVICE_ACTION_NAME,"NewName");
                        setResult(RESULT_OK,getIntent());
                        finish();*/
                    }
                }
        );
        btDeviceDelete.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Toast.makeText(getApplication(), "Delete Button not enable yet", Toast.LENGTH_SHORT).show();
                        getIntent().putExtra(ACTION_NAME,DELETE_ACTION_NAME);
                        setResult(RESULT_OK,getIntent());
                        finish();
                    }
                }
        );
    }

    private void openWebPage(String deviceIp){
        Uri uri = Uri.parse("http://" + deviceIp);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }
}