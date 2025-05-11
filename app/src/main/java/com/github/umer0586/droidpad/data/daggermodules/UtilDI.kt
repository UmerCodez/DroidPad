package com.github.umer0586.droidpad.data.daggermodules

import android.content.Context
import com.github.umer0586.droidpad.data.sensor.SensorEventProvider
import com.github.umer0586.droidpad.data.sensor.SensorEventProviderImp
import com.github.umer0586.droidpad.data.sensor.SensorManagerUtil
import com.github.umer0586.droidpad.data.sensor.SensorManagerUtilImp
import com.github.umer0586.droidpad.data.util.BluetoothUtil
import com.github.umer0586.droidpad.data.util.BluetoothUtilImp
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped


@Module
@InstallIn(ViewModelComponent::class)
object UtilDI {

    @Provides
    @ViewModelScoped
    fun provideBluetoothUtil(@ApplicationContext appContext: Context): BluetoothUtil {
        return BluetoothUtilImp(appContext)
    }

    @Provides
    @ViewModelScoped
    fun provideSensorMangerUtil(@ApplicationContext appContext: Context): SensorManagerUtil {
        return SensorManagerUtilImp(appContext)
    }

    @Provides
    @ViewModelScoped
    fun provideSensorEventProvider(@ApplicationContext appContext: Context): SensorEventProvider {
        return SensorEventProviderImp(appContext)
    }



}