package com.makeevapps.simpletodolist.di

import android.content.Context
import com.makeevapps.simpletodolist.utils.DeviceInfo
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class UtilsModule {

    @Provides
    @Singleton
    internal fun provideDeviceInfo(context: Context): DeviceInfo = DeviceInfo(context)
}