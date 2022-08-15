package br.com.vieirateam.paranote.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import br.com.vieirateam.paranote.NoteApplication
import br.com.vieirateam.paranote.R
import br.com.vieirateam.paranote.adapter.NoteAdapter
import br.com.vieirateam.paranote.entity.Note
import br.com.vieirateam.paranote.extension.getBitmap
import br.com.vieirateam.paranote.extension.requestPermission
import br.com.vieirateam.paranote.extension.rotateBitmap
import br.com.vieirateam.paranote.util.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.adapter_app_bar.*
import kotlinx.android.synthetic.main.adapter_card_view.view.*
import kotlinx.android.synthetic.main.bottom_sheet_camera.view.*
import kotlinx.android.synthetic.main.bottom_sheet_color_dark.view.*
import kotlinx.android.synthetic.main.bottom_sheet_color_light.view.*
import kotlinx.android.synthetic.main.bottom_sheet_confirm.view.*
import kotlinx.android.synthetic.main.bottom_sheet_draw.view.*
import kotlinx.android.synthetic.main.bottom_sheet_image.view.*
import kotlinx.android.synthetic.main.bottom_sheet_options.view.*
import kotlinx.android.synthetic.main.bottom_sheet_reminder.view.*
import kotlinx.android.synthetic.main.bottom_sheet_text.view.*
import kotlinx.android.synthetic.main.bottom_sheet_toolbar.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.Serializable

abstract class BaseNoteFragment : BaseFragment<NoteAdapter, Note>(), BottomSheet.Callback {

    lateinit var note: Note
    lateinit var adapter: NoteAdapter

    private var save = false
    private var home = false
    private var editTitle = false
    private var mReminderUtil: ReminderUtil? = null

    private lateinit var mBitmap: Bitmap
    private lateinit var mDrawUtil: DrawUtil
    private lateinit var mVoiceUtil: VoiceUtil
    private lateinit var mCameraUtil: CameraUtil
    private lateinit var mTextAnalyzerUtil: TextAnalyzerUtil

