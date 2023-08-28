package com.atb.mdns_resolve.repository

import com.atb.mdns_resolve.service.HostRecord
import com.github.druk.rx2dnssd.Rx2DnssdEmbedded
 class HostScanClient constructor(_hostScanClient : HostScanRepository?) {
    private var hostScanClient: HostScanRepository?= null
    init {
        hostScanClient = _hostScanClient
    }
   fun updateHost(host: HostRecord){
       hostScanClient?.updateHostMutableList(host)
   }
    fun getHostList(): MutableList<HostRecord>? {
       return hostScanClient?.getHostMutableList()
    }
    fun getRxDnssd(): Rx2DnssdEmbedded?{
        return hostScanClient?.getRxDnssd()
    }
}