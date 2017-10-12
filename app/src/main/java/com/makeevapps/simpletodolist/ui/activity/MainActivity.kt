package com.makeevapps.simpletodolist.ui.activity

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.Toolbar
import com.makeevapps.simpletodolist.R
import com.makeevapps.simpletodolist.databinding.ActivityMainBinding
import com.makeevapps.simpletodolist.enums.MainMenuItemType
import com.makeevapps.simpletodolist.enums.ThemeStyle
import com.makeevapps.simpletodolist.ui.fragment.CalendarFragment
import com.makeevapps.simpletodolist.ui.fragment.TodayFragment
import com.makeevapps.simpletodolist.utils.UIUtils
import com.makeevapps.simpletodolist.viewmodel.MainViewModel
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.holder.StringHolder
import com.mikepenz.materialdrawer.model.DividerDrawerItem
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem


class MainActivity : BaseActivity() {
    lateinit var model: MainViewModel
    lateinit var binding: ActivityMainBinding

    private lateinit var drawer: Drawer

    companion object {
        fun getActivityIntent(context: Context, clearFlag: Boolean = true): Intent {
            val intent = Intent(context, MainActivity::class.java)
            if (clearFlag) intent.addFlags(UIUtils.getClearFlags())
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        model = ViewModelProviders.of(this).get(MainViewModel::class.java)
        setTheme(ThemeStyle.getThemeById(model.preferenceManager.getThemeId()).themeResId)
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.controller = this
        binding.model = model

        drawer = DrawerBuilder()
                .withActivity(this)
                .withTranslucentStatusBar(false)
                .withHasStableIds(true)
                .withSavedInstance(savedInstanceState)
                .addDrawerItems(
                        createMenuItemByType(MainMenuItemType.TODAY),
                        createMenuItemByType(MainMenuItemType.CALENDAR),
                        DividerDrawerItem(),
                        createMenuItemByType(MainMenuItemType.SETTINGS)
                )
                //.withSelectedItem(1)
                .withOnDrawerItemClickListener { view, position, drawerItem ->
                    if (drawerItem != null) {
                        val menuItem = MainMenuItemType.getItemById(drawerItem.identifier)
                        selectMenuItem(menuItem)
                    }
                    false
                }
                .build()

        selectMenuItem(MainMenuItemType.TODAY)

        observeTaskData()
    }

    private fun createMenuItemByType(type: MainMenuItemType): PrimaryDrawerItem {
        return PrimaryDrawerItem().withIdentifier(type.id).withName(type.textResId).withIcon(type.imageResId)
                .withSelectable(true)
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
                .commitNow()
    }

    fun setToolbar(toolbar: Toolbar, homeAsUp: Boolean, homeEnabled: Boolean, title: String?) {
        setSupportActionBar(toolbar, homeAsUp, homeEnabled, title)
        drawer.setToolbar(this, toolbar)
        drawer.drawerLayout.setStatusBarBackgroundColor(ContextCompat.getColor(this, R.color.status_bar))
    }

    override fun onBackPressed() {
        if (drawer.isDrawerOpen) {
            drawer.closeDrawer()
        } else {
            super.onBackPressed()
        }
    }
}
