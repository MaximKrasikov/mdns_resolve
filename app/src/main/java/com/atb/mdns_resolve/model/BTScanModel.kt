package com.atb.mdns_resolve.model

import android.app.Application
import android.content.Context
import android.net.nsd.NsdServiceInfo
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atb.mdns_resolve.android.Logging
import com.atb.mdns_resolve.repository.NsdRepository
import com.atb.mdns_resolve.util.anonymize
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class BTScanModel @Inject constructor(
    private val application: Application,
    private val nsdRepository: NsdRepository
) : ViewModel(), Logging {

    private val context: Context get() = application.applicationContext
    val devices = MutableLiveData<MutableMap<String, DeviceListEntry>>(mutableMapOf())
    var selectedAddress: String? = null
    val selectedNotNull: String get() = selectedAddress ?: "n"

    private var networkDiscovery: Job? = null
    fun startScan(context: Context?) {
        // Start Network Service Discovery (find TCP devices)
        networkDiscovery = nsdRepository.networkDiscoveryFlow()
            .onEach { addDevice(TCPDeviceListEntry(it)) }
            .launchIn(viewModelScope)
    }

    private fun addDevice(entry: DeviceListEntry) {
        val oldDevs = devices.value!!
        oldDevs[entry.fullAddress] = entry // Add/replace entry
        devices.value = oldDevs // trigger gui updates
    }

    class TCPDeviceListEntry(val service: NsdServiceInfo) : DeviceListEntry(
        service.host.toString().substring(1),
        service.host.toString().replace("/", " "),
        true
    )

    open class DeviceListEntry(val name: String, val fullAddress: String, val bonded: Boolean) {
        val prefix get() = fullAddress[0]
        val address get() = fullAddress.substring(1)

        override fun toString(): String {
            return "DeviceListEntry(name=${name.anonymize}, addr=${address.anonymize}, bonded=$bonded)"
        }

        val isTCPATB: Boolean get() = prefix == 't'
    }

    /* @SuppressLint("MissingPermission")
     fun getDeviceListEntry(fullAddress: String, bonded: Boolean = false): DeviceListEntry {
         val address = fullAddress.substring(1)
         val device = getRemoteDevice(address)
         return if (device != null && device.name != null) {
             BLEDeviceListEntry(device)
         } else {
             DeviceListEntry(address, fullAddress, bonded)
         }
     }*/

}