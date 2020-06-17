package com.es.arcoretest.manager

import android.content.Context
import android.graphics.Bitmap
import androidx.core.graphics.drawable.toBitmap
import com.es.arcoretest.R
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions

class ClothesManager(context: Context) {

    lateinit var clothesBitmap: Bitmap
    private val options =
        FirebaseVisionFaceDetectorOptions.Builder()
            .setPerformanceMode(FirebaseVisionFaceDetectorOptions.FAST)
//                .setContourMode(FirebaseVisionFaceDetectorOptions.ALL_CONTOURS)
            .setMinFaceSize(0f)
            .build()

    private val detector =
        FirebaseVision.getInstance().getVisionFaceDetector(options)

    init {
        context.resources.getDrawable(R.drawable.origin_clothes).run {
            clothesBitmap = this.toBitmap() // width, height, config
        }
    }

    fun processImage() {


    }

}