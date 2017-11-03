package com.makeevapps.simpletodolist.ui.activity

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import com.makeevapps.simpletodolist.R
import com.makeevapps.simpletodolist.databinding.ActivityMainBinding
import com.makeevapps.simpletodolist.databinding.ViewMenuHeaderBinding
import com.makeevapps.simpletodolist.enums.MainMenuItemType
import com.makeevapps.simpletodolist.ui.fragment.CalendarFragment
import com.makeevapps.simpletodolist.ui.fragment.TodayFragment
import com.makeevapps.simpletodolist.utils.DateUtils
import com.makeevapps.simpletodolist.utils.UIUtils
import com.makeevapps.simpletodolist.viewmodel.MainViewModel
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.holder.DimenHolder
import com.mikepenz.materialdrawer.holder.StringHolder
import com.mikepenz.materialdrawer.model.DividerDrawerItem
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem


class MainActivity : BaseActivity(), SharedPreferences.OnSharedPreferenceChangeListener {
    private lateinit var drawer: Drawer

    companion object {
        fun getActivityIntent(context: Context, clearFlag: Boolean = true): Intent {
            val intent = Intent(context, MainActivity::class.java)
            if (clearFlag) intent.addFlags(UIUtils.getClearFlags())
            return intent
        }
    }

    val model: MainViewModel by lazy {
        ViewModelProviders.of(this).get(MainViewModel::class.java)
    }

    val binding: ActivityMainBinding by lazy {
        DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.controller = this
        binding.model = model

        model.preferenceManager.getSharedPreferences().registerOnSharedPreferenceChangeListener(this)

        val headerBinding = DataBindingUtil.inflate<ViewMenuHeaderBinding>(LayoutInflater.from(this), R.layout
                .view_menu_header, null, false)
        headerBinding.controller = this
        headerBinding.date = DateUtils.currentTime()


        drawer = DrawerBuilder()
                .withActivity(this)
                .withHeader(headerBinding.root)
                .withHeaderHeight(DimenHolder.fromDp(200))
                .withTranslucentStatusBar(false)
                .withHasStableIds(true)
                .withSavedInstance(savedInstanceState)
                .addDrawerItems(
                        createMenuItemByType(MainMenuItemType.TODAY, true),
                        createMenuItemByType(MainMenuItemType.CALENDAR, true),
                        DividerDrawerItem(),
                        createMenuItemByType(MainMenuItemType.SETTINGS, false)
                )
                .withSelectedItem(1)
                .withOnDrawerItemClickListener { view, position, drawerItem ->
                    if (drawerItem != null) {
                        val menuItem = MainMenuItemType.getItemById(drawerItem.identifier)
                        selectMenuItem(menuItem)
                    }
                    false
                }
                .build()

        if (savedInstanceState == null) {
            selectMenuItem(MainMenuItemType.TODAY)
        }

        observeTaskData()
    }

    private fun createMenuItemByType(type: MainMenuItemType, selectable: Boolean): PrimaryDrawerItem {
        return PrimaryDrawerItem()
                .withIdentifier(type.id)
                .withName(type.textResId)
                .withIcon(type.imageResId)
                .withSelectable(selectable)
                .withIconTintingEnabled(true)
    }

    private fun observeTaskData() {
        model.getTasksCount().observe(this, Observer<Int> { tasksCount ->
            if (tasksCount != null) {
                drawer.updateBadge(1, StringHolder("$tasksCount"))
            }
        })
    }

    fun selectMenuItem(menuItem: MainMenuItemType) {
        when (menuItem) {
            MainMenuItemType.TODAY -> {
                showFragment(TodayFragment.newInstance())
            }
            MainMenuItemType.CALENDAR -> {
                showFragment(CalendarFragment.newInstance())
            }
            MainMenuItemType.SETTINGS -> {
                startActivity(SettingsActivity.getActivityIntent(this))
            }
        }
    }

    private fun showFragment(fragment: Fragment) {
        //val lastFragment = supportFragmentManager.findFragmentById(R.id.container)
        supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commitNowAllowingStateLoss()
    }

    fun setToolbar(toolbar: Toolbar, homeAsUp: Boolean, homeEnabled: Boolean, title: String?) {
        setSupportActionBar(toolbar, homeAsUp, homeEnabled, title)
        drawer.setToolbar(this, toolbar, true)
        //drawer.drawerLayout.setStatusBarBackgroundColor(ContextCompat.getColor(this, R.color.status_bar))
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (!key.isNullOrEmpty() && key == getString(R.string.is24HourFormat)) {
            selectMenuItem(MainMenuItemType.getItemById(drawer.currentSelection))
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        val outState = drawer.saveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onBackPressed() {
        if (drawer.isDrawerOpen) {
            drawer.closeDrawer()
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        model.preferenceManager.getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this)
        super.onDestroy()
    }
}
