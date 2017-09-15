package com.wirelesssen;

import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GroupCreation extends AppCompatActivity {
    FloatingActionButton fab;
    WifiManager wMan;
    HashMap<String, Integer> macrssi;
    List<ScanResult> wifiList;
    LinearLayout sv;
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_creation);
        setTitle("Join Group");
        wMan = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        GroupCreation.wifiReceiver wifiReciever = new GroupCreation.wifiReceiver();
        registerReceiver(wifiReciever, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        sv=(LinearLayout) findViewById(R.id.sv);

        macrssi=(HashMap<String, Integer>) getIntent().getExtras().getSerializable("macrssi");
        wifiList=(List<ScanResult>)getIntent().getExtras().getSerializable("wifiList");
        if(macrssi==null||wifiList==null)
            Snackbar.make(findViewById(R.id.R1),"Wifi Scan Import Failed",Snackbar.LENGTH_SHORT).show();
        init();
    }

    private void init() {
        sv.removeAllViews();
        Snackbar.make(findViewById(R.id.R1),"View Refreshed",Snackbar.LENGTH_SHORT).show();
            int size=wifiList.size();
        RadioGroup g1=new RadioGroup(this);
        sv.addView(g1);
        if(g1!=null) {
            for (int i = 0; i < size; i++) {
                RadioButton c1 = new RadioButton(this);
                c1.setText("\n" + wifiList.get(i).SSID + "\n(" + wifiList.get(i).BSSID + ")");
                g1.addView(c1);
            }
        }
        else
            Toast.makeText(this, "G1:NULL", Toast.LENGTH_SHORT).show();
    }
    void invite(View view){
    Snackbar.make(findViewById(R.id.R1),"Invites Sent",Snackbar.LENGTH_SHORT).show();

    }
    void ref(View view){
        wMan.startScan();

    }
    class wifiReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO: This method is called when the BroadcastReceiver is receiving

            listDataHeader = new ArrayList<String>();
            listDataChild = new HashMap<String, List<String>>();
            wifiList = wMan.getScanResults();
            //List<List<String>> tot = new ArrayList<List<String>>();

            for(int i = 0; i < wifiList.size(); i++){
                List<String> info=new ArrayList<String>();
                listDataHeader.add((i+1)+"."+wifiList.get(i).SSID);
                info.add("MAC:"+wifiList.get(i).BSSID);
                info.add("RSSI:"+wifiList.get(i).level);

                macrssi.put(wifiList.get(i).BSSID,wifiList.get(i).level);
                listDataChild.put(listDataHeader.get(i), info);
            }
            init();
        }
    }



}