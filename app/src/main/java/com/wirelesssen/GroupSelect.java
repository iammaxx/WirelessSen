package com.wirelesssen;

import android.net.wifi.ScanResult;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ScrollView;

import java.util.HashMap;
import java.util.List;

public class GroupSelect extends AppCompatActivity {
    FloatingActionButton fab;
    HashMap<String, Integer> macrssi;
    List<ScanResult> wifiList;
    ScrollView sv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_select);
    }

}