    override fun addOnClickListener() {
        home = true
        showBottom = "options"
        val builder = BottomSheet.Builder(mMainActivity, R.layout.bottom_sheet_options, this).apply {
            setOptions(home)
            build()
        }
        mDialogViewTemp = builder.getBottomSheetView()
        configureFloatingButtonOptions(builder)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mTextAnalyzerUtil = TextAnalyzerUtil(mMainActivity)
        if (savedInstanceState == null) {
            mBundle = Bundle()
        } else {
            mBundle = savedInstanceState
            val item = savedInstanceState.getSerializable(ConstantsUtil.ITEM)
            if (item != null) note = item as Note
            home = savedInstanceState.getBoolean(ConstantsUtil.HOME)
            save = savedInstanceState.getBoolean(ConstantsUtil.SAVE)
            voiceSearch = savedInstanceState.getBoolean(ConstantsUtil.VOICE)
            editTitle = savedInstanceState.getBoolean(ConstantsUtil.EDIT_TITLE)
            showBottom = savedInstanceState.getString(ConstantsUtil.BOTTOM_SHEET)
            configureShowBottomSheet()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(ConstantsUtil.SAVE, save)
        outState.putBoolean(ConstantsUtil.HOME, home)
        outState.putBoolean(ConstantsUtil.VOICE, voiceSearch)
        outState.putBoolean(ConstantsUtil.EDIT_TITLE, editTitle)
        outState.putString(ConstantsUtil.BOTTOM_SHEET, showBottom)
        if (::note.isInitialized) { outState.putSerializable(ConstantsUtil.ITEM, note) }
    }

    private fun configureShowBottomSheet() {
        when(showBottom) {
            "camera" -> {
                configureBottomSheetCamera()
            }
            "image" -> {
                configureBottomSheetImage(mBitmap)
            }
            "draw" -> {
                configureBottomSheetDraw()
            }
            "voice" -> {
                configureBottomSheetVoice()
            }
            "keyboard" -> {
                configureBottomSheetText(note, save)
            }
            "options" -> {
                addOnClickListener()
            }
            "confirm" -> {
                configureBottomSheetConfirm()
            }
            "color" -> {
                configureBottomSheetText(note, save)
                configureBottomSheetColor()
            }
            "reminder" -> {
                configureBottomSheetText(note, save)
                configureBottomSheetReminder()
            }
            "reminder_options" -> {
                configureBottomSheetText(note, save)
                configureBottomSheetOptions()
            }
            else -> {
                showBottom = null
            }
        }
    }

    override fun onBackPressed() {
        when(showBottom) {
            "camera" -> {
                mCameraUtil.shutdownFlash()
                mCameraUtil.shutdownCamera()
                showBottom = null
            }
            "image" -> {
                showBottom = null
            }
            "draw" -> {
                showBottom = null
            }
            "voice" -> {
                mVoiceUtil.onStop()
                showBottom = null
            }
            "keyboard" -> {
                saveItem(note)
                showBottom = null
                mInputEditTextTitle = null
                mInputEditTextBody = null
            }
            "options" -> {
                showBottom = null
            }
            "confirm" -> {
                showBottom = null
            }
            "color" -> {
                showBottom = "keyboard"
            }
            "reminder" -> {
                showBottom = "keyboard"
                updateReminder()
            }
            "reminder_options" -> {
                showBottom = null
            }
            else -> {
                showBottom = null
            }
        }
    }

    override fun onStartTextRecognition(dialog: BottomSheetDialog) {
        when(showBottom) {
            "draw" -> {
                val bitmap = mDrawUtil.getBitmap()
                if(bitmap != null) {
                    mTextAnalyzerUtil.getText(this@BaseNoteFragment, bitmap, dialog)
                } else {
                    dialog.hide()
                }
            }
            "image" -> {
                val bitmap = mDialogView.image_view_image.getBitmap()
                mTextAnalyzerUtil.getText(this@BaseNoteFragment, bitmap, dialog)
            }
        }
    }

    override fun onFinishTextRecognition() {
        onBackPressed()
        note = Note()
        note.body = mTextAnalyzerUtil.resultText.toString()
        configureBottomSheetText(note, true)
    }

    override fun onClickListener(view: View) {
        when (view.id) {
            R.id.image_view_undo -> {
                when(showBottom) {
                    "draw" -> {
                        mDrawUtil.undo()
                    }
                    "keyboard" -> {
                        if (editTitle) {
                            mInputEditTextTitle?.undo()
                        } else {
                            mInputEditTextBody?.undo()
                        }
                    }
                }
            }
            R.id.image_view_redo -> {
                when(showBottom) {
                    "draw" -> {
                        mDrawUtil.redo()
                    }
                    "keyboard" -> {
                        if (editTitle) {
                            mInputEditTextTitle?.redo()
                        } else {
                            mInputEditTextBody?.redo()
                        }
                    }
                }
            }
            R.id.image_view_clear -> {
                when (showBottom) {
                    "draw" -> {
                        mDrawUtil.clear()
                    }
                    "image" -> {
                        val bitmap = mDialogView.image_view_image.getBitmap()
                        val bitmapRotate = bitmap.rotateBitmap()
                        mDialogView.image_view_image.setImageBitmap(bitmapRotate)
                    }
                    "keyboard" -> {
                        if (editTitle) {
                            mInputEditTextTitle?.clear()
                            note.title = ""
                        } else {
                            mInputEditTextBody?.clear()
                            note.body = ""
                        }
                        updateUI()
                    }
                }
            }
            R.id.image_view_send -> {
                when (showBottom) {
                    "camera" -> {
                        mCameraUtil.setFlash()
                    }
                    "keyboard" -> {
                        onBackPressed()
                    }
                }
            }
        }
    }

    override fun saveItem(item: Serializable) {
        note = item as Note
        note.title = mInputEditTextTitle?.getText().toString()
        note.body = mInputEditTextBody?.getText().toString()

        if (mReminderUtil != null) {
            note.reminder = (mReminderUtil as ReminderUtil).getReminder()
        }
        if (fragmentTAG == "favorite") {
            note.favorite = true
        }
        if (note.body.isEmpty()) {
            if (!save) {
                NotificationUtil.cancel(note)
                NoteApplication.noteViewModel.delete(note)
            }
        } else {
            if (save) {
                NoteApplication.noteViewModel.getScope().launch(Dispatchers.Main) {
                    val result = NoteApplication.noteViewModel.selectLastInsert()
                    if (result == null) {
                        UserPreferenceUtil.noteID = 1
                    } else {
                        UserPreferenceUtil.noteID = result.id
                        UserPreferenceUtil.noteID = UserPreferenceUtil.noteID + 1
                    }
                    note.id = UserPreferenceUtil.noteID
                    NoteApplication.noteViewModel.insert(note)
                    startReminderFragment()
                }
            } else {
                NoteApplication.noteViewModel.update(note)
                startReminderFragment()
            }
        }
        mReminderUtil = null
    }

    private fun startReminderFragment() {
        if (note.reminder.isChecked) {
            if (!save) NotificationUtil.cancel(note)
            NotificationUtil.create(mBundle, note)
            mMainActivity.startReminderFragment()
        } 
    }

    override fun onItemClick(item: Note, view: View) {
        if (mActionModeNote.selected) {
            mActionModeNote.selectedItem(item, view)
        } else {
            note = item
            if (fragmentTAG == "archive") {
                configureBottomSheetConfirm()
            } else{
                configureBottomSheetText(note, false)
            }
        }
    }

    override fun onItemLongClick(item: Note, view: View) {
        if (!mActionModeNote.selected) {
            hideFloatingButton(true)
            (view.context as AppCompatActivity).startSupportActionMode(mActionModeNote)
            mActionModeNote.selected = true
        }
        mActionModeNote.selectedItem(item, view)
    }

    private fun onReminderItemClick(item: Note) {
        note = item
        if (fragmentTAG == "archive") {
            configureBottomSheetConfirm()
        } else{
            configureBottomSheetText(note, false)
            configureBottomSheetReminder()
        }
    }

    override fun setAdapter(): NoteAdapter {
        adapter = NoteAdapter(this::onItemClick, this::onItemLongClick, this::onReminderItemClick)
        return adapter
    }

    override fun configureBottomSheetCamera() {
        home = false
        showBottom = "camera"
        val builder = BottomSheet.Builder(mMainActivity, R.layout.bottom_sheet_camera, this)
        builder.apply {
            setTitle(getString(R.string.text_view_scan_text))
            setInflateMenus()
            builder.build()
        }
        mDialogView = builder.getBottomSheetView()
        mDialogView.bottom_sheet_camera.minimumHeight = builder.getBottomSheetHeight()
        mCameraUtil = CameraUtil(this, mDialogView, builder)
        mCameraUtil.startCamera()
    }

    override fun configureBottomSheetImage(bitmap: Bitmap) {
        home = false
        mBitmap = bitmap
        showBottom = "image"
        val builder = BottomSheet.Builder(mMainActivity, R.layout.bottom_sheet_image, this)
        builder.apply {
            setTitle(getString(R.string.text_view_storage_image))
            setInflateMenus()
            build()
        }
        mDialogView = builder.getBottomSheetView()
        mDialogView.bottom_sheet_image.minimumHeight = builder.getBottomSheetHeight()
        mDialogView.image_view_image.setImageBitmap(bitmap)
    }

    override fun configureBottomSheetVoice() {
        home = true
        showBottom = "voice"
        val builder = BottomSheet.Builder(mMainActivity, R.layout.bottom_sheet_confirm, this)
        builder.build()
        mDialogView = builder.getBottomSheetView()
        mVoiceUtil = VoiceUtil(mMainActivity, mDialogView)
        mDialogView.text_view_confirm_title.text = getString(R.string.text_view_voice_recognition_title)
        mDialogView.text_view_confirm_body.text = ""
        mDialogView.text_view_confirm_body.hint = getString(R.string.text_view_voice_recognition_body)
        mDialogView.image_view_confirm.setImageResource(R.drawable.ic_drawable_voice_white)
        mDialogView.button_cancel.setOnClickListener {
            onBackPressed()
            builder.setHide()
        }
        mDialogView.button_confirm.setOnClickListener {
            onBackPressed()
            builder.setHide()
            val text = mDialogView.text_view_confirm_body.text.toString().trim()
            if (text.isNotEmpty()) {
                if(voiceSearch) {
                    mMaterialSearchView.setQuery(text, false)
                    onBindSearch(text)
                } else {
                    val note = Note(body = text)
                    configureBottomSheetText(note, true)
                }
            }
        }
    }

    override fun configureBottomSheetDraw() {
        home = false
        showBottom = "draw"
        val builder = BottomSheet.Builder(mMainActivity, R.layout.bottom_sheet_draw, this)
        builder.apply {
            setTitle(getString(R.string.menu_draw))
            setInflateMenus()
            build()
        }
        mDialogView = builder.getBottomSheetView()
        mDialogView.bottom_sheet_draw.minimumHeight = builder.getBottomSheetHeight()
        mDrawUtil = mDialogView.findViewById(R.id.draw_view)
        mDrawUtil.setBottomSheet(builder)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun configureBottomSheetText(item: Serializable?, save: Boolean) {
        home = false
        showBottom = "keyboard"
        this.save = save
        this.note = if (item == null) {
            Note()
        } else {
            item as Note
        }
        val builder = BottomSheet.Builder(mMainActivity, R.layout.bottom_sheet_text, this)
        builder.apply {
            setTitle(getTitle())
            setInflateMenus()
            setOnScrollChangeListener()
            setVisibleButtons(note)
            setLock(false)
            build()
        }
        mDialogView = builder.getBottomSheetView()
        mDialogView.bottom_sheet_text.minimumHeight = builder.getBottomSheetHeight()
        mDialogView.image_view_send.setImageResource(R.drawable.ic_drawable_save)
        configureBottomSheetText(this.note)

        mDialogView.material_bottom_toolbar.setOnTouchListener { _, event ->
            if (event.action == KeyEvent.ACTION_DOWN) {
                builder.setLock(true)
            }
            return@setOnTouchListener true
        }
    }

    private fun getTitle(): String {
        return if (save) {
            getString(R.string.app_save)
        } else {
            getString(R.string.app_edit)
        }
    }

    private fun configureBottomSheetText(note: Note) {
        mInputEditTextTitle = InputEditTextUtil(mDialogView.text_input_base_title)
        mInputEditTextBody = InputEditTextUtil(mDialogView.text_input_base_body)
        mInputEditTextTitle?.setHint(getString(R.string.text_view_note_title))
        mInputEditTextBody?.setHint(getString(R.string.text_view_note_body))
        updateUI()
        mInputEditTextBody?.requestFocus()
        mInputEditTextBody?.setSelection(note.body.length)
        if (save) {
            mInputEditTextBody?.show()
        }
        configureInputEditTextListeners()
        configureFloatingButtonListeners()
    }

    private fun updateReminder() {
        if (mReminderUtil != null) {
            val reminderUtil = (mReminderUtil as ReminderUtil)
            note.reminder = reminderUtil.getReminder()
            reminderUtil.disableFields(note.reminder)
        }
        if (!save) NoteApplication.noteViewModel.update(note)
        updateUI()
    }

    @SuppressLint("SetTextI18n")
    private fun updateUI() {
        mInputEditTextTitle?.setText(note.title)
        mInputEditTextBody?.setText(note.body)

        if (note.reminder.isChecked) {
            setLayoutParams(mDialogView.button_reminder_base, 32, 0, 0, 32)
            setLayoutParams(mDialogView.text_input_base_body, 32, 32, 32, 0)
            mDialogView.button_reminder_base.visibility = View.VISIBLE
            val date = DateFormatUtil.format(note.reminder.dateTime.time, true)
            val hour = DateFormatUtil.format(note.reminder.dateTime.time, false)
            mDialogView.button_reminder_base.text = "$date $hour"
        } else {
            setLayoutParams(mDialogView.button_reminder_base, 0, 0, 0, 0)
            setLayoutParams(mDialogView.text_input_base_body, 32, 32, 32, 0)
            mDialogView.button_reminder_base.visibility = View.INVISIBLE
        }
    }

    private fun setLayoutParams(view: View, marginLeft: Int, marginTop: Int, marginRight: Int, marginBottom: Int) {
        val layoutParams = view.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.setMargins(marginLeft, marginTop, marginRight, marginBottom)
        view.layoutParams = layoutParams
    }

    private fun configureInputEditTextListeners() {
        editTitle = false
        mDialogView.text_input_base_title.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(editable: Editable?) { note.title = editable.toString() }
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        mDialogView.text_input_base_body.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(editable: Editable?) { note.body = editable.toString() }
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        mDialogView.text_input_base_title.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) { editTitle = true }
            return@setOnFocusChangeListener
        }

        mDialogView.text_input_base_body.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) { editTitle = false }
            return@setOnFocusChangeListener
        }
    }

    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    private fun configureFloatingButtonListeners() {
        mDialogView.floating_button_base.show()
        mDialogView.floating_button_base.setOnClickListener {
            configureBottomSheetOptions()
        }

        mDialogView.floating_button_base.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                configureBottomSheetOptions()
                return@setOnTouchListener true
            }
            return@setOnTouchListener false
        }

        mDialogView.button_reminder_base.setOnClickListener {
            configureBottomSheetReminder()
        }
    }

    override fun configureBottomSheetOptions() {
        home = false
        showBottom = "reminder_options"
        val builder = BottomSheet.Builder(mMainActivity, R.layout.bottom_sheet_options, this)
        builder.apply {
            setOptions(home)
            setFavorite(note)
            build()
        }
        mDialogViewTemp = builder.getBottomSheetView()
        configureFloatingButtonOptions(builder)
    }

    override fun configureBottomSheetConfirm() {
        home = false
        showBottom = "confirm"
        val builder = BottomSheet.Builder(mMainActivity, R.layout.bottom_sheet_confirm, this)
        builder.build()
        mDialogView = builder.getBottomSheetView()
        mDialogView.text_view_confirm_title.text = getString(R.string.text_view_unarchive)
        mDialogView.text_view_confirm_body.text = getString(R.string.text_view_options)
        mDialogView.image_view_confirm.setImageResource(R.drawable.ic_drawable_unarchive)
        mDialogView.button_cancel.setOnClickListener {
            onBackPressed()
            builder.setHide()
        }

        mDialogView.button_confirm.setOnClickListener {
            note.archived = !note.archived
            NoteApplication.noteViewModel.update(note)
            onBackPressed()
            builder.setHide()
        }
    }

    override fun configureBottomSheetColor() {
        home = false
        showBottom = "color"
        val layoutID = if(UserPreferenceUtil.darkMode) {
            R.layout.bottom_sheet_color_dark
        } else {
            R.layout.bottom_sheet_color_light
        }
        val builder = BottomSheet.Builder(mMainActivity, layoutID, this)
        builder.apply {
            setTitle(getString(R.string.text_view_color))
            setInflateMenus()
            build()
        }
        mDialogViewTemp = builder.getBottomSheetView()
        if (UserPreferenceUtil.darkMode) {
            mDialogViewTemp.bottom_sheet_color_dark.minimumHeight = builder.getBottomSheetHeight()
        } else {
            mDialogViewTemp.bottom_sheet_color_light.minimumHeight = builder.getBottomSheetHeight()
        }
        val colors = ColorsUtil.getColors(mDialogViewTemp)
        for (color in colors) {
            val view = color.circleImageView
            ColorsUtil.setColor(note, color)
            view?.setOnClickListener {
                note.color = color.id
                ColorsUtil.setBackgroundColor(note, mDialogView)
                if (!save) NoteApplication.noteViewModel.update(note)
                onBackPressed()
                builder.setHide()
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun configureBottomSheetReminder() {
        home = false
        showBottom = "reminder"
        val builder = BottomSheet.Builder(mMainActivity, R.layout.bottom_sheet_reminder, this)
        builder.apply {
            setTitle(getString(R.string.menu_reminder))
            setInflateMenus()
            build()
        }
        mDialogViewTemp = builder.getBottomSheetView()
        mDialogViewTemp.bottom_sheet_reminder.minimumHeight = builder.getBottomSheetHeight()
        mReminderUtil = ReminderUtil(note, mDialogViewTemp)

        mDialogViewTemp.switch_reminder.setOnClickListener {
            note.reminder.isChecked = !note.reminder.isChecked
            updateReminder()
        }

        mDialogViewTemp.switch_reminder.setOnTouchListener { _, event ->
            if (event.actionMasked == MotionEvent.ACTION_MOVE) {
                return@setOnTouchListener false
            }
            return@setOnTouchListener false
        }
    }

    private fun configureFloatingButtonOptions(builder: BottomSheet.Builder) {
        mDialogViewTemp.floating_button_option_1.setOnClickListener {
            builder.setHide()
            if (home) {
                checkPermissionsCamera()
            } else {
                val message = OptionsUtil.getMessage(note)
                OptionsUtil.share(mMainActivity, message)
            }
        }
        mDialogViewTemp.floating_button_option_2.setOnClickListener {
            builder.setHide()
            if (home) {
                configureBottomSheetDraw()
            } else {
                val message = OptionsUtil.getMessage(note)
                OptionsUtil.copy(mMainActivity, message)
            }
        }
        mDialogViewTemp.floating_button_option_3.setOnClickListener {
            builder.setHide()
            if (home) {
                configureBottomSheetStorage()
            } else {
                configureBottomSheetColor()
            }
        }
        mDialogViewTemp.floating_button_option_4.setOnClickListener {
            builder.setHide()
            if (home) {
                voiceSearch = false
                checkPermissionsVoice()
            } else {
                note.favorite = !note.favorite
                builder.setFavorite(note)
                if (!save) NoteApplication.noteViewModel.update(note)
            }
        }
        mDialogViewTemp.floating_button_option_5.setOnClickListener {
            builder.setHide()
            if (home) {
                configureBottomSheetText(null, true)
            } else {
                configureBottomSheetReminder()
            }
        }
    }

    override fun configureBottomSheetPermission(manifestPermission: String, requestCode: Int) {
        showBottom = "permission"
        val builder = BottomSheet.Builder(mMainActivity, R.layout.bottom_sheet_confirm, this)
        builder.build()
        mDialogView = builder.getBottomSheetView()

        when(manifestPermission) {
            Manifest.permission.CAMERA -> {
                mDialogView.text_view_confirm_title.text = getString(R.string.text_view_permission_camera_title)
                mDialogView.text_view_confirm_body.text = getString(R.string.text_view_permission_camera_body)
                mDialogView.image_view_confirm.setImageResource(R.drawable.ic_drawable_camera)
            }
            Manifest.permission.RECORD_AUDIO -> {
                mDialogView.text_view_confirm_title.text = getString(R.string.text_view_permission_voice_title)
                mDialogView.text_view_confirm_body.text = getString(R.string.text_view_permission_voice_body)
                mDialogView.image_view_confirm.setImageResource(R.drawable.ic_drawable_voice_white)
            }
        }

        mDialogView.button_cancel.setOnClickListener {
            showBottom = null
            builder.setHide()
        }

        mDialogView.button_confirm.setOnClickListener {
            requestPermission(manifestPermission, requestCode)
            showBottom = null
            builder.setHide()
        }
    }

    protected fun showMessage(message: String, resourceID: Int) {
        mMainActivity.constraint_layout_status.visibility = View.VISIBLE
        mMainActivity.text_view_status.text = message
        mMainActivity.image_view_status.setImageResource(resourceID)
    }
}