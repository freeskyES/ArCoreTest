package com.es.arcoretest.manager

import android.content.Context
import android.graphics.Bitmap
import androidx.core.graphics.drawable.toBitmap
import com.es.arcoretest.R
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import kotlinx.coroutines.coroutineScope
import timber.log.Timber
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

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
            Timber.i("clothesBitmap size ${clothesBitmap.width} / ${clothesBitmap.height}")
        }
    }

    suspend fun processImage(): Bitmap = coroutineScope {
        detectFace(clothesBitmap).let {faces ->
            // 성공 & 얼굴 정보 얻기
            Timber.i("detectFace result $faces")

            getFaceContourInfo(faces).let {faceInfo ->
                val editedImage = editClothesImage(clothesBitmap/*resizeMask*//*resizedBitmap*/, faceInfo)

                Timber.i("end")
                return@coroutineScope editedImage
            }

        }
    }

    private fun editClothesImage(
        resizedBitmap: Bitmap,
        faceContourInfo: FaceDetectInfo
    ): Bitmap {

        val splitResult = splitImage(resizedBitmap, faceContourInfo)

        return  splitResult
    }

    private fun splitImage(targetBitmap: Bitmap, faceContourInfo: FaceDetectInfo): Bitmap {

        return Bitmap.createBitmap(
            targetBitmap,
            0,
            faceContourInfo.chinBottomPos.py.toInt(),
            targetBitmap.width,
            targetBitmap.height - faceContourInfo.chinBottomPos.py.toInt()
        )
    }

    private suspend fun getFaceContourInfo(faces: List<FirebaseVisionFace>) = suspendCoroutine<FaceDetectInfo>{ cont ->

        for (i in faces.indices) {
            faces[i].let {face->
                val x = face.boundingBox.centerX().toFloat()
                val y = face.boundingBox.centerY().toFloat()

                val xOffset = face.boundingBox.width() / 2.0f
                val yOffset = face.boundingBox.height() / 2.0f
                val left = x - xOffset
                val top = y - yOffset
                val right = x + xOffset
                val bottom = y + yOffset

                var chinBottomPos = FaceContourData(x, bottom)

                FaceDetectInfo(left, top, right-left, bottom-top, x, y, chinBottomPos).run {
                    Timber.i("$this")
                    cont.resume(this)
                }
            }
        }
    }

    private suspend fun detectFace(resizedBitmap: Bitmap) = suspendCoroutine<List<FirebaseVisionFace>> { cont ->

        try {

            FirebaseVisionImage.fromBitmap(resizedBitmap).let {firebaseVisionImage ->

                detector.detectInImage(firebaseVisionImage)
                    .addOnSuccessListener { faces ->

                        if (faces.isNotEmpty()) {
                            cont.resume(faces)
                        } else {
                            cont.resumeWithException(java.lang.Exception("face not found"))
                        }
                    }
                    .addOnFailureListener { e ->
                        // Task failed with an exception
                        e.printStackTrace()
                        cont.resumeWithException(e)
                    }

            }
        } catch (e: Exception) {
            e.printStackTrace()
            cont.resumeWithException(e)
        }
    }

    data class FaceContourData(val px: Float,
                               val py: Float)

    data class FaceDetectInfo(val left: Float,
                              val top: Float,
                              val rectWidth: Float,
                              val rectHeight: Float,
                              val centerPx: Float,
                              val centerPy: Float,
                              val chinBottomPos: FaceContourData
    )
}