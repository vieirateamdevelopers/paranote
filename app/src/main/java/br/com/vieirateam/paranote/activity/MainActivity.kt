package br.com.vieirateam.paranote.activity

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import br.com.vieirateam.paranote.R
import br.com.vieirateam.paranote.extension.addFragment
import br.com.vieirateam.paranote.fragment.*
import br.com.vieirateam.paranote.util.ConstantsUtil
import com.google.android.material.navigation.NavigationView
import com.miguelcatalan.materialsearchview.MaterialSearchView
import kotlinx.android.synthetic.main.activity_main.drawer_layout
import kotlinx.android.synthetic.main.activity_main.navigation_view
import kotlinx.android.synthetic.main.adapter_main_toolbar.material_toolbar

class MainActivity : GenericActivity(R.layout.activity_main), NavigationView.OnNavigationItemSelectedListener {

    private var isHome = true
    private var commit = false
    private lateinit var mMaterialSearchView: MaterialSearchView
    private lateinit var mAppCompatImageViewVoice : AppCompatImageView

    override fun posCreate(savedInstanceState: Bundle?) {
        mMaterialSearchView = findViewById(R.id.material_search_view)
        mAppCompatImageViewVoice = findViewById(R.id.image_view_voice)
        setSupportActionBar()
        configureToolbar()

        val savedTitle = savedInstanceState?.getCharSequence(ConstantsUtil.TITLE)
        val isHome = savedInstanceState?.getBoolean(ConstantsUtil.HOME)
        if (savedTitle != null && isHome != null) {
            title = savedTitle
            this.isHome = isHome
        } else {
            firstFragment()
        }
    }

    private fun configureToolbar() {
        val mActionBarDrawerToggle = ActionBarDrawerToggle(this, drawer_layout, material_toolbar, 0, 0)
        mActionBarDrawerToggle.isDrawerIndicatorEnabled = false
        mActionBarDrawerToggle.setToolbarNavigationClickListener {
            drawer_layout.openDrawer(GravityCompat.START)
        }
        mActionBarDrawerToggle.syncState()
        navigation_view.setNavigationItemSelectedListener(this)
    }

    override fun onRestart() {
        super.onRestart()
        if (!commit) firstFragment()
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            if (isSearchOpen()) {
                return
            } else {
                if (isHome) {
                    finishAffinity()
                    super.onBackPressed()
                } else {
                    firstFragment()
                }
            }
        }
    }

    private fun isSearchOpen(): Boolean {
        return if (mMaterialSearchView.isSearchOpen) {
            mMaterialSearchView.closeSearch()
            mAppCompatImageViewVoice.visibility = View.INVISIBLE
            true
        } else {
            false
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(ConstantsUtil.HOME, isHome)
        outState.putCharSequence(ConstantsUtil.TITLE, title)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        if (mMaterialSearchView.isSearchOpen) {
            mAppCompatImageViewVoice.visibility = View.VISIBLE
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_favorites -> {
                setFragment(0, "favorite", getString(R.string.nav_favorites), false, FavoritesFragment())
            }
            R.id.nav_notes -> {
                firstFragment()
            }
            R.id.nav_reminders-> {
                setFragment(2, "reminder", getString(R.string.nav_reminders), false, RemindersFragment())
            }
            R.id.nav_archived -> {
                setFragment(3, "archive", getString(R.string.nav_archived), false, ArchivedFragment())
            }
            R.id.nav_settings -> {
                setFragment(4, "settings", getString(R.string.nav_settings), false, SettingsFragment())
            }
            R.id.nav_about -> {
                setFragment(5, "about", getString(R.string.nav_about), false, AboutFragment())
            }
            R.id.nav_exit -> {
                finishAffinity()
            }
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        isSearchOpen()
        return true
    }

    private fun firstFragment() {
        setFragment(1, "home", getString(R.string.nav_notes), true, NotesFragment())
    }

    private fun setFragment(index: Int, fragmentTAG: String, title: String, isHome: Boolean, fragment: Fragment) {
        this.title = title
        this.isHome = isHome
        navigation_view.menu.getItem(index).isChecked = true
        bundle.putString(ConstantsUtil.FRAGMENT, fragmentTAG)
        fragment.arguments = bundle
        startFragment(fragment)
    }

    private fun startFragment(fragment: Fragment) {
        commit = addFragment(R.id.frame_layout, fragment)
    }

    fun startReminderFragment() {
        setFragment(2, "reminder", getString(R.string.nav_reminders), false, RemindersFragment())
    }

    fun getMaterialSearchView() = mMaterialSearchView

    fun getAppCompatImageViewVoice() = mAppCompatImageViewVoice
}