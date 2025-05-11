/*
 *     This file is a part of DroidPad (https://www.github.com/umer0586/DroidPad)
 *     Copyright (C) 2025 Umer Farooq (umerfarooq2383@gmail.com)
 *
 *     DroidPad is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     DroidPad is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with DroidPad. If not, see <https://www.gnu.org/licenses/>.
 *
 */

package com.github.umer0586.droidpad.data.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Handler
import android.os.HandlerThread
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class SensorEventProviderImp(
    context: Context
) : SensorEventProvider, SensorEventListener {


    private val scope : CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val _events = MutableSharedFlow<SensorEvent>()
    private var handlerThread: HandlerThread = HandlerThread("Handler Thread")
    private var handler: Handler

    private var _sensorTypes = listOf<Int>()

    override val events: Flow<SensorEvent>
        get() = _events.asSharedFlow()


    init {
        handlerThread.start()
        handler = Handler(handlerThread.looper)
    }


    override fun provideEventsFor(sensorTypes: List<Int>) {

        _sensorTypes = sensorTypes


        for (sensorType in sensorTypes) {
            val sensor = sensorManager.getDefaultSensor(sensorType)
                ?: throw IllegalArgumentException("Sensor not found")

            sensorManager.registerListener(this,sensor,SensorManager.SENSOR_DELAY_NORMAL,handler)

        }

    }

    override fun stopProvidingEvents() {

        for (sensorType in _sensorTypes) {
            sensorManager.unregisterListener(this,sensorManager.getDefaultSensor(sensorType))
        }

    }

    override fun onSensorChanged(sensorEvent: android.hardware.SensorEvent) {

        // Comment out to observe if we are receiving any sensors events while disconnected
        // Log.d("SensorEvent", "${sensorEvent.values[0]}")

        val sensorType = sensorEvent.sensor.type
        scope.launch{
            if (sensorType == Sensor.TYPE_ACCELEROMETER) {
                _events.emit(
                    AccelerometerEvent(
                        x = sensorEvent.values[0],
                        y = sensorEvent.values[1],
                        z = sensorEvent.values[2]
                    )
                )
            }
            else if(sensorType == Sensor.TYPE_GYROSCOPE){
                _events.emit(
                    GyroscopeEvent(
                        x = sensorEvent.values[0],
                        y = sensorEvent.values[1],
                        z = sensorEvent.values[2]
                    )
                )
            }
        }

    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    override fun cleanUp() {
        if(handlerThread.isAlive)
            handlerThread.quitSafely()

        try{
            scope.cancel()
        }catch (e: IllegalStateException){
            e.printStackTrace()
        }
    }
}