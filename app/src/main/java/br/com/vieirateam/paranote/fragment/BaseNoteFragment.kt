package br.com.vieirateam.paranote.fragment

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import br.com.vieirateam.paranote.R
import br.com.vieirateam.paranote.NoteApplication
import br.com.vieirateam.paranote.adapter.NoteAdapter
import br.com.vieirateam.paranote.bottomsheet.BaseBottomSheet
import br.com.vieirateam.paranote.bottomsheet.CameraBottomSheet
import br.com.vieirateam.paranote.bottomsheet.ConfirmBottomSheet
import br.com.vieirateam.paranote.bottomsheet.ColorBottomSheet
import br.com.vieirateam.paranote.bottomsheet.DrawBottomSheet
import br.com.vieirateam.paranote.bottomsheet.ImageBottomSheet
import br.com.vieirateam.paranote.bottomsheet.NoteBottomSheet
import br.com.vieirateam.paranote.bottomsheet.OptionsBottomSheet
import br.com.vieirateam.paranote.bottomsheet.ReminderBottomSheet
import br.com.vieirateam.paranote.entity.Note
import br.com.vieirateam.paranote.util.ConstantsUtil
import br.com.vieirateam.paranote.util.DateFormatUtil
import br.com.vieirateam.paranote.util.KeyboardUtil
import br.com.vieirateam.paranote.util.NotificationUtil
import br.com.vieirateam.paranote.util.OptionsUtil
import br.com.vieirateam.paranote.util.ReminderUtil
import br.com.vieirateam.paranote.util.UserPreferenceUtil
import kotlinx.android.synthetic.main.adapter_app_bar.constraint_layout_status
import kotlinx.android.synthetic.main.adapter_app_bar.text_view_status
import kotlinx.android.synthetic.main.adapter_app_bar.image_view_status
import kotlinx.android.synthetic.main.bottom_sheet_base.view.text_input_base_title
import kotlinx.android.synthetic.main.bottom_sheet_base.view.text_input_base_body
import kotlinx.android.synthetic.main.bottom_sheet_base.view.button_reminder_base
import kotlinx.android.synthetic.main.bottom_sheet_base.view.floating_button_base
import java.io.Serializable

abstract class BaseNoteFragment : BaseFragment<NoteAdapter, Note>(), BaseBottomSheet.Callback {

    lateinit var adapter: NoteAdapter
    protected lateinit var note: Note

    private var save = false
    private var home = false
    private var mPath: String? = null
    private var mReminderUtil: ReminderUtil? = null
    private lateinit var mBundle: Bundle

    override fun addOnClickListener() {
        home = true
        showBottom = "options"
        mBottomSheetListener = OptionsBottomSheet(mMainActivity, this, null, home)
        mBottomSheetListener.build()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (savedInstanceState == null) {
            mBundle = Bundle()
        } else {
            mBundle = savedInstanceState
            val item = savedInstanceState.getSerializable(ConstantsUtil.ITEM)
            if (item != null) note = item as Note
            home = savedInstanceState.getBoolean(ConstantsUtil.HOME)
            save = savedInstanceState.getBoolean(ConstantsUtil.SAVE)
            mPath = savedInstanceState.getString(ConstantsUtil.PATH)
            showBottom = savedInstanceState.getString(ConstantsUtil.BOTTOM_SHEET)
            configureShowBottomSheet()
        }
    }

