package br.com.vieirateam.paranote.util

import android.annotation.SuppressLint
import android.util.Log
import android.util.Size
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import br.com.vieirateam.paranote.R
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import kotlinx.android.synthetic.main.bottom_sheet_camera.view.*
import kotlinx.android.synthetic.main.bottom_sheet_toolbar.view.*
import java.lang.Exception
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraUtil(private val context: AppCompatActivity,
                 private val view: View,
                 private val bottomSheet: BottomSheet.Builder): ImageAnalysis.Analyzer {

    private lateinit var mExecutorService: ExecutorService
    private lateinit var mProcessCameraProvider: ProcessCameraProvider
    private lateinit var mCameraInfo: CameraInfo
    private lateinit var mCameraControl: CameraControl
    private lateinit var mImageAnalysis: ImageAnalysis

    private var resultText: String? = null
    private val previewView = view.camera_view
    private val recognizer = TextRecognition.getClient()
    private val mCameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

    fun startCamera() {
        mExecutorService = Executors.newSingleThreadExecutor()
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            mProcessCameraProvider = cameraProviderFuture.get()
            mImageAnalysis = ImageAnalysis.Builder()
                .setTargetResolution(Size(1280, 720))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
            mImageAnalysis.setAnalyzer(mExecutorService, this)
            configureCamera()
        }, ContextCompat.getMainExecutor(context))
    }

    @SuppressLint("UnsafeExperimentalUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        imageProxy.image?.let { image ->
            val inputImage = InputImage.fromMediaImage(image, imageProxy.imageInfo.rotationDegrees)
            recognizer.process(inputImage).addOnSuccessListener { result ->
                resultText = result.text
                Log.d(ConstantsUtil.TAG, resultText.toString())
            }
            .addOnFailureListener { exception ->
                resultText = null
                Log.d(ConstantsUtil.TAG, exception.toString())
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
        } ?: imageProxy.close()
    }

    private fun configureCamera() {
        val preview = Preview.Builder()
            .setTargetRotation(previewView.display.rotation)
            .build()
        try {
            mProcessCameraProvider.unbindAll()
            mProcessCameraProvider.bindToLifecycle(context, mCameraSelector, preview, mImageAnalysis).apply {
                mCameraInfo = cameraInfo
                mCameraControl = cameraControl
                configureZoom()
                configureFlash()
            }
            preview.setSurfaceProvider(previewView.surfaceProvider)
        } catch (exception: Exception) {
            Log.d(ConstantsUtil.TAG, exception.toString())
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun configureZoom() {
        val scaleGestureDetector = ScaleGestureDetector(context, zoomListener)
        previewView.setOnTouchListener { _, event ->
            bottomSheet.setLock(false)
            scaleGestureDetector.onTouchEvent(event)
            if (event.action == MotionEvent.ACTION_UP){
                bottomSheet.setLock(true)
            }
            return@setOnTouchListener true
        }
    }

    private fun configureFlash() {
        mCameraInfo.torchState.observe(context) { state ->
            if (state == TorchState.OFF) {
                view.image_view_send.setImageResource(R.drawable.ic_drawable_flash_off)
            } else {
                view.image_view_send.setImageResource(R.drawable.ic_drawable_flash_on)
            }
        }
    }

    private var zoomListener = object: ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            val currentZoomRatio = mCameraInfo.zoomState.value?.zoomRatio ?: 0F
            val delta = detector.scaleFactor
            mCameraControl.setZoomRatio(currentZoomRatio * delta)
            return super.onScale(detector)
        }
    }

    fun setFlash() {
        if(mCameraInfo.hasFlashUnit()) {
            mCameraControl.enableTorch(mCameraInfo.torchState.value == TorchState.OFF)
        }
    }

    @SuppressLint("RestrictedApi")
    fun shutdownCamera() {
        mImageAnalysis.clearAnalyzer()
        mProcessCameraProvider.unbindAll()
    }

    fun shutdownFlash() {
        if (mCameraInfo.hasFlashUnit()) {
            mCameraControl.enableTorch(false)
        }
    }

    fun getResultText(): String? = resultText
}