package com.atb.mdns_resolve.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.atb.mdns_resolve.service.HostRecord
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HostViewModel @Inject constructor() : ViewModel() {
    /*val itemLiveData: LiveData<ArrayList<HostRecord>>
        get() = items*/

    //private val items = MutableLiveData<ArrayList<HostRecord>>()
    private val _items = MutableLiveData<ArrayList<HostRecord>>()
    val itemLiveData : LiveData<ArrayList<HostRecord>> = _items

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
        _items.value = itemImpl as ArrayList<HostRecord>? //.postValue(itemImpl as ArrayList<HostRecord>?)
    }
}