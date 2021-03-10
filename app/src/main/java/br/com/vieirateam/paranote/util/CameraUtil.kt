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
import androidx.core.content.ContextCompat
import br.com.vieirateam.paranote.R
import br.com.vieirateam.paranote.bottomsheet.CameraBottomSheet
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import kotlinx.android.synthetic.main.bottom_sheet_camera.view.camera_view
import kotlinx.android.synthetic.main.bottom_sheet_toolbar.view.image_view_base_send
import java.lang.Exception
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraUtil(private val context: AppCompatActivity,
                 private val view: View): ImageAnalysis.Analyzer {

    private val recognizer = TextRecognition.getClient()
    private lateinit var executorService: ExecutorService
    private lateinit var cameraProvider: ProcessCameraProvider
    private lateinit var mCameraInfo: CameraInfo
    private lateinit var mCameraControl: CameraControl
    private lateinit var mCameraBottomSheet: CameraBottomSheet
    private lateinit var mImageAnalysis: ImageAnalysis
    private var resultText: String? = null
    private val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private val previewView = view.camera_view

    fun show() {
        executorService = Executors.newSingleThreadExecutor()
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()
            mImageAnalysis = ImageAnalysis.Builder()
                .setTargetResolution(Size(1280, 720))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
            mImageAnalysis.setAnalyzer(executorService, this)
            configureCamera()
        }, ContextCompat.getMainExecutor(context))
    }

    private fun configureCamera() {
        val preview = Preview.Builder()
            .setTargetRotation(previewView.display.rotation)
            .build()
        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(context, cameraSelector, preview, mImageAnalysis).apply {
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

    @SuppressLint("ClickableViewAccessibility")
    private fun configureZoom() {
        val scaleGestureDetector = ScaleGestureDetector(context, zoomListener)
        previewView.setOnTouchListener { _, event ->
            mCameraBottomSheet.lockBottomSheet(false)
            scaleGestureDetector.onTouchEvent(event)
            if (event.action == MotionEvent.ACTION_UP){
                mCameraBottomSheet.lockBottomSheet(true)
            }
            return@setOnTouchListener true
        }
    }

    private fun configureFlash() {
        mCameraInfo.torchState.observe(context) { state ->
            if (state == TorchState.OFF) {
                view.image_view_base_send.setImageResource(R.drawable.ic_drawable_flash_off)
            } else {
                view.image_view_base_send.setImageResource(R.drawable.ic_drawable_flash_on)
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
        cameraProvider.unbindAll()
    }

    fun shutdownFlash() {
        if(mCameraInfo.hasFlashUnit()) {
            mCameraControl.enableTorch(false)
        }
    }

    fun setCameraBottomSheet(cameraBottomSheet: CameraBottomSheet) {
        this.mCameraBottomSheet = cameraBottomSheet
    }

    fun getResultText(): String? = resultText
}