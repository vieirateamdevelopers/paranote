package br.com.vieirateam.paranote.fragment

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
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

abstract class BaseFragment<T, V> : GenericFragment(R.layout.adapter_recycler_view), MaterialSearchView.SearchViewListener, MaterialSearchView.OnQueryTextListener {

    protected var voiceSearch = false
    private lateinit var mConfiguration: Configuration
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
                ConstantsUtil.REQUEST_CODE_VOICE -> {
                    configureBottomSheetVoice()
                }
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
                    ConstantsUtil.REQUEST_CODE_READ_STORAGE -> {
                        val imageUri = intent.data as Uri
                        val bitmap = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                            val decoder = ImageDecoder.createSource(mMainActivity.contentResolver, imageUri)
                            ImageDecoder.decodeBitmap(decoder)
                        } else {
                            MediaStore.Images.Media.getBitmap(mMainActivity.contentResolver, imageUri)
                        }
                        configureBottomSheetImage(bitmap)
                    }
                    ConstantsUtil.REQUEST_CODE_DRAW -> {
                        val bundle = intent.getBundleExtra(ConstantsUtil.BUNDLE) as Bundle
                        val text = bundle.getString(ConstantsUtil.ITEM).toString()
                        val note = Note(body = text)
                        configureBottomSheetText(note, true)
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_search, menu)
        inflater.inflate(R.menu.menu_grid, menu)
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
            checkPermissionsVoice()
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

    fun checkPermissionsVoice() {
        val context = (activity as AppCompatActivity)
        val permission = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
        if (permission == PackageManager.PERMISSION_GRANTED) {
            configureBottomSheetVoice()
        } else {
            ActivityCompat.requestPermissions(context, arrayOf(Manifest.permission.RECORD_AUDIO), ConstantsUtil.REQUEST_CODE_VOICE)
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

    abstract fun saveItem(item: Serializable)

    abstract fun onActivityCreated()

    abstract fun onItemClick(item: V, view: View)

    abstract fun onItemLongClick(item: V, view: View)

    abstract fun setAdapter(): T

    abstract fun getItems()

    abstract fun onBindSearch(newText: String?)

    abstract fun configureBottomSheetCamera()

    abstract fun configureBottomSheetDraw()

    abstract fun configureBottomSheetImage(bitmap: Bitmap)

    abstract fun configureBottomSheetVoice()

    abstract fun configureBottomSheetText(item: Serializable?, save: Boolean)

    abstract fun configureBottomSheetOptions()

    abstract fun configureBottomSheetConfirm()

    abstract fun configureBottomSheetColor()

    abstract fun configureBottomSheetReminder()
}
