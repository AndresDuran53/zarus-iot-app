package com.example.zarusiot.component;

import static android.content.Context.WIFI_SERVICE;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class HttpRequest {
    private Context context;
    private int requestPending;

    public HttpRequest(Context context){
        this.context = context;
    }

    public void callGetRequests(String ip,
                                Function<String,Boolean> validateResponse,
                                BiConsumer<String,String> callback){
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);
        String url ="http://"+ip+"/getRedInformation";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(validateResponse.apply(response)) callback.accept(ip,response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public void callGetListRequests(List<String> listIps,
                                    Function<String,Boolean> validateResponse,
                                    BiConsumer<String,String> callback){
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);
        listIps.stream().forEach(ip->{
            String url ="http://"+ip+"/getRedInformation";
            // Request a string response from the provided URL.
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if(validateResponse.apply(response)) callback.accept(ip,response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    //callback.accept(ip,"");
                }
            });
            // Add the request to the RequestQueue.
            queue.add(stringRequest);
        });
    }

    // Deprecated of formatIpAddress() because the function doesn't support ipv6
    // but neither does WifiInfo.
    @SuppressWarnings("deprecation")
    public String getOwnIp(){
        WifiManager wifiMgr = (WifiManager) context.getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        int ip = wifiInfo.getIpAddress();
        String ownIp = Formatter.formatIpAddress(ip);
        return ownIp;
    }

}