    private fun configureShowBottomSheet() {
        when(showBottom) {
            "options" -> {
                addOnClickListener()
            } "confirm" -> {
                configureBottomSheetConfirm()
            } "keyboard" -> {
                resultItem(note, save)
            } "color" -> {
                resultItem(note, save)
                configureBottomSheetColor()
            } "draw" -> {
                configureBottomSheetDraw()
            } "camera" -> {
                configureBottomSheetCamera()
            } "image" -> {
                configureBottomSheetImage(Uri.parse(mPath))
            } "reminder" -> {
                resultItem(note, save)
                configureBottomSheetReminder()
            } "reminder_options" -> {
                resultItem(note, save)
                configureBottomSheetOptions()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(ConstantsUtil.SAVE, save)
        outState.putBoolean(ConstantsUtil.HOME, home)
        outState.putString(ConstantsUtil.PATH, mPath)
        outState.putString(ConstantsUtil.BOTTOM_SHEET, showBottom)
        if (::note.isInitialized) outState.putSerializable(ConstantsUtil.ITEM, note)
    }

    override fun resultItem(item: Serializable?, save: Boolean) {
        home = false
        showBottom = "keyboard"
        this.save = save
        this.note = if (item == null) {
            Note()
        } else {
            item as Note
        }
        mBottomSheetListener = NoteBottomSheet(mMainActivity, this, note)
        mBottomSheetListener.build()
        mDialogView = mBottomSheetListener.getBottomSheetView()
        if (this.save) {
            mBottomSheetListener.setTitle(getString(R.string.app_save), 2)
        } else {
            mBottomSheetListener.setTitle(getString(R.string.app_edit), 2)
        }
        configureBottomSheet(this.note)
    }

    override fun onBottomSheetBackPressed() {
        if (showBottom == "camera") {
            val cameraUtil = (mBottomSheetListener as CameraBottomSheet).getCameraUtil()
            cameraUtil.shutdownFlash()
            cameraUtil.shutdownCamera()
        }
        getStatusBottom()
    }

    private fun getStatusBottom() {
        when(showBottom) {
            "keyboard" -> saveItem(note)
            "confirm" -> hideBottomSheet()
            "draw" -> hideBottomSheet()
            "camera" -> hideBottomSheet()
            "image" -> hideBottomSheet()
            "options" -> hideBottomSheet()
            "color" -> showBottom = "keyboard"
            "reminder" -> updateReminder()
            "reminder_options" -> hideBottomSheet()
        }
    }

    override fun setOnBottomSheetClickListener(buttonID: String) {
        when (buttonID) {
            "back" -> {
                getStatusBottom()
            } "cancel" -> {
                getStatusBottom()
            } "confirm" -> {
                note.archived = !note.archived
                NoteApplication.noteViewModel.update(note)
                getStatusBottom()
            } "send" -> {
                getStatusBottom()
            } "clear" -> {
                note.title = ""
                note.body = ""
                updateUI()
            } "draw" -> {
                note = Note()
                note.body = mBottomSheetListener.getBottomSheetText().toString()
                resultItem(note, true)
            } "color" -> {
                note.color = mBottomSheetListener.getBottomSheetColor()
                if (!save) NoteApplication.noteViewModel.update(note)
                getStatusBottom()
            } "send_image" -> {
                note = Note()
                note.body = mBottomSheetListener.getBottomSheetText().toString()
                resultItem(note, true)
            } "camera" -> {
                note = Note()
                note.body = mBottomSheetListener.getBottomSheetText().toString()
                resultItem(note, true)
            } "reminder" -> {
                note.reminder.isChecked = !note.reminder.isChecked
                if (mReminderUtil != null) {
                    note.reminder = (mReminderUtil as ReminderUtil).getReminder()
                    (mReminderUtil as ReminderUtil).disableFields(note.reminder)
                }
                updateUI()
            } "option1" -> {
                if (home) {
                    checkPermissionsCamera()
                } else {
                    val message = OptionsUtil.getMessage(note)
                    OptionsUtil.share(mMainActivity, message)
                }
            } "option2" -> {
                if (home) {
                    configureBottomSheetDraw()
                } else {
                    val message = OptionsUtil.getMessage(note)
                    OptionsUtil.copy(mMainActivity, message)
                }
            } "option3" -> {
                if (home) {
                    checkPermissionsReadStorage()
                } else {
                   configureBottomSheetColor()
                }
            } "option4" -> {
                if (home) {
                    voiceSearch = false
                    configureRecognizerIntent()
                } else {
                    note.favorite = !note.favorite
                    mBottomSheetListener.setFavoriteNote(note.favorite)
                    if (!save) NoteApplication.noteViewModel.update(note)
                    getStatusBottom()
                }
            } "option5" -> {
                if (home) {
                    resultItem(null,true)
                } else {
                    configureBottomSheetReminder()
                }
            }
        }
    }

    override fun saveItem(item: Serializable) {
        note = item as Note
        note.title = mDialogView.text_input_base_title.text?.trim().toString()
        note.body = mDialogView.text_input_base_body.text?.trim().toString()

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
                note.id = UserPreferenceUtil.noteID
                if (note.id == 0.toLong()) {
                    note.id = 1
                } else {
                    note.id += 1
                }
                UserPreferenceUtil.noteID = note.id
                NoteApplication.noteViewModel.insert(note)
                if (note.reminder.isChecked) {
                    NotificationUtil.create(note)
                }
            } else {
                NoteApplication.noteViewModel.update(note)
                if (note.reminder.isChecked) {
                    NotificationUtil.cancel(note)
                    NotificationUtil.create(note)
                }
            }
        }
        mReminderUtil = null
        hideBottomSheet()
    }

