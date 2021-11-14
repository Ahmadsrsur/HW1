package com.example.hw1;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private TextView helloTxt;
    private TextView textName;
    private TextView textPassword;
    private EditText nameEdit;
    private EditText passwordEdit;
    private Button log_in;
    public static float swRoll;
    public static float swPitch;
    public static float swAzimuth;
    public static double azimuth;
    public static double pitch;

    public static SensorManager mSensorManager;
    public static Sensor accelerometer;
    public static Sensor magnetometer;

    public static float[] mAccelerometer = null;
    public static float[] mGeomagnetic = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        findViews();
        initViews();

}


    private void findViews() {
        helloTxt = findViewById(R.id.helloTxt);
        nameEdit = findViewById(R.id.NameEdit);
        passwordEdit = findViewById(R.id.PasswordEdit);
        textName = findViewById(R.id.NameTextV);
        textPassword = findViewById(R.id.PasswordTextV);
        log_in = findViewById(R.id.log_in);

    }


    private void initViews() {

        log_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float brightness = Settings.System.getInt(getApplicationContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, -1);
                //just when the phone battery is less then 80% , and the position of the phone is flipped and the brightness level is less then 150
                if((getBatteryPercentage(getApplicationContext()) < 80) && (pitch > 0) && (brightness < 150 )) {

                    Toast.makeText(getApplicationContext(), "Nice, Hello Ahmad " +"\n" + "Battery value "  +String.valueOf(getBatteryPercentage(getApplicationContext()))
                            +"\n"+" orientation number " + String.valueOf(pitch) + "\n"+"brightness level " + String.valueOf(brightness), Toast.LENGTH_SHORT).show();

                }else{
                    Toast.makeText(getApplicationContext(), "oops! try more parameters", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static int getBatteryPercentage(Context context) {
            BatteryManager bm = (BatteryManager) context.getSystemService(BATTERY_SERVICE);
            return bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
    }



    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // onSensorChanged gets called for each sensor so we have to remember the values
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            mAccelerometer = event.values;
        }

        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            mGeomagnetic = event.values;
        }

        if (mAccelerometer != null && mGeomagnetic != null) {
            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, mAccelerometer, mGeomagnetic);

            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                // at this point, orientation contains the azimuth(direction), pitch and roll values.
                azimuth = 180 * orientation[0] / Math.PI;
                pitch = 180 * orientation[1] / Math.PI;
                double roll = 180 * orientation[2] / Math.PI;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this, accelerometer);
        mSensorManager.unregisterListener(this, magnetometer);
    }



}