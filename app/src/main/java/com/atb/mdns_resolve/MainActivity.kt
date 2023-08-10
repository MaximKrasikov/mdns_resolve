package com.atb.mdns_resolve

import android.content.Context
import android.os.Bundle
import android.provider.UserDictionary
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
/*import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList*/
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.atb.mdns_resolve.R
import com.atb.mdns_resolve.databinding.MainActivityBinding
import com.github.druk.rx2dnssd.BonjourService
import com.github.druk.rx2dnssd.Rx2DnssdEmbedded
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch

class AppConstants {
    companion object {
        const val SERVICE_TYPE = "_http._tcp"
        const val DOMAIN_NAME = "local."
    }
}

data class HostRecord(
    val serviceName: String,
    val hostName: String,
    val ip4addr: String
)


/*class MainViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return HostViewModel(MainRepository()) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}*/

class MainActivity : AppCompatActivity() {

    /*   private lateinit var viewModel: HostViewModel
        private lateinit var binding: MainActivityBinding*/

    private lateinit var binding: MainActivityBinding
    private val viewModel: HostViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                //val wordsDao = (application as App).db.wordsDao()
                return HostViewModel() as T
            }
        }
    }


    //private val viewModel: HostViewModel by viewModels { MainViewModelFactory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //lateinit var viewModel: HostViewModel
        //viewModel = ViewModelProvider(this).get(HostViewModel::class.java)



            //setContentView(R.layout.main_activity)

        //binding = DataBindingUtil.setContentView(this, R.layout.main_activity)


        binding.buttonReload.setOnClickListener {
            App.startNsdWorker()
        }





        lifecycleScope.launch {

            //val result = NsdWorker().doWork().outputData.toString()
/*            viewModel.itemLiveData. { words: List<UserDictionary.Words> ->

                binding.fullNameField.text = words.joinToString()

            }*/

        }

        //val items by hostViewModel.itemLiveData.

        viewModel.viewModelScope.launch {


        }

/*                viewModel.itemLiveData.observe(this, Observer { newitemLiveData ->
                    binding.fullNameField.text = newitemLiveData.toString()



                    //binding.fullNameField.text= viewModel.itemLiveData.toString()

                    Log.e("viewModel", viewModel.toString())
                })*/


        /*        viewModel.itemLiveData.observe(this, Observer { items ->
                    binding.fullNameField.text = hostViewModel.toString()
                })*/

        /*


                val model: HostViewModel
                HostViewModel().viewModelScope.launch {  }
        */


        //val myitem = model.itemLiveData


    }
}


var hostViewModel = HostViewModel()



class HostViewModel : ViewModel() {
    //val itemLiveData: ArrayList<HostRecord>
    val itemLiveData: LiveData<ArrayList<HostRecord>>
    //LiveData<SnapshotStateList<HostRecord>>

        //val itemLiveData = this.itemImpl.updateHostList()
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



fun AppTabs(model: HostViewModel) {
    //var state by remember { mutableStateOf(0) }
    val titles = listOf("Onboarded Speakers", "Not Onboarded Speakers", "TAB 3")
    Log.e("AppTabs", model.toString())
    Log.e("hostViewModel", hostViewModel.itemLiveData.toString())
    val items = model.itemLiveData
    Log.e("hostViewModel", items.toString())
    //hostViewModel.updateHostList()

    /*    Column {
            TabRow(selectedTabIndex = state) {
                titles.forEachIndexed { index, title ->
                    Tab(
                        text = { Text(title) },
                        selected = state == index,
                        onClick = { state = index }
                    )
                }
            }
            when (state) {
                0 -> Tab1(model)
                1 -> Tab2(model)
                2 -> Tab3(model)
            }
        }*/
}

class NsdWorker constructor(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        val TAG = NsdWorker::class.java.simpleName
    }

    override suspend fun doWork(): Result {
        return try {
            try {
                networkBrowserTask()
                Result.success()
            } catch (e: Exception) {
                Log.d(TAG, "exception in doWork ${e.message}")
                Result.failure()
            }
        } catch (e: Exception) {
            Log.d(TAG, "exception in doWork ${e.message}")
            Result.failure()
        }
    }

    private fun networkBrowserTask(): Disposable? {
        Log.i(TAG, "Entering networkBrowserTask()")
        val mRxDnssd: Rx2DnssdEmbedded? = App.getRxDnssd()
        val mDisposable: Disposable? = mRxDnssd!!.browse(
            AppConstants.SERVICE_TYPE,
            AppConstants.DOMAIN_NAME,
        )
            .compose(mRxDnssd.resolve())
            .compose(mRxDnssd.queryIPRecords())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ service: BonjourService ->
                if (!service.isLost && service.regType.startsWith("_http") && service.inet4Address != null) {
                    Log.i(TAG, "App IP New: " + service.inet4Address.toString())
                    Log.i(TAG, "App Nsd Service: $service")
                    val hostname: String = if (service.hostname == null) "Not known" else service.hostname!!
                    hostViewModel.updateHostList(HostRecord(service.serviceName, hostname, service.inet4Address.toString()))
                } else {
                    Log.i(TAG, "Other Nsd Service: $service")
                }
            },
                { throwable: Throwable? ->
                    Log.e(TAG, "Error: ", throwable)
                })
        Log.i(TAG, "Leaving networkBrowserTask()")

        Log.e("mDisposable", mDisposable.toString())
        return mDisposable
    }
}


/*

@Composable
fun Tab1(
    model: HostViewModel
) {
    Column {
        val items by model.itemLiveData.observeAsState()
        Button(
            modifier = Modifier.padding(all = 8.dp),
            onClick = {
                items?.clear()
                App.startNsdWorker()
            },
        ) { Text("Scan") }
        items?.let { ListOfHosts(it) }
    }
}


@Composable

fun HostCard(host: HostRecord) {
    Row(modifier = Modifier.padding(all = 8.dp)) {
        Column() {
            Text(
                text = host.serviceName,
                color = MaterialTheme.colors.primary,
                style = MaterialTheme.typography.subtitle1
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = host.hostName,
                color = MaterialTheme.colors.secondaryVariant,
                style = MaterialTheme.typography.subtitle2
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = host.ip4addr,
                color = MaterialTheme.colors.secondaryVariant,
                style = MaterialTheme.typography.subtitle2
            )

            Log.e("host.ip4addr", host.ip4addr)

        }
    }
}

@Composable
fun ListOfHosts(hostList: SnapshotStateList<HostRecord>) {
    var state by remember { mutableStateOf(0) }
    LazyColumn {
        hostList.map { item { HostCard(it) } }
    }
}

*/


