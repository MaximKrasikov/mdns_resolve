package com.atb.mdns_resolve.model

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.atb.mdns_resolve.repository.HostScanClient
import com.atb.mdns_resolve.service.HostRecord
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Optional
import javax.inject.Inject

@HiltViewModel
class HostViewModel @Inject constructor() : ViewModel() {

    private val _items = MutableLiveData<ArrayList<HostRecord>>()
    val itemLiveData: LiveData<ArrayList<HostRecord>> = _items

    var hostScanClient: HostScanClient? = null

    fun updateHostList() {
        if (hostScanClient?.getHostList()?.size != 0)
            _items.value = hostScanClient?.getHostList() as ArrayList<HostRecord>?
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun getHostRecordByNameFromList(name: String): HostRecord {
        val hostRecord: Optional<HostRecord>? =
            itemLiveData.value?.stream()?.filter { it.serviceName.equals(name) }?.findFirst()
        return hostRecord!!.get()
    }
}