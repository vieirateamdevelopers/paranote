package br.com.vieirateam.paranote.fragment

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import br.com.vieirateam.paranote.BuildConfig
import br.com.vieirateam.paranote.R
import br.com.vieirateam.paranote.bottomsheet.BaseBottomSheet
import br.com.vieirateam.paranote.bottomsheet.NoteBottomSheet
import br.com.vieirateam.paranote.util.ConstantsUtil
import br.com.vieirateam.paranote.util.KeyboardUtil
import kotlinx.android.synthetic.main.bottom_sheet_base.view.text_input_base_title
import kotlinx.android.synthetic.main.bottom_sheet_base.view.text_input_base_body
import kotlinx.android.synthetic.main.bottom_sheet_base.view.linear_layout_base
import kotlinx.android.synthetic.main.fragment_about.text_view_version
import kotlinx.android.synthetic.main.fragment_about.card_view_github
import kotlinx.android.synthetic.main.fragment_about.card_view_site
import kotlinx.android.synthetic.main.fragment_about.card_view_play_store

class AboutFragment : GenericFragment(R.layout.fragment_about), BaseBottomSheet.Callback {

    private var feedbackText: String? = null

    override fun posActivityCreated(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            showBottom = savedInstanceState.getString(ConstantsUtil.BOTTOM_SHEET)
            feedbackText = savedInstanceState.getString(ConstantsUtil.BODY)
            if (showBottom == "feedback") {
                addOnClickListener()
            }
        }
        configureFields()
    }

    override fun addOnClickListener() {
        mBottomSheetListener = NoteBottomSheet(mMainActivity, this, null)
        mBottomSheetListener.build()
        showBottom = "feedback"
        mDialogView = mBottomSheetListener.getBottomSheetView()
        configureBottomSheet()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(ConstantsUtil.BODY, feedbackText)
        outState.putString(ConstantsUtil.BOTTOM_SHEET, showBottom)
    }

    override fun onBottomSheetBackPressed() {
        showBottom = null
    }

    override fun setOnBottomSheetClickListener(buttonID: String) {
        when(buttonID) {
            "clear" -> {
                feedbackText = ""
                updateUI()
            }
            "send" -> {
                showBottom = null
                val text = mDialogView.text_input_base_body.text?.trim().toString()
                if (text.isNotEmpty()) {
                    val intent = Intent(Intent.ACTION_SENDTO)
                    intent.data = Uri.parse("mailto:")
                    intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.text_view_email_developers)))
                    intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.menu_feedback))
                    intent.putExtra(Intent.EXTRA_TEXT, text)
                    startIntentActivity(intent)
                }
            }
        }
    }

    private fun configureBottomSheet() {
        mDialogView.linear_layout_base.removeView(mDialogView.text_input_base_title)
        mBottomSheetListener.setTitle(getString(R.string.menu_feedback), 2)
        mDialogView.text_input_base_body.hint = getString(R.string.text_view_feedback_desc)
        updateUI()
        KeyboardUtil.show(mDialogView.text_input_base_body)

        mDialogView.text_input_base_body.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(editable: Editable?) {
                feedbackText = editable.toString()
            }
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun updateUI() {
        mDialogView.text_input_base_body.setText(feedbackText)
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
    }

    private fun startIntentActivity(intent: Intent){
        try {
            startActivity(intent)
        } catch (exception: ActivityNotFoundException) {
            Log.d(ConstantsUtil.TAG, exception.toString())
        }
    }
}