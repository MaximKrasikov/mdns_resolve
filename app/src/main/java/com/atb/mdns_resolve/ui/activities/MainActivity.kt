package com.atb.mdns_resolve.ui.activities

import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.atb.mdns_resolve.databinding.MainActivityBinding
import com.atb.mdns_resolve.model.HostViewModel
import com.atb.mdns_resolve.repository.HostScanClient
import com.atb.mdns_resolve.repository.HostScanRepositoryImpl
import com.atb.mdns_resolve.service.HostRecord
import com.atb.mdns_resolve.service.NsdWorkerService
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val hostViewModel: HostViewModel by viewModels()

    private var hostScanClient: HostScanClient? = null

    private lateinit var binding: MainActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)

        setContentView(binding.root)

        hostScanClient = HostScanClient(HostScanRepositoryImpl(context = applicationContext))

        hostViewModel.hostScanClient = hostScanClient

        binding.buttonReload.setOnClickListener {
            startNsdWorker()
        }
        binding.buttonGet.setOnClickListener {
            hostViewModel.updateHostList()
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onStart() {
        super.onStart()

        hostViewModel.itemLiveData.observe(this) { list ->
            if (list.size != 0) {
                val hostRecord: HostRecord =
                    hostViewModel.getHostRecordByNameFromList("ATB2100_Krasikov")
                runOnUiThread {
                    binding.fullNameField.text =
                        hostRecord.toString()//hostRecord.hostName+ " IP: " + hostRecord.ip4addr
                }
            } else
                binding.fullNameField.text = "Сетей не нашлось"
        }
    }
    fun startNsdWorker() {
        NsdWorkerService(hostScanClient!!).startScan()
    }
}



