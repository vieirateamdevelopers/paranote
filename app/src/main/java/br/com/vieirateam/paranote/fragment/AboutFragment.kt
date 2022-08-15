package br.com.vieirateam.paranote.fragment

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import br.com.vieirateam.paranote.BuildConfig
import br.com.vieirateam.paranote.R
import br.com.vieirateam.paranote.util.BottomSheet
import br.com.vieirateam.paranote.util.ConstantsUtil
import br.com.vieirateam.paranote.util.InputEditTextUtil
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.bottom_sheet_text.view.*
import kotlinx.android.synthetic.main.fragment_about.*

class AboutFragment : GenericFragment(R.layout.fragment_about), BottomSheet.Callback {

    private var feedbackText: String? = null

    override fun posActivityCreated(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            showBottom = savedInstanceState.getString(ConstantsUtil.BOTTOM_SHEET).toString()
            feedbackText = savedInstanceState.getString(ConstantsUtil.BODY)
            if (showBottom == "feedback") {
                addOnClickListener()
            }
        }
        configureFields()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(ConstantsUtil.BODY, feedbackText)
        outState.putString(ConstantsUtil.BOTTOM_SHEET, showBottom)
    }

    override fun onBackPressed() {
        showBottom = null
        feedbackText = null
        mInputEditTextTitle = null
    }

    override fun onClickListener(view: View) {
        when(view.id){
            R.id.image_view_undo -> {
                mInputEditTextTitle?.undo()
            }
            R.id.image_view_redo -> {
                mInputEditTextTitle?.redo()
            }
            R.id.image_view_clear -> {
                mInputEditTextTitle?.clear()
                feedbackText = ""
                updateUI()
            }
            R.id.image_view_send -> {
                val text = mInputEditTextTitle?.getText().toString()
                if (text.isNotEmpty()) {
                    val intent = Intent(Intent.ACTION_SENDTO)
                    intent.data = Uri.parse("mailto:")
                    intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.text_view_email_developers)))
                    intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.menu_feedback))
                    intent.putExtra(Intent.EXTRA_TEXT, text)
                    startIntentActivity(intent)
                }
                onBackPressed()
            }
        }
    }

    override fun addOnClickListener() {
        showBottom = "feedback"
        val builder = BottomSheet.Builder(mMainActivity, R.layout.bottom_sheet_text, this).apply {
            setTitle(getString(R.string.menu_feedback))
            setInflateMenus()
            setVisibleButtons(null)
            build()
        }
        mDialogView = builder.getBottomSheetView()
        mDialogView.bottom_sheet_text.minimumHeight = builder.getBottomSheetHeight()
        configureBottomSheet()
    }

    private fun configureBottomSheet() {
        mInputEditTextTitle = InputEditTextUtil(mDialogView.text_input_base_title)
        mInputEditTextTitle?.setHint(getString(R.string.text_view_feedback_desc))
        updateUI()
        mInputEditTextTitle?.requestFocus()
        mInputEditTextTitle?.show()
        configureInputEditTextListener()
    }

    private fun updateUI() {
        mInputEditTextTitle?.setText(feedbackText)
    }

    private fun configureInputEditTextListener() {
        mDialogView.text_input_base_title.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(editable: Editable?) { feedbackText = editable.toString() }
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun configureFields() {
        var intent : Intent
        text_view_version.text = getString(R.string.text_view_version, BuildConfig.VERSION_NAME)

        card_view_play_store.setOnClickListener {
            intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(getString(R.string.text_view_play_store_developers))
            startIntentActivity(intent)
        }

        card_view_site.setOnClickListener {
            intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(getString(R.string.text_view_site_developers))
            startIntentActivity(intent)
        }

        card_view_github.setOnClickListener {
            intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(getString(R.string.text_view_github_developers))
            startIntentActivity(intent)
        }

        about_scroll_view.setOnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
            if (scrollY > oldScrollY) {
                mFloatingActionButton.hide()
            } else {
                mFloatingActionButton.show()
            }
        }
    }

    private fun startIntentActivity(intent: Intent){
        try {
            startActivity(intent)
        } catch (exception: ActivityNotFoundException) {
            Log.d(ConstantsUtil.TAG, exception.toString())
        }
    }

    override fun onStartTextRecognition(dialog: BottomSheetDialog) {}

    override fun onFinishTextRecognition() {}
}