package edu.gsu.sensordatacollection;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class SensorDataService extends Service implements SensorEventListener {

    public static final String fileName = "sensorData.csv";
    private  String sdcard = "";
    private BufferedWriter writer;
    private SensorManager sensorManager;


    private Sensor accelerator;
    private Sensor gyroscope;
    private Sensor rotation;

    float mAccX = 0.0f;
    float mAccY= 0.0f;
    float mAccZ= 0.0f;

    float mGyroX= 0.0f;
    float mGyroY= 0.0f;
    float mGyroZ= 0.0f;

    float mMagX= 0.0f;
    float mMagY= 0.0f;
    float mMagZ= 0.0f;

    BufferedWriter out ;

    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.d("sensorData", "x:"+mAccX +";y:"+mAccY+";z:"+mAccZ);
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            mAccX = event.values[0];
            mAccY = event.values[1];
            mAccZ = event.values[2];
        }
        else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE){
            mGyroX = event.values[0];
            mGyroY = event.values[1];
            mGyroZ = event.values[2];
        }
        else if(event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR){
            mMagX = event.values[0];
            mMagY = event.values[1];
            mMagZ = event.values[2];
        }

        try {
            writer.append(mAccX+","+mAccY+","+mAccZ+","+mGyroX+","+mGyroY+","+mGyroZ+","+mMagX+","+mMagY+","+mMagZ+'\n');
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d("SensorDataService", "service started" );
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerator = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        rotation = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        sensorManager.registerListener(this, accelerator, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, rotation, SensorManager.SENSOR_DELAY_NORMAL);

        sdcard = Environment.getExternalStorageDirectory().getPath();



        File sensorDataFile =  new File(sdcard, fileName);
        try {
            writer = new BufferedWriter(new FileWriter(sensorDataFile));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        int zone = intent.getIntExtra(MainActivity.ZONE, 0);
                        System.out.println("receiving clicked zone"+ zone);
                        try {
                            writer.append(mAccX+","+mAccY+","+mAccZ+","+mGyroX+","+mGyroY+","+mGyroZ+","+mMagX+","+mMagY+","+mMagZ+","+ zone+'\n');
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }, new IntentFilter(MainActivity.ACTION_LOCATION_BROADCAST)
        );
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(this);
        Log.d("SensorDataService", "service stopped" );
        try {
            writer.flush();
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
