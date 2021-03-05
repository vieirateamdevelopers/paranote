package br.com.vieirateam.paranote.util

import android.annotation.SuppressLint
import android.util.Log
import android.util.SparseArray
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.text.TextBlock
import com.google.android.gms.vision.text.TextRecognizer
import java.io.IOException
import java.lang.StringBuilder

class CameraUtil(private val context: AppCompatActivity, private val surfaceView: SurfaceView) : SurfaceHolder.Callback, Detector.Processor<TextBlock> {

    private lateinit var mCameraSource: CameraSource
    private lateinit var mTextRecognizer: TextRecognizer
    private lateinit var mStringBuilder: StringBuilder

    @SuppressLint("MissingPermission")
    override fun surfaceCreated(holder: SurfaceHolder?) {
        try {
            if(::mCameraSource.isInitialized) mCameraSource.start(surfaceView.holder)
        } catch (exception: IOException) {
            Log.d(ConstantsUtil.TAG, exception.toString())
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {}

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        if(::mTextRecognizer.isInitialized) mTextRecognizer.release()
        if(::mCameraSource.isInitialized) mCameraSource.stop()
    }

    override fun receiveDetections(detector: Detector.Detections<TextBlock>?) {
        val items : SparseArray<TextBlock> = detector?.detectedItems as SparseArray<TextBlock>
        mStringBuilder = StringBuilder()
        if (items.size() != 0) {
            for(i in 0 until items.size()) {
                val item = items.valueAt(i)
                mStringBuilder.append(item.value)
                mStringBuilder.append("\n")
            }
        }
    }

    fun show() {
        mTextRecognizer = TextRecognizer.Builder(context).build()
        if (mTextRecognizer.isOperational) {
            mCameraSource = CameraSource.Builder(context, mTextRecognizer)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setAutoFocusEnabled(true)
                .build()
        }
        mTextRecognizer.setProcessor(this)
        surfaceView.holder.addCallback(this)
    }

    fun getContentFromCamera(): String {
        if(::mTextRecognizer.isInitialized) mTextRecognizer.release()
        if(::mCameraSource.isInitialized) mCameraSource.stop()
        return if(::mStringBuilder.isInitialized) {
            mStringBuilder.toString()
        } else {
            ""
        }
    }

    override fun release() {}
}