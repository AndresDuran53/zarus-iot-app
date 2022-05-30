package com.example.zarusiot.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.zarusiot.R;
import com.example.zarusiot.data.model.IotDevice;

import java.util.List;

public class DiscoveredListViewItemAdapter extends BaseAdapter {
    private Context context;
    private List<IotDevice> listItems;

    public DiscoveredListViewItemAdapter(Context context, List<IotDevice> listItems) {
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
        view = LayoutInflater.from(context).inflate(R.layout.listview_discovered_iot_item,null);
        TextView title = (TextView) view.findViewById(R.id.discoveredIotTitle);
        TextView subTitle = (TextView) view.findViewById(R.id.discoveredIotSubTitle);
        TextView addText = (TextView) view.findViewById(R.id.discoveredIotAdd);

        title.setText(iotDevice.getType() + ": " +iotDevice.getId());
        subTitle.setText(iotDevice.getType()+" | "+iotDevice.getIp());
        if(iotDevice.isAdded()){
            addText.setText(R.string.already_added);
            addText.setTextColor(ContextCompat.getColor(context, R.color.zarus_text_muted));
        }
        else addText.setText(R.string.add_device);

        return view;
    }
}
