package com.example.zarusiot.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.zarusiot.R;
import com.example.zarusiot.data.model.WiFiNetwork;

import java.util.List;

public class ListViewWiFiItemAdapter extends BaseAdapter {
    private Context context;
    private List<WiFiNetwork> listItems;

    public ListViewWiFiItemAdapter(Context context, List<WiFiNetwork> listItems) {
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
        WiFiNetwork wiFiNetwork = (WiFiNetwork) getItem(i);
        view = LayoutInflater.from(context).inflate(R.layout.custom_listview_item,null);
        TextView title = (TextView) view.findViewById(R.id.iotTitle);
        TextView subTitle = (TextView) view.findViewById(R.id.iotSubTitle);

        title.setText(wiFiNetwork.getTypeDevice()+" | "+wiFiNetwork.getNameDevice());
        subTitle.setText(wiFiNetwork.getBssid());

        return view;
    }
}
