package com.atb.mdns_resolve.service

import android.util.Log
import com.atb.mdns_resolve.repository.HostScanClient
import com.github.druk.rx2dnssd.BonjourService
import com.github.druk.rx2dnssd.Rx2DnssdEmbedded
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class NsdWorkerService constructor(
    _hostScanClient: HostScanClient
) {
    private var hostScanClient: HostScanClient?= null
    companion object {
        val TAG = NsdWorkerService::class.java.simpleName
    }
    init {
        hostScanClient= _hostScanClient
    }
    fun startScan(){
        networkBrowserTask()
    }

    private fun networkBrowserTask(): Disposable? {
        Log.i(NsdWorker.TAG, "Entering networkBrowserTask()")
        val mRxDnssd: Rx2DnssdEmbedded = hostScanClient?.getRxDnssd()!!
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
                    Log.i(NsdWorker.TAG, "App IP New: " + service.inet4Address.toString())
                    Log.i(NsdWorker.TAG, "App Nsd Service: $service")
                    val hostname: String = if (service.hostname == null) "Not known" else service.hostname!!
                    //hostViewModel.updateHostList(HostRecord(service.serviceName, hostname, service.inet4Address.toString()))
                    hostScanClient?.updateHost(HostRecord(service.serviceName, hostname, service.inet4Address.toString()))
                } else {
                    Log.i(NsdWorker.TAG, "Other Nsd Service: $service")
                }
            },
                { throwable: Throwable? ->
                    Log.e(NsdWorker.TAG, "Error: ", throwable)
                })

        Log.i(NsdWorker.TAG, "Leaving networkBrowserTask()")

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
){
    override fun toString(): String = " SN: $serviceName HN: $hostName IP: $ip4addr\n"
}