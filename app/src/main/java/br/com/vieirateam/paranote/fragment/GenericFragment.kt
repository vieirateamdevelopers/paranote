package br.com.vieirateam.paranote.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import br.com.vieirateam.paranote.R
import br.com.vieirateam.paranote.activity.MainActivity
import br.com.vieirateam.paranote.util.ConstantsUtil
import br.com.vieirateam.paranote.util.InputEditTextUtil
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.miguelcatalan.materialsearchview.MaterialSearchView
import kotlinx.android.synthetic.main.adapter_app_bar.*
import kotlinx.android.synthetic.main.adapter_main_content.*

abstract class GenericFragment(private val layoutID: Int) : Fragment() {

    var showBottom: String? = null
    lateinit var mDialogView: View
    protected var fragmentTAG : String = ""
    protected lateinit var mView: View
    protected lateinit var mDialogViewTemp: View
    protected var mInputEditTextTitle: InputEditTextUtil? = null
    protected var mInputEditTextBody: InputEditTextUtil? = null
    protected lateinit var mMaterialSearchView: MaterialSearchView
    protected lateinit var mFloatingActionButton: FloatingActionButton
    protected lateinit var mMaterialSearchViewVoice: AppCompatImageView
    protected lateinit var mMainActivity: MainActivity
    protected var mBundle = Bundle()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(layoutID, container, false)
        setHasOptionsMenu(true)
        return mView
    }

    override fun onPause() {
        super.onPause()
        if(::mDialogView.isInitialized) {
            when(showBottom) {
                "keyboard" -> {
                    mInputEditTextTitle?.hide()
                    mInputEditTextBody?.hide()
                }
                "feedback" -> {
                    mInputEditTextBody?.hide()
                }
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        fragmentTAG = arguments?.getString(ConstantsUtil.FRAGMENT).toString()
        mMainActivity = (activity as MainActivity)
        mMaterialSearchView = mMainActivity.getMaterialSearchView()
        mMaterialSearchViewVoice = mMainActivity.getAppCompatImageViewVoice()
        mFloatingActionButton = mMainActivity.floating_button

        configureListeners()
        configureFloatingButton()
        posActivityCreated(savedInstanceState)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun configureListeners() {
        mFloatingActionButton.setOnClickListener {
            if (!mMaterialSearchView.isSearchOpen) {
                addOnClickListener()
            }
        }

        mFloatingActionButton.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                if (!mMaterialSearchView.isSearchOpen) {
                    addOnClickListener()
                    return@setOnTouchListener true
                } else {
                    return@setOnTouchListener false
                }
            }
            return@setOnTouchListener false
        }
    }

    private fun configureFloatingButton() {
        when(fragmentTAG) {
            "favorite" -> {
                setImageResource(false, R.drawable.ic_drawable_add)
            }
            "home" -> {
                setImageResource(false, R.drawable.ic_drawable_add)
            }
            "reminder" -> {
                setImageResource(true, R.drawable.ic_drawable_add)
            }
            "archive" -> {
                setImageResource(true, R.drawable.ic_drawable_add)
            }
            "settings" -> {
                hideMessage()
                setImageResource(true, R.drawable.ic_drawable_add)
            }
            "about" -> {
                hideMessage()
                setImageResource(false, R.drawable.ic_drawable_feedback)
            }
        }
    }

    private fun setImageResource(hide: Boolean, resourceID: Int) {
        mFloatingActionButton.setImageResource(resourceID)
        hideFloatingButton(hide)
    }

    protected fun hideMessage() {
        mMainActivity.constraint_layout_status.visibility = View.INVISIBLE
    }

    fun hideFloatingButton(hide: Boolean) {
        if (hide) {
            mFloatingActionButton.hide()
        } else {
            mFloatingActionButton.show()
        }
    }

    abstract fun posActivityCreated(savedInstanceState: Bundle?)

    abstract fun addOnClickListener()
}