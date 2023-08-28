package com.atb.mdns_resolve.ui.activities

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.atb.mdns_resolve.databinding.MainActivityBinding
import com.atb.mdns_resolve.model.BTScanModel
import com.atb.mdns_resolve.model.HostViewModel
import com.atb.mdns_resolve.service.NsdWorker
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val hostViewModel: HostViewModel by viewModels()
    private val scanModel: BTScanModel by viewModels()

    private lateinit var binding: MainActivityBinding
   // lateinit var mRxdnssd : Rx2DnssdEmbedded

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)
       // mRxdnssd  = Rx2DnssdEmbedded(applicationContext)
        setContentView(binding.root)

        binding.buttonReload.setOnClickListener {
            //startNsdWorker()
            scanModel.startScan(context = applicationContext)
        }
    }

    override fun onStart() {
        super.onStart()

        hostViewModel.itemLiveData.observe(this){
           if(it !=null){
               runOnUiThread {
                   binding.fullNameField.text = it.toString()//E1F15E15
               }
           }else
               binding.fullNameField.text = "Сетей не нашлось"
               //Toast.makeText(applicationContext, "Wifi сетей не нашлось...", Toast.LENGTH_SHORT).show()
        }


        scanModel.devices.observe(this) { devices ->
            binding.fullNameField.text =devices.toString()
        }
    }

    fun startNsdWorker() {
        val request = OneTimeWorkRequestBuilder<NsdWorker>().build()
        WorkManager.getInstance(applicationContext).enqueueUniqueWork("Work", ExistingWorkPolicy.REPLACE, request);
    }
    /*fun getRxDnssd() : Rx2DnssdEmbedded {
        return mRxdnssd
    }*/
}



