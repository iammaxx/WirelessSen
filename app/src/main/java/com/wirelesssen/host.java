package com.wirelesssen;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;

public class host extends AppCompatActivity {

    private WifiConfiguration netConfig;
    private WifiManager wifiManager;
    private ProgressDialog p1;
    private ToggleButton t1;
    private TextView SSID;
    private WifiManager wMan;
    private LinearLayout LL;
    HashMap<String,String> dev ;
    private ArrayList<Object> listNote;
    private TextView textResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host);
        listNote = new ArrayList<>();
        dev=new HashMap<String, String>();
        textResult=(TextView)findViewById(R.id.res);
        LL=(LinearLayout)findViewById(R.id.LL1);
        netConfig = new WifiConfiguration();
        wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wMan = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        t1=(ToggleButton)findViewById(R.id.toggleButton);
        SSID=(TextView)findViewById(R.id.SSID);
    }
    void turnon(View view) {
        p1=new ProgressDialog(this);
        p1.setMessage("Please Wait ...\nTurning on Personal Hotspot");
        p1.show();
        if(t1.isChecked()){
            wMan.setWifiEnabled(false);
            netConfig.SSID = SSID.getText().toString();
            netConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            netConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            netConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            netConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);

            try {
                Method setWifiApMethod = wifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
                boolean apstatus = (Boolean) setWifiApMethod.invoke(wifiManager, netConfig, true);

                Method isWifiApEnabledmethod = wifiManager.getClass().getMethod("isWifiApEnabled");
                while (!(Boolean) isWifiApEnabledmethod.invoke(wifiManager)) {
                }
                ;
                Method getWifiApStateMethod = wifiManager.getClass().getMethod("getWifiApState");
                int apstate = (Integer) getWifiApStateMethod.invoke(wifiManager);
                Method getWifiApConfigurationMethod = wifiManager.getClass().getMethod("getWifiApConfiguration");
                netConfig = (WifiConfiguration) getWifiApConfigurationMethod.invoke(wifiManager);
                Log.e("CLIENT", "\nSSID:" + netConfig.SSID + "\nPassword:" + netConfig.preSharedKey + "\n");
                p1.dismiss();
            } catch (Exception e) {
                Log.e(this.getClass().toString(), "", e);
            }

        }
        else
        {
            wMan.setWifiEnabled(true);
        }
    }

    public void dis(View view) {
        readAddresses();
        if(listNote.size()==0)
            textResult.setText("No Devices Found");
        textResult.setText("");
        for(int i=0; i<listNote.size(); i++){
            textResult.append(i+1. + "   ");
            textResult.append(listNote.get(i).toString());
            textResult.append("\n");
        }
    }
    private void readAddresses() {
        listNote.clear();
        BufferedReader bufferedReader = null;

        try {
            bufferedReader = new BufferedReader(new FileReader("/proc/net/arp"));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] splitted = line.split(" +");
                if (splitted != null && splitted.length >= 4) {
                    String ip = splitted[0];
                    String mac = splitted[3];
                    if (mac.matches("..:..:..:..:..:..")) {
                        Node thisNode = new Node(ip, mac);
                        listNote.add(thisNode);
                    }
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            try {
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class Node {
        String ip;
        String mac;

        Node(String ip, String mac){
            this.ip = ip;
            this.mac = mac;
        }

        @Override
        public String toString() {
            return "IP:"+ip + "  MAC:" + mac;
        }
    }
}

