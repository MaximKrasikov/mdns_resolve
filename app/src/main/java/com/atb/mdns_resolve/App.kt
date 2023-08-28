package com.atb.mdns_resolve

import android.app.Application
import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.atb.mdns_resolve.service.NsdWorker
import com.github.druk.rx2dnssd.Rx2DnssdEmbedded
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {

    companion object {
        //lateinit var ctx: Context

        //lateinit var mRxdnssd : Rx2DnssdEmbedded

      /*  fun getRxDnssd() : Rx2DnssdEmbedded {
            return mRxdnssd
        }*/

        /*fun startNsdWorker() {
            val request = OneTimeWorkRequestBuilder<NsdWorker>().build()
            WorkManager.getInstance(ctx).enqueueUniqueWork("Work", ExistingWorkPolicy.REPLACE, request);
        }*/
    }

    override fun onCreate() {
        super.onCreate()
        //ctx = applicationContext
        //mRxdnssd = Rx2DnssdEmbedded(applicationContext)
    }
}
