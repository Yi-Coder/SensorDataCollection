package edu.gsu.sensordatacollection;

import static androidx.core.content.PackageManagerCompat.LOG_TAG;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.List;


public class SensorDataService extends Service implements SensorEventListener {

    public static final String ACTION_LOCATION_BROADCAST = SensorDataService.class.getName() + "sensorDataBroadCast";
    public static final String SENSOR_DATA = "sensor_data";

    private SensorManager sensorManager;

    private Sensor mLight;

    float mAccX;
    float mAccY;
    float mAccZ;

    float mGyroX;
    float mGyroY;
    float mGyroZ;

    float mMagX;
    float mMagY;
    float mMagZ;

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
        else if(event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
            mMagX = event.values[0];
            mMagY = event.values[1];
            mMagZ = event.values[2];
        }
        sendBroadcastMessage(mAccX);

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
        Log.d("SensorDataService", "servicestarted" );
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mLight = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, mLight, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void sendBroadcastMessage(float senorData) {
            Intent intent = new Intent(ACTION_LOCATION_BROADCAST);
            intent.putExtra(SENSOR_DATA, new Double(senorData));
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(this);
        Log.d("SensorDataService", "serviceStop" );
    }

}
