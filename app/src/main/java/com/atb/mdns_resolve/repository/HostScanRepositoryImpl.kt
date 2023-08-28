package com.atb.mdns_resolve.repository

import android.content.Context
import com.atb.mdns_resolve.service.HostRecord
import com.github.druk.rx2dnssd.Rx2DnssdEmbedded


class HostScanRepositoryImpl constructor(context: Context) : HostScanRepository {

    var mRxdnssd : Rx2DnssdEmbedded = Rx2DnssdEmbedded(context)

    override fun updateHostMutableList(host: HostRecord) {

    }

    override fun getRxDnssd(): Rx2DnssdEmbedded {
        return mRxdnssd
    }
}