package com.atb.mdns_resolve.service

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.atb.mdns_resolve.App
import com.atb.mdns_resolve.model.HostViewModel
import com.github.druk.rx2dnssd.BonjourService
import com.github.druk.rx2dnssd.Rx2DnssdEmbedded
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class NsdWorker constructor(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    var hostViewModel = HostViewModel()
    companion object {
        val TAG = NsdWorker::class.java.simpleName
    }

    override suspend fun doWork(): Result {
        return try {
            try {
                networkBrowserTask()
                Result.success()
            } catch (e: Exception) {
                Log.d(TAG, "exception in doWork ${e.message}")
                Result.failure()
            }
        } catch (e: Exception) {
            Log.d(TAG, "exception in doWork ${e.message}")
            Result.failure()
        }
    }

    private fun networkBrowserTask(): Disposable? {
        Log.i(TAG, "Entering networkBrowserTask()")
        val mRxDnssd: Rx2DnssdEmbedded = App.getRxDnssd()
        val mDisposable: Disposable? = mRxDnssd.browse(
            ServiceConstants.SERVICE_TYPE,
            ServiceConstants.DOMAIN_NAME,
        )
            .compose(mRxDnssd.resolve())
            .compose(mRxDnssd.queryIPRecords())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ service: BonjourService ->
                if (!service.isLost && service.regType.startsWith("_http") && service.inet4Address != null) {
                    Log.i(TAG, "App IP New: " + service.inet4Address.toString())
                    Log.i(TAG, "App Nsd Service: $service")
                    val hostname: String = if (service.hostname == null) "Not known" else service.hostname!!
                    hostViewModel.updateHostList(HostRecord(service.serviceName, hostname, service.inet4Address.toString()))
                } else {
                    Log.i(TAG, "Other Nsd Service: $service")
                }
            },
                { throwable: Throwable? ->
                    Log.e(TAG, "Error: ", throwable)
                })
        Log.i(TAG, "Leaving networkBrowserTask()")

        Log.e("mDisposable", mDisposable.toString())
        return mDisposable
    }
}

class ServiceConstants {
    companion object {
        const val SERVICE_TYPE = "_http._tcp"
        const val DOMAIN_NAME = "local."
    }
}

data class HostRecord(
    val serviceName: String,
    val hostName: String,
    val ip4addr: String
)