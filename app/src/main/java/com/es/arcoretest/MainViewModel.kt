package com.es.arcoretest

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.es.arcoretest.manager.ClothesManager
import com.es.arcoretest.util.wrapEspressoIdlingResource
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import timber.log.Timber

class MainViewModel : ViewModel() {

    private val _processedImage = MutableLiveData<Bitmap>()
    val processedImage: LiveData<Bitmap> = _processedImage

    private lateinit var clothesManager: ClothesManager

    fun setupClothesManager(clothesManager: ClothesManager) {
        this.clothesManager = clothesManager
    }

    fun setupClothes() {
        wrapEspressoIdlingResource {
            viewModelScope.launch {
                val result = async { clothesManager.processImage() }.await()

                _processedImage.value = result
                Timber.i("result !")
            }
        }
    }



}