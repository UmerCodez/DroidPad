package com.github.umer0586.droidpad.data.daggermodules

import android.content.Context
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
}