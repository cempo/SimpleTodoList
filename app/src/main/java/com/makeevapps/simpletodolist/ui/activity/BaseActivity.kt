package com.makeevapps.simpletodolist.ui.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import com.makeevapps.simpletodolist.App

open class BaseActivity : AppCompatActivity() {
    var isActivityVisible = false

    /*@Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(App.instance.getCurrentThemeStyle().themeResId)
        super.onCreate(savedInstanceState)
    }

    fun setSupportActionBar(toolbar: Toolbar, homeAsUp: Boolean, homeEnabled: Boolean, title: String?) {
        setSupportActionBar(toolbar)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(homeAsUp)
            actionBar.setHomeButtonEnabled(homeEnabled)
            if (title == null) {
                actionBar.setDisplayShowTitleEnabled(false)
            }

            if (!title.isNullOrEmpty()) {
                actionBar.title = title
            }
        }
    }

    /*fun setSwipeRefreshLayout(swipeRefreshLayout: SwipeRefreshLayout) {
        swipeRefreshLayout.setColorSchemeResources(
                R.color.refresh_progress_1,
                R.color.refresh_progress_2,
                R.color.refresh_progress_3)
    }*/

    override fun onResume() {
        super.onResume()
        isActivityVisible = true
    }

    override fun onPause() {
        isActivityVisible = false
        super.onPause()
    }
}