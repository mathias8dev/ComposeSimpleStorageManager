package com.mathias8dev.composesimplestoragemanager

import android.app.Application
import com.mathias8dev.composesimplestoragemanager.di.AppModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.ksp.generated.defaultModule
import org.koin.ksp.generated.module
import timber.log.Timber

class SSMApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
        startKoin {
            androidContext(this@SSMApplication)
            modules(
                defaultModule,
                AppModule().module
            )
        }
    }
}