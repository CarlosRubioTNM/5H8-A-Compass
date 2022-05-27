package com.example.compass;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    //Componentes gráficos
    private TextView lblOrientation;
    private ImageView imgRose;

    //Sensores
    private SensorManager sensorManager;
    private Sensor sensorAccel, sensorMagnet;

    private float[] lastAccel = new float[3];
    private float[] lastMagnet = new float[3];
    private float[] rotationMatrix = new float[9];
    private float[] orientation = new float[3];

    private boolean isAccel = false;
    private boolean isMagnet = false;

    private long lastUpdatedTime = 0;
    private long updateThreshold = 250;
    private float currentRotation = 0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lblOrientation = findViewById(R.id.lblOrientation);
        imgRose = findViewById(R.id.imgRose);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorAccel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorMagnet = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensorAccel, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, sensorMagnet, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this, sensorAccel);
        sensorManager.unregisterListener(this, sensorMagnet);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor == sensorAccel) {
            System.arraycopy(sensorEvent.values,0,lastAccel,0,sensorEvent.values.length);
            isAccel = true;
        } else if (sensorEvent.sensor == sensorMagnet) {
            System.arraycopy(sensorEvent.values,0,lastMagnet,0,sensorEvent.values.length);
            isMagnet = true;
        }
        if (!isAccel || !isMagnet | System.currentTimeMillis() - lastUpdatedTime <= updateThreshold) {
            return;
        }

        SensorManager.getRotationMatrix(rotationMatrix, null, lastAccel, lastMagnet);
        SensorManager.getOrientation(rotationMatrix, orientation);

        float azimuthRadians = orientation[0];
        float azimuth = (float) Math.toDegrees(azimuthRadians);
        if (azimuth < 0f) {
            azimuth = 360f + azimuth;
        }


        RotateAnimation rotateAnimation = new RotateAnimation(currentRotation, -azimuth, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        rotateAnimation.setDuration(updateThreshold);
        rotateAnimation.setFillAfter(true);
        imgRose.startAnimation(rotateAnimation);

        currentRotation = -azimuth;
        lastUpdatedTime = System.currentTimeMillis();
        int angleInt = (int) azimuth;
        lblOrientation.setText(String.valueOf(angleInt) + "°");


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}