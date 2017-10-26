package com.makeevapps.simpletodolist.ui.activity

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import com.makeevapps.simpletodolist.R
import com.makeevapps.simpletodolist.enums.ThemeStyle
import com.makeevapps.simpletodolist.viewmodel.SettingsViewModel
import kotlinx.android.synthetic.main.toolbar.*


class SettingsActivity : BaseActivity() {
    lateinit var model: SettingsViewModel

    companion object {
        fun getActivityIntent(context: Context): Intent = Intent(context, SettingsActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        model = ViewModelProviders.of(this).get(SettingsViewModel::class.java)
        setTheme(model.getThemeResId())
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        setSupportActionBar(toolbar, true, true, getString(R.string.settings))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }
}