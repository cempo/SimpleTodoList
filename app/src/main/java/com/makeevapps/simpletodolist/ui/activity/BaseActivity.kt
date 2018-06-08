package com.makeevapps.simpletodolist.ui.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import com.makeevapps.simpletodolist.App

open class BaseActivity : AppCompatActivity() {
    /*@Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(App.instance.getCurrentThemeStyle().themeResId)
        super.onCreate(savedInstanceState)
    }

    fun setSupportActionBar(toolbar: Toolbar, homeAsUp: Boolean, homeEnabled: Boolean, titleText: String?) {
        setSupportActionBar(toolbar)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(homeAsUp)
            setHomeButtonEnabled(homeEnabled)
            setDisplayShowTitleEnabled(!titleText.isNullOrEmpty())

            if (!titleText.isNullOrEmpty()) {
                title = titleText
            }
        }
    }

    /*fun setSwipeRefreshLayout(swipeRefreshLayout: SwipeRefreshLayout) {
        swipeRefreshLayout.setColorSchemeResources(
                R.color.refresh_progress_1,
                R.color.refresh_progress_2,
                R.color.refresh_progress_3)
    }*/
}