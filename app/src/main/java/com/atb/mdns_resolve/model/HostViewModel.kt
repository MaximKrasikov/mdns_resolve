package com.atb.mdns_resolve.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.atb.mdns_resolve.service.HostRecord

class HostViewModel : ViewModel() {
    val itemLiveData: LiveData<ArrayList<HostRecord>>
        get() = items

    private val items = MutableLiveData<ArrayList<HostRecord>>()
    private val itemImpl = mutableListOf<HostRecord>()

    fun updateHostList(host: HostRecord) {
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
        items.postValue(itemImpl as ArrayList<HostRecord>?)
    }
}