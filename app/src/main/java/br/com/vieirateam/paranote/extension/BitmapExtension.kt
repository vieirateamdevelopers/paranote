package br.com.vieirateam.paranote.extension

import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.widget.ImageView

fun Bitmap.rotateBitmap(): Bitmap {
    val matrix = Matrix().apply { postRotate(90f) }
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}

fun ImageView.getBitmap(): Bitmap {
    val bitmapDrawable = drawable as BitmapDrawable
    return bitmapDrawable.bitmap
}