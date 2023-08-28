package com.atb.mdns_resolve.repository

import com.atb.mdns_resolve.service.HostRecord
import com.github.druk.rx2dnssd.Rx2DnssdEmbedded

interface HostScanRepository {
    fun updateHostMutableList(host: HostRecord)

    fun getRxDnssd() : Rx2DnssdEmbedded
}