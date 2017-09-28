package com.wirelesssen;

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
public class canvasplot extends AppCompatActivity {
    Bitmap mutableBitmap;
    Paint paint = new Paint();
    float x,y;
    long theta;
    TextView textView;
    static int cnt=4;
    float ix,iy,fx,fy;
    Canvas canvas;
    TextView textView2;
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canvasplot);
        imageView = (ImageView)findViewById(R.id.imageView);
        BitmapFactory.Options myOptions = new BitmapFactory.Options();
        myOptions.inDither = true;
        myOptions.inScaled = false;
        myOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
        myOptions.inPurgeable = true;
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.csels,myOptions);
        paint.setAntiAlias(true);
        paint.setColor(Color.BLUE);
        final Bitmap workingBitmap = Bitmap.createBitmap(bitmap);
        mutableBitmap = workingBitmap.copy(Bitmap.Config.ARGB_8888, true);

        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                canvas = new Canvas(mutableBitmap);
                textView = (TextView) findViewById(R.id.textView);
                textView.setText("X:" + event.getX() + "\nY:" + event.getY());
                if(cnt==4) {
                    // Toast.makeText(getApplicationContext(),"if",Toast.LENGTH_SHORT).show();
                    ix = event.getX();
                    iy = event.getY();
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
                    textView2=(TextView)findViewById(R.id.textView2);
                    theta=Math.round(Math.toDegrees(Math.atan((iy-fy)/(fx-ix))));
                    if(fx>ix&&fy>iy)
                        theta+=360;
                    else if(fy>iy &&fx<ix)
                        theta+=180;
                    else if(fx<ix && fy<iy)
                        theta=180+theta;
                    textView2.setText("theta="+theta);
                }
                imageView.setAdjustViewBounds(true);
                imageView.setImageBitmap(mutableBitmap);

                // mutableBitmap = workingBitmap.copy(Bitmap.Config.ARGB_8888, true);
                //Toast.makeText(MainActivity.this, String.valueOf(cnt), Toast.LENGTH_SHORT).show();

                return true;
            }
        });

    }

}

