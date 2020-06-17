package com.es.arcoretest.util

import android.graphics.Bitmap
import android.util.Pair
import android.util.Size
import timber.log.Timber
import kotlin.math.max

object BitmapUtils {

    fun resizeImage(targetSize: Size, selectBitmap: Bitmap): Bitmap {
        Timber.i("resizeImage ${selectBitmap.width} / ${selectBitmap.height}  targetSize $targetSize")
        val targetedSize: Pair<Int, Int> = Pair(targetSize.width, targetSize.height)
        val targetWidth = targetedSize.first
        val maxHeight = targetedSize.second
        // Determine how much to scale down the image
        val scaleFactor = max(
            targetSize.width.toFloat() / targetWidth.toFloat(),
            targetSize.height.toFloat() / maxHeight.toFloat()
        )
        return Bitmap.createScaledBitmap(
            selectBitmap,
            (targetSize.width / scaleFactor).toInt(),
            (targetSize.height / scaleFactor).toInt(),
            true
        )
    }

}