    override fun onItemClick(item: Note, view: View) {
        if (mActionModeNote.selected) {
            mActionModeNote.selectedItem(item, view)
        } else {
            note = item
            if (fragmentTAG == "archive") {
                configureBottomSheetConfirm()
            } else{
                resultItem(note, false)
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
            resultItem(note, false)
            configureBottomSheetReminder()
        }
    }

    override fun setAdapter(): NoteAdapter {
        adapter = NoteAdapter(this::onItemClick, this::onItemLongClick, this::onReminderItemClick)
        return adapter
    }

    private fun configureBottomSheet(note: Note) {
        mDialogView.text_input_base_title.hint = getString(R.string.text_view_note_title)
        mDialogView.text_input_base_body.hint = getString(R.string.text_view_note_body)
        updateUI()
        mDialogView.text_input_base_body.requestFocus()
        mDialogView.text_input_base_body.setSelection(note.body.length)
        if (save) KeyboardUtil.show(mDialogView.text_input_base_body)
        configureFloatingButtonListeners(mDialogView)

        mDialogView.text_input_base_title.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                note.title = editable.toString()
            }
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        mDialogView.text_input_base_body.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                note.body = editable.toString()
            }
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    @SuppressLint("SetTextI18n")
    private fun updateUI() {
        mDialogView.text_input_base_title.setText(note.title)
        mDialogView.text_input_base_body.setText(note.body)

        if (note.reminder.isChecked) {
            mDialogView.button_reminder_base.visibility = View.VISIBLE
            val date = DateFormatUtil.format(note.reminder.dateTime.time, true)
            val hour = DateFormatUtil.format(note.reminder.dateTime.time, false)
            mDialogView.button_reminder_base.text = "$date $hour"
        } else {
            mDialogView.button_reminder_base.visibility = View.INVISIBLE
        }
    }

    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    private fun configureFloatingButtonListeners(view: View) {
        view.floating_button_base.show()
        view.floating_button_base.setOnClickListener {
            configureBottomSheetOptions()
        }

        view.floating_button_base.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                configureBottomSheetOptions()
                return@setOnTouchListener true
            }
            return@setOnTouchListener false
        }

        view.button_reminder_base.setOnClickListener {
            configureBottomSheetReminder()
        }
    }

    override fun configureBottomSheetCamera() {
        home = false
        showBottom = "camera"
        mBottomSheetListener = CameraBottomSheet(mMainActivity, this)
        mBottomSheetListener.setTitle(getString(R.string.text_view_scan_text), 1)
        mBottomSheetListener.build()
    }

    override fun configureBottomSheetImage(imageUri: Uri) {
        home = false
        mPath = imageUri.toString()
        showBottom = "image"
        mBottomSheetListener = ImageBottomSheet(imageUri, mMainActivity, this)
        mBottomSheetListener.build()
        mDialogView = mBottomSheetListener.getBottomSheetView()
        mBottomSheetListener.setTitle(getString(R.string.text_view_storage_image), 2)
    }

    private fun configureBottomSheetDraw() {
        home = false
        showBottom = "draw"
        mBottomSheetListener = DrawBottomSheet(mMainActivity, this)
        mBottomSheetListener.build()
        mBottomSheetListener.setTitle(getString(R.string.menu_draw), 3)
    }

    private fun configureBottomSheetOptions() {
        home = false
        showBottom = "reminder_options"
        mBottomSheetListener = OptionsBottomSheet(mMainActivity, this, note, home)
        mBottomSheetListener.build()
    }

    private fun configureBottomSheetConfirm() {
        home = false
        showBottom = "confirm"
        mBottomSheetListener = ConfirmBottomSheet(mMainActivity, this)
        mBottomSheetListener.build()
    }

    private fun configureBottomSheetColor() {
        home = false
        showBottom = "color"
        mBottomSheetListener = ColorBottomSheet(mMainActivity, this, note, mDialogView)
        mBottomSheetListener.build()
        mBottomSheetListener.setTitle(getString(R.string.text_view_color), 0)
    }

    private fun configureBottomSheetReminder() {
        home = false
        showBottom = "reminder"
        mBottomSheetListener = ReminderBottomSheet(mMainActivity, this)
        mBottomSheetListener.build()
        mBottomSheetListener.setTitle(getString(R.string.menu_reminder), 0)
        val mDialogView = mBottomSheetListener.getBottomSheetView()
        mReminderUtil = ReminderUtil(note, mDialogView)
    }

    private fun hideBottomSheet() {
        showBottom = null
    }

    private fun updateReminder() {
        showBottom = "keyboard"
        updateUI()
    }

    protected fun showMessage(message: String, resourceID: Int) {
        mMainActivity.constraint_layout_status.visibility = View.VISIBLE
        mMainActivity.text_view_status.text = message
        mMainActivity.image_view_status.setImageResource(resourceID)
    }
}