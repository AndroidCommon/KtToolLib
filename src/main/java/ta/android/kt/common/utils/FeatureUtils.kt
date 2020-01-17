/*
 * Copyright (c) 2020. - Tyler Tata,  cutmyfinger@163.com
 */

package ta.android.kt.common.utils

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager

/**
 * Android Common Feature API Utils
 */
object FeatureUtils {

    private fun hasSensor(context: Context, sensorType: Int): Boolean = try {
        null != (context.getSystemService(Context.SENSOR_SERVICE) as SensorManager).getDefaultSensor(
            sensorType
        )
    } catch (e: Exception) {
        false
    }

    /**
     * detect whether current mobile has Acc sensor
     * @param context the App Context
     * @return true if equipment, or false
     */
    fun hasAccelerometerSensor(context: Context): Boolean =
        hasSensor(context, Sensor.TYPE_ACCELEROMETER)

    /**
     * detect whether current mobile has magnetic sensor
     * @param context the App Context
     * @return true if equipment, or false
     */
    fun hasMagneticFieldSensor(context: Context): Boolean =
        hasSensor(context, Sensor.TYPE_MAGNETIC_FIELD)

    /**
     * detect whether current mobile has orientation change sensor
     * @param context the App Context
     * @return true if equipment, or false
     */
    fun hasOrientationSensor(context: Context): Boolean =
        hasSensor(context, Sensor.TYPE_ORIENTATION)

    /**
     * detect whether current mobile has gyroscope sensor, which used to recognize direction
     * @param context the App Context
     * @return true if equipment, or false
     */
    fun hasGyroscopeSensor(context: Context): Boolean = hasSensor(context, Sensor.TYPE_GYROSCOPE)

    /**
     * detect whether current mobile has light sensor
     * @param context the App Context
     * @return true if equipment, or false
     */
    fun hasLightSensor(context: Context): Boolean = hasSensor(context, Sensor.TYPE_LIGHT)

    /**
     * detect whether current mobile has distance sensor
     * @param context the App Context
     * @return true if equipment, or false
     */
    fun hasDistanceSensor(context: Context): Boolean = hasSensor(context, Sensor.TYPE_PROXIMITY)

    /**
     * detect whether current mobile has temperature sensor
     * @param context the App Context
     * @return true if equipment, or false
     */
    fun hasTemperatureSensor(context: Context): Boolean =
        hasSensor(context, Sensor.TYPE_TEMPERATURE) || hasSensor(
            context,
            Sensor.TYPE_AMBIENT_TEMPERATURE
        )
}