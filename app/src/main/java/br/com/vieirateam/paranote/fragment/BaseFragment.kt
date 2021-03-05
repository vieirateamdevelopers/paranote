package br.com.vieirateam.paranote.fragment

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import br.com.vieirateam.paranote.NoteApplication
import br.com.vieirateam.paranote.R
import br.com.vieirateam.paranote.actionmode.ActionModeNote
import br.com.vieirateam.paranote.adapter.BaseAdapter
import br.com.vieirateam.paranote.entity.Note
import br.com.vieirateam.paranote.util.ConstantsUtil
import br.com.vieirateam.paranote.util.UserPreferenceUtil
import com.miguelcatalan.materialsearchview.MaterialSearchView
import kotlinx.android.synthetic.main.adapter_recycler_view.*
import java.io.Serializable
import java.util.Locale

abstract class BaseFragment<T, V> : GenericFragment(R.layout.adapter_recycler_view), MaterialSearchView.SearchViewListener, MaterialSearchView.OnQueryTextListener {

    private lateinit var mConfiguration: Configuration

    protected var voiceSearch = false
    protected lateinit var mActionModeNote: ActionModeNote

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mConfiguration = resources.configuration
        configureRecyclerView()
        setOnScrollChangeListener()

        recycler_view.apply {
            itemAnimator = DefaultItemAnimator()
            scheduleLayoutAnimation()
            setHasFixedSize(true)
            adapter = setAdapter() as BaseAdapter<*>
        }
    }

    override fun posActivityCreated(savedInstanceState: Bundle?) {
        NoteApplication.getViewModel()
        getItems()
        onActivityCreated()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            when(requestCode) {
                ConstantsUtil.REQUEST_CODE_CAMERA -> {
                    configureBottomSheetCamera()
                }
                ConstantsUtil.REQUEST_CODE_READ_STORAGE -> {
                    configureBottomSheetStorage()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        intent?.let {
            if (resultCode == Activity.RESULT_OK) {
                when(requestCode) {
                    ConstantsUtil.REQUEST_CODE_VOICE -> {
                        val resultVoice = it.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                        if (resultVoice != null) {
                            val text = resultVoice[0]
                            if (voiceSearch) {
                                mMaterialSearchView.setQuery(text, false)
                                onBindSearch(text)
                            } else {
                                if (text.isNotEmpty()) {
                                    val note = Note(body = text)
                                    resultItem(note, true)
                                }
                            }
                        }
                    }
                    ConstantsUtil.REQUEST_CODE_READ_STORAGE -> {
                        val uri = intent.data as Uri
                        configureBottomSheetImage(uri)
                    }
                    ConstantsUtil.REQUEST_CODE_DRAW -> {
                        val bundle = intent.getBundleExtra(ConstantsUtil.BUNDLE) as Bundle
                        val text = bundle.getString(ConstantsUtil.ITEM).toString()
                        val note = Note(body = text)
                        resultItem(note, true)
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.search, menu)
        inflater.inflate(R.menu.grid, menu)
        mMaterialSearchView.setMenuItem(menu.findItem(R.id.menu_search))
        mMaterialSearchView.setVoiceSearch(true)
        mMaterialSearchView.setAnimationDuration(0)
        mMaterialSearchView.setOnQueryTextListener(this)
        mMaterialSearchView.setOnSearchViewListener(this)

        menu.findItem(R.id.menu_grid)?.isVisible =
            !(mConfiguration.orientation == Configuration.ORIENTATION_PORTRAIT &&
                    resources.getInteger(R.integer.span_count_portrait) == 1)

        val item: MenuItem? = menu.findItem(R.id.menu_grid)
        configureGridMode(item)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.menu_grid) {
            UserPreferenceUtil.gridMode = !UserPreferenceUtil.gridMode
            configureGridMode(item)
            configureRecyclerView()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onSearchViewShown() {
        mMaterialSearchViewVoice.visibility = View.VISIBLE
        mMaterialSearchViewVoice.setOnClickListener {
            voiceSearch = true
            configureRecognizerIntent()
        }
    }

    override fun onSearchViewClosed() {
        mMaterialSearchViewVoice.visibility = View.INVISIBLE
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        mMaterialSearchView.hideKeyboard(mView)
        return true
    }

    override fun onQueryTextChange(newText: String): Boolean {
        if (newText.isEmpty()) {
            mMaterialSearchViewVoice.visibility = View.VISIBLE
        } else {
            mMaterialSearchViewVoice.visibility = View.INVISIBLE
        }
        onBindSearch(newText)
        return true
    }

    private fun setOnScrollChangeListener() {
        recycler_view.setOnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
            if (fragmentTAG == "home" || fragmentTAG == "favorite") {
                if (!mActionModeNote.selected) {
                    if (scrollY > oldScrollY) {
                        mFloatingActionButton.hide()
                    } else {
                        mFloatingActionButton.show()
                    }
                }
            }
        }
    }

    private fun configureRecyclerView() {
        val spanCount = if (UserPreferenceUtil.gridMode) {
            1
        } else {
            if (mConfiguration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                resources.getInteger(R.integer.span_count_portrait)
            } else {
                resources.getInteger(R.integer.span_count_landscape)
            }
        }
        recycler_view.layoutManager = StaggeredGridLayoutManager(
            spanCount,
            StaggeredGridLayoutManager.VERTICAL
        )
    }

    private fun configureGridMode(item: MenuItem?) {
        if (UserPreferenceUtil.gridMode) {
            item?.setIcon(R.drawable.ic_drawable_list)
        } else {
            item?.setIcon(R.drawable.ic_drawable_grid)
        }
    }

    private fun configureBottomSheetStorage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        try {
            startActivityForResult(intent, ConstantsUtil.REQUEST_CODE_READ_STORAGE)
        } catch (exception: ActivityNotFoundException) {
            Log.d(ConstantsUtil.TAG, exception.toString())
        }
    }

    fun checkPermissionsReadStorage() {
        val context = (activity as AppCompatActivity)
        val permission = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
        if (permission == PackageManager.PERMISSION_GRANTED) {
            configureBottomSheetStorage()
        } else {
            ActivityCompat.requestPermissions(context, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), ConstantsUtil.REQUEST_CODE_READ_STORAGE)
        }
    }

    fun checkPermissionsCamera() {
        val context = (activity as AppCompatActivity)
        val permission = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
        if (permission == PackageManager.PERMISSION_GRANTED) {
            configureBottomSheetCamera()
        } else {
            ActivityCompat.requestPermissions(context, arrayOf(Manifest.permission.CAMERA), ConstantsUtil.REQUEST_CODE_CAMERA)
        }
    }

    fun configureRecognizerIntent() {
        val recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        try {
            startActivityForResult(recognizerIntent, ConstantsUtil.REQUEST_CODE_VOICE)
        } catch (exception: ActivityNotFoundException) {
            Log.d(ConstantsUtil.TAG, exception.toString())
        }
    }

    abstract fun saveItem(item: Serializable)

    abstract fun resultItem(item: Serializable?, save: Boolean)

    abstract fun onActivityCreated()

    abstract fun onItemClick(item: V, view: View)

    abstract fun onItemLongClick(item: V, view: View)

    abstract fun setAdapter(): T

    abstract fun getItems()

    abstract fun onBindSearch(newText: String?)

    abstract fun configureBottomSheetImage(path: Uri)

    abstract fun configureBottomSheetCamera()
}
