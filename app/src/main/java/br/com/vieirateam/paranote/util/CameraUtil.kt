package br.com.vieirateam.paranote.util

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import br.com.vieirateam.paranote.R
import br.com.vieirateam.paranote.bottomsheet.CameraBottomSheet
import kotlinx.android.synthetic.main.bottom_sheet_camera.view.camera_view
import kotlinx.android.synthetic.main.bottom_sheet_toolbar.view.image_view_base_send
import java.io.File
import java.lang.Exception
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraUtil(private val context: AppCompatActivity, private val view: View) {

    private lateinit var executorService: ExecutorService
    private lateinit var cameraProvider: ProcessCameraProvider
    private lateinit var mCameraInfo: CameraInfo
    private lateinit var mCameraControl: CameraControl
    private lateinit var mCameraBottomSheet: CameraBottomSheet
    private var imageResult: Uri? = null
    private var imageCapture: ImageCapture? = null
    private val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private val previewView = view.camera_view

    fun show() {
        executorService = Executors.newSingleThreadExecutor()
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()
            imageCapture = ImageCapture.Builder()
                .setTargetRotation(previewView.display.rotation)
                .build()
            configureCamera()
        }, ContextCompat.getMainExecutor(context))
    }

    fun takePhoto() {
        val imageCapture = this.imageCapture ?: return
        val file = FileUtil.getImageCapturePath()
        if (file != null) {
            val currentTimeMillis = System.currentTimeMillis()
            val photo = File("$file$currentTimeMillis.jpg")
            val outputOptions = ImageCapture.OutputFileOptions.Builder(photo).build()

            imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(context), object: ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    imageResult = Uri.fromFile(photo)
                }

                override fun onError(exception: ImageCaptureException) {
                    val message = context.getString(R.string.text_view_capture_error)
                    ToastUtil.show(context, message)
                    Log.d(ConstantsUtil.TAG, exception.toString())
                }
            })
        }
    }

    private fun configureCamera() {
        val preview = Preview.Builder()
            .setTargetRotation(previewView.display.rotation)
            .build()
        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(context, cameraSelector, preview, imageCapture).apply {
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

    fun shutdownFlash() {
        if(mCameraInfo.hasFlashUnit()) {
            mCameraControl.enableTorch(false)
        }
    }

    fun setCameraBottomSheet(cameraBottomSheet: CameraBottomSheet) {
        this.mCameraBottomSheet = cameraBottomSheet
    }

    fun getImageResult(): Uri? = imageResult
}