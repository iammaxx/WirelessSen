package com.wirelesssen;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.ScanResult;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.util.HashMap;
import java.util.List;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
public class GroupSelect extends AppCompatActivity implements SensorEventListener,StepListener{
    SensorManager mSensorManager;
    SimpleStepDetector simpleStepDetector;
    int numSteps;
    TextView tx,txt1,st;
    boolean type;
    private TextView mypath;
    private TextView path;
    static HashMap<String, String> macip;
    HashMap<String, String> hostip;
    HashMap<String, String> iphost;

    Bitmap mutableBitmap;
    Bitmap workingBitmap;
    Paint paint = new Paint();
    float x,y;
    float a,b;
    long theta;
    TextView textView;
    static int cnt=4;
    float ix,iy,fx,fy;
    Canvas canvas;
    TextView textView2;
    ImageView imageView;
    Button bx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_select);
        macip = (HashMap<String, String>) getIntent().getExtras().getSerializable("macip");
        hostip=(HashMap<String, String>) getIntent().getExtras().getSerializable("hostip");
        iphost =(HashMap<String, String>) getIntent().getExtras().getSerializable("iphost");
        x=0;
        y=0;
        bx=(Button)findViewById(R.id.start);
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


        imageView = (ImageView)findViewById(R.id.imageView);
        BitmapFactory.Options myOptions = new BitmapFactory.Options();
        myOptions.inDither = true;
        myOptions.inScaled = false;
        myOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
        myOptions.inPurgeable = true;
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.map,myOptions);
        paint.setAntiAlias(true);
        paint.setColor(Color.BLUE);
        workingBitmap = Bitmap.createBitmap(bitmap);
        mutableBitmap = workingBitmap.copy(Bitmap.Config.ARGB_8888, true);

        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                canvas = new Canvas(mutableBitmap);

                if(cnt==4) {
                    // Toast.makeText(getApplicationContext(),"if",Toast.LENGTH_SHORT).show();
                    ix = event.getX();
                    iy = event.getY();
                    x=ix/10;
                    y=iy/10;
                    canvas.drawCircle(ix, iy, 25, paint);
                    cnt--;
                }
                else if(cnt==3)
                {
                    cnt--;
                }
                else if (cnt==2) {
                    //  Toast.makeText(getApplicationContext(),"else",Toast.LENGTH_SHORT).show();
                    fx = event.getX();
                    fy = event.getY();
                    canvas.drawCircle(fx, fy, 25, paint);
                    cnt--;

                    theta=Math.round(Math.toDegrees(Math.atan((iy-fy)/(fx-ix))));
                    if(fx>ix&&fy>iy)
                        theta+=360;
                    else if(fy>iy &&fx<ix)
                        theta+=180;
                    else if(fx<ix && fy<iy)
                        theta=180+theta;
                    bx.setEnabled(true);
                    deg-=theta;
                    imageView.setOnTouchListener(null);
                }
                imageView.setAdjustViewBounds(true);
                imageView.setImageBitmap(mutableBitmap);

                // mutableBitmap = workingBitmap.copy(Bitmap.Config.ARGB_8888, true);
                //Toast.makeText(MainActivity.this, String.valueOf(cnt), Toast.LENGTH_SHORT).show();

                return true;
            }
        });

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
        y-=0.69*Math.sin(Math.toRadians(deg));

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
            DatagramPacket packet = (DatagramPacket) msg.obj;
        //    switch(msg.what){
         //       case 0 :
            String invite=new String(packet.getData());
            String address=packet.getAddress().toString();
            String x[]=invite.split("_");
           parent.path.setText(Long.toString(Math.round(Double.parseDouble(x[0])))+"    "+Long.toString(Math.round(Double.parseDouble(x[1]))));
            parent.canvas = new Canvas(parent.mutableBitmap);
            parent.a=Float.parseFloat(x[0]);
            parent.b=Float.parseFloat(x[1]);
            parent.canvas.drawCircle(parent.a*10,parent.b*10,25,parent.paint);
            parent.imageView.setAdjustViewBounds(true);
            parent.imageView.setImageBitmap(parent.mutableBitmap);
            parent.mutableBitmap = parent.workingBitmap.copy(Bitmap.Config.ARGB_8888, true);




            /*break;
                case 1 :

                    try {
                        packet = (DatagramPacket) msg.obj;
                        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
                        ObjectOutputStream out = new ObjectOutputStream(byteOut);
                        out.writeObject(macip);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                    ByteArrayInputStream bytein=new ByteArrayInputStream(packet.getData());
                    try {
                        ObjectInputStream in = new ObjectInputStream(bytein);
                        HashMap<String,String> data2 = (HashMap<String,String>) in.readObject();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            }*/
        }}}

