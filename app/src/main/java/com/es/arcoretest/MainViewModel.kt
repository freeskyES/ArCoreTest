package com.es.arcoretest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.es.arcoretest.manager.ClothesManager
import com.es.arcoretest.util.wrapEspressoIdlingResource
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import timber.log.Timber

class MainViewModel : ViewModel() {

    private lateinit var clothesManager: ClothesManager

    fun setupClothesManager(clothesManager: ClothesManager) {
        this.clothesManager = clothesManager
    }

    fun setupClothes() {
        wrapEspressoIdlingResource {
            viewModelScope.launch {
                val result = async { clothesManager.processImage() }.await()

                Timber.i("result !")
            }
        }
    }

}