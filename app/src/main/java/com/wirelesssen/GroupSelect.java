package com.wirelesssen;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.ScanResult;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.util.HashMap;
import java.util.List;

public class GroupSelect extends AppCompatActivity implements SensorEventListener,StepListener{
    SensorManager mSensorManager;
    SimpleStepDetector simpleStepDetector;
    int numSteps;
    TextView tx,txt1,st;
    boolean type;
    double x,y;
    private TextView mypath;
    private TextView path;
    HashMap<String, String> macip;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_select);
            macip = (HashMap<String, String>) getIntent().getExtras().getSerializable("macip");
            if (macip==null)
                Toast.makeText(this, "MACIP NULL", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this,macip.keySet().iterator().next(), Toast.LENGTH_SHORT).show();

        x=0;
        y=0;
        tx=(TextView)findViewById(R.id.tx1);
        txt1=(TextView)findViewById(R.id.txt1);
        st=(TextView)findViewById(R.id.status);
        type=getIntent().getBooleanExtra("SEN",false);
        numSteps=0;
        simpleStepDetector=new SimpleStepDetector();
        simpleStepDetector.registerListener(this);
        mypath=(TextView)findViewById(R.id.mypath);
        path=(TextView)findViewById(R.id.path);
        ts = System.currentTimeMillis();
        gy=0.0;
        gy1=0.0;
    }
    getrssi receive;
    Thread t;
    void start(View view)
    {
        st.setText("**Recording**");
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                SensorManager.SENSOR_DELAY_GAME);
        if(type)//TODO                           HOST
        {
                        myhandler hand = new myhandler(this);
                        receive=new getrssi(4555,hand,0);
                        receive.start();
                        t = new Thread() {
                            @Override
                            public void run() {
                                try {
                                    while(true){
                                        for(int i=0;i<macip.values().size();i++) {
                                            UdpClientThread send = new UdpClientThread((String.valueOf(x) + "_" + String.valueOf(y)).getBytes(),macip.keySet().iterator().next(), 4455);
                                            send.start();
                                        }
                                        Thread.sleep(2000);
                                    }
                                } catch (Exception e) {
                                }
                            }
                        };
                        t.start();
        }
        else//TODO                         NODE
        {
                myhandler hand = new myhandler(this);
                receive = new getrssi(4455, hand, 0);
                receive.start();

                t = new Thread() {
                @Override
                public void run() {
                    try {
                        while(true){
          UdpClientThread send = new UdpClientThread((String.valueOf(x)+"_"+String.valueOf(y)).getBytes(),"192.168.43.1",4555);
                        send.start();
                        Thread.sleep(2000);
                        }
                    } catch (Exception e) {
                    }
                }
            };
            t.start();
        }

    }
    void stop(View view)
    {
        st.setText("**Paused**");
        mSensorManager.unregisterListener(this);
        t.stop();

        receive.socket.close();
    }
    void reset(View view)
    {
        deg=0.0;
        gy=0.0;
        numSteps=0;
        tx.setText("Rotation :0 Degrees");
        txt1.setText("Steps0");
    }
    @Override
    protected void onResume() {
        super.onResume();
        // for the system's orientation sensor registered listeners
        //    mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
        //          SensorManager.SENSOR_DELAY_GAME);
    }
    void add(View view) throws IOException {
        numSteps++;
        txt1.setText("Steps"+numSteps);
        File dir = new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/IMU");
        dir.mkdirs();
        File file = new File(dir, "IMUDATA.txt");

        FileOutputStream fileOutputStream = new FileOutputStream(file, true);
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
        outputStreamWriter.write(0.69+" " + deg+"\n");
        outputStreamWriter.close();
        fileOutputStream.close();

    }
    @Override
    protected void onPause()
    {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }
    long ts ;
    double time;
    Double deg=0.0,gy,gy1;
    @Override
    public void onSensorChanged(SensorEvent event) {
        try {
            simpleStepDetector.updateAccel(
                    event.timestamp, event.values[0], event.values[1], event.values[2]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //double vel = (double) (event.values[2]) * 180 / Math.PI;
        double vel = (double) (event.values[1]) * 180 / Math.PI;
        Log.d("Gyro", Float.toString(event.values[0]));
        time = System.currentTimeMillis() - ts;
        ts = System.currentTimeMillis();
        time /= 1000;
        deg += time * (vel + gy) / 2;
        if (deg > 360)
            deg -= 360;
        if (deg < -360)
            deg += 360;
        gy = vel;
        //Toast.makeText(this, "Sensing"+Double.toString(vel), Toast.LENGTH_SHORT).show();
        tx.setText("Rotation: " + Math.round(deg) + " degrees");


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void step(long timeNs) throws IOException {
        numSteps++;
        txt1.setText("Steps"+numSteps);
        File dir = new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/IMU");
        dir.mkdirs();
        File file = new File(dir, "IMUDATA.txt");
        x+=0.69*Math.cos(Math.toRadians(deg));
        y+=0.69*Math.sin(Math.toRadians(deg));
        mypath.setText(Math.round(x)+"  "+Math.round(y));
        FileOutputStream fileOutputStream = new FileOutputStream(file, true);
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
        outputStreamWriter.write(0.69+" " + deg+"\n");
        outputStreamWriter.close();
        fileOutputStream.close();
    }
    public static class myhandler extends Handler {
        private GroupSelect parent;

        public myhandler(GroupSelect parent) {
            super();
            this.parent=parent;
        }

        @Override
        public void handleMessage(Message msg) {
            DatagramPacket packet= (DatagramPacket) msg.obj;
            String invite=new String(packet.getData());
            String address=packet.getAddress().toString();
            String x[]=invite.split("_");
           parent.path.setText(Long.toString(Math.round(Double.parseDouble(x[0])))+"    "+Long.toString(Math.round(Double.parseDouble(x[1]))));

            }
        }}

