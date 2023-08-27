package com.atb.mdns_resolve.ui.activities

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.atb.mdns_resolve.App
import com.atb.mdns_resolve.databinding.MainActivityBinding
import com.atb.mdns_resolve.model.HostViewModel
import kotlinx.coroutines.launch

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonReload.setOnClickListener {
            App.startNsdWorker()
        }

        lifecycleScope.launch {
        }

        viewModel.viewModelScope.launch {
        }

    }
}



