package com.example.compassexample

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.ImageView

class MainActivity : AppCompatActivity(), SensorEventListener{

    private lateinit var imageView:ImageView
    private var mGravity = FloatArray(3)
    private var mGeomagnetic = FloatArray(3)
    private var azimuth:Float = 0F
    private var currectAzimuth = 0F
    private lateinit var mSensorManager:SensorManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main)

        imageView = findViewById(R.id.compass)
        mSensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

    }

    override fun onResume(){
        super.onResume()
        mSensorManager.registerListener(this,mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),SensorManager.SENSOR_DELAY_GAME)

        mSensorManager.registerListener(this,mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_GAME)

    }
    
    override fun onPause(){
        super.onPause()

        mSensorManager.unregisterListener(this)

    }

    override fun onSensorChanged(sensorEvent: SensorEvent?) {
        var alpha:Float = 0.97f
        synchronized(this){
            if (sensorEvent!!.sensor.type == Sensor.TYPE_ACCELEROMETER){
                mGravity[0] = alpha*mGravity[0] + (1 - alpha)*sensorEvent.values[0]
                mGravity[1] = alpha*mGravity[1] + (1 - alpha)*sensorEvent.values[1]
                mGravity[2] = alpha*mGravity[2] + (1 - alpha)*sensorEvent.values[2]
            }

            if (sensorEvent.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
                mGeomagnetic[0] = alpha * mGeomagnetic[0] + (1 - alpha) * sensorEvent.values[0]
                mGeomagnetic[1] = alpha * mGeomagnetic[1] + (1 - alpha) * sensorEvent.values[1]
                mGeomagnetic[2] = alpha * mGeomagnetic[2] + (1 - alpha) * sensorEvent.values[2]
            }

            var R = FloatArray(9)
            var I = FloatArray(9)
            var success:Boolean = SensorManager.getRotationMatrix(R,I,mGravity,mGeomagnetic)

            if (success) {
                var orientation = FloatArray(3)
                SensorManager.getOrientation(R,orientation)
                azimuth = Math.toDegrees(orientation[0].toDouble()).toFloat()

                azimuth = (azimuth + 360) % 360

                var anim:Animation = RotateAnimation(currectAzimuth,-azimuth, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
                currectAzimuth = azimuth

                anim.duration = 500
                anim.repeatCount = 0
                anim.fillAfter = true

                imageView.startAnimation(anim)
            }
        }
    }


    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }


}
