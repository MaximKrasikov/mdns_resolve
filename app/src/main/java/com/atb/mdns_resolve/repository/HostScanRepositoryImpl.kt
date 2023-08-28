package com.atb.mdns_resolve.repository

import android.content.Context
import android.util.Log
import com.atb.mdns_resolve.service.HostRecord
import com.github.druk.rx2dnssd.Rx2DnssdEmbedded


class HostScanRepositoryImpl constructor(context: Context) : HostScanRepository {

    var mRxdnssd : Rx2DnssdEmbedded = Rx2DnssdEmbedded(context)
    val itemImpl = mutableListOf<HostRecord>()

    override fun updateHostMutableList(host: HostRecord) {
        if (itemImpl.contains(host)) {
            Log.i("updateHostList", "Already have entry for ${host.hostName}")
            return
        }
        // Here can also check host.hostName etc to make sure it's something we're interested in,
        // For atb :
        // if (!host.hostName.startsWith("myspeaker")) {
        //     Log.i("updateHostList", "Not my speaker : ${host}")
        //     return
        // }
        itemImpl.add(host)
    }
    override fun getHostMutableList(): MutableList<HostRecord> {
        return itemImpl
    }
    override fun getRxDnssd(): Rx2DnssdEmbedded {
        return mRxdnssd
    }
}