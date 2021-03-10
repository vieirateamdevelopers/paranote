package br.com.vieirateam.paranote.util

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.res.ResourcesCompat
import br.com.vieirateam.paranote.R
import br.com.vieirateam.paranote.bottomsheet.DrawBottomSheet
import kotlin.math.abs

class DrawUtil(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private var positionX: Float = 0f
    private var positionY: Float = 0f
    private var mPaint: Paint = Paint()
    private var mPath: Path = Path()

    private lateinit var mDrawBottomSheet: DrawBottomSheet
    private var mPaths: MutableList<Path> = mutableListOf()
    private var mPathsTemp: MutableList<Path> = mutableListOf()

    init {
        mPaint.isAntiAlias = true
        mPaint.color = getBrushColor()
        mPaint.strokeJoin = Paint.Join.ROUND
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeWidth = ConstantsUtil.STROKE_WIDTH
    }

    private fun getBrushColor(): Int {
        return if (UserPreferenceUtil.darkMode){
            ResourcesCompat.getColor(resources, R.color.colorLightMode, null)
        } else {
            ResourcesCompat.getColor(resources, R.color.colorDarkMode, null)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (path in mPaths) {
            canvas.drawPath(path, mPaint)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val positionX = event.x
        val positionY = event.y

        return when(event.action) {
            MotionEvent.ACTION_DOWN -> {
                touchStart(positionX, positionY)
                invalidate()
                true
            }
            MotionEvent.ACTION_UP -> {
                touchUp()
                invalidate()
                true
            }
            MotionEvent.ACTION_MOVE -> {
                touchMove(positionX, positionY)
                invalidate()
                true
            }
            else -> {
                false
            }
        }
    }

    private fun touchStart(positionX: Float, positionY: Float) {
        mPath = Path()
        mPaths.add(mPath)
        mPath.reset()
        mPath.moveTo(positionX, positionY)
        this.positionX = positionX
        this.positionY = positionY
    }

    private fun touchMove(positionX: Float, positionY: Float) {
        val x = abs(x - this.positionX)
        val y = abs(y - this.positionY)

        if (x >= ConstantsUtil.TOUCH_TOLERANCE || y >= ConstantsUtil.TOUCH_TOLERANCE) {
            mPath.quadTo(this.positionX, this.positionY, (positionX + this.positionX) / 2, (positionY + this.positionY) /2)
            this.positionX = positionX
            this.positionY = positionY
            mDrawBottomSheet.lockBottomSheet(false)
        }
    }

    private fun touchUp() {
        mDrawBottomSheet.lockBottomSheet(true)
        mPath.lineTo(this.positionX, this.positionY)
    }

    fun getBitmap(): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        draw(canvas)
        return bitmap
    }

    fun setDrawBottomSheet(drawBottomSheet: DrawBottomSheet) {
        mDrawBottomSheet = drawBottomSheet
    }

    fun clear() {
        mPaths.clear()
        invalidate()
    }

    fun undo() {
        if (mPaths.size > 0) {
            val position = mPaths.size - 1
            mPathsTemp.add(mPaths.removeAt(position))
            invalidate()
        }
    }

    fun redo() {
        if (mPathsTemp.size > 0) {
            val position = mPathsTemp.size - 1
            mPaths.add(mPathsTemp.removeAt(position))
            invalidate()
        }
    }
}