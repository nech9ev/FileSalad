package com.lingburg.filesalad

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Process
import android.os.StrictMode
import androidx.core.content.getSystemService
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class FileSaladApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        if (isMainProcess()) {
            Timber.plant(Timber.DebugTree())
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder().penaltyFlashScreen().penaltyLog().build()
            )
            StrictMode.setVmPolicy(
                StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects().penaltyLog().build()
            )
        }
    }

    private fun Context.isMainProcess(): Boolean {
        val pid = Process.myPid()
        val activityManager: ActivityManager? = getSystemService() as ActivityManager?

        val isMainProcess = activityManager?.runningAppProcesses.orEmpty().any { processInfo ->
            processInfo.pid == pid && processInfo.processName == packageName
        }

        return isMainProcess
    }
}
