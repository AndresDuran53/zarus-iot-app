package com.example.zarusiot.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.zarusiot.R;
import com.example.zarusiot.data.IoTDeviceType;
import com.example.zarusiot.data.model.IotDevice;

import java.util.List;

public class ListViewItemAdapter extends BaseAdapter {
    private Context context;
    private List<IotDevice> listItems;

    public ListViewItemAdapter(Context context, List<IotDevice> listItems) {
        this.context = context;
        this.listItems = listItems;
    }

    @Override
    public int getCount() {
        return listItems.size();
    }

    @Override
    public Object getItem(int i) {
        return listItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        IotDevice iotDevice = (IotDevice) getItem(i);
        view = LayoutInflater.from(context).inflate(R.layout.custom_listview_item,null);
        TextView title = (TextView) view.findViewById(R.id.iotTitle);
        TextView subTitle = (TextView) view.findViewById(R.id.iotSubTitle);

        IoTDeviceType ioTDeviceType = IoTDeviceType.findByValue(iotDevice.getType());
        String type = ioTDeviceType.getName();

        title.setText(iotDevice.getName());
        subTitle.setText(type+" | "+iotDevice.getIp());

        return view;
    }
}
