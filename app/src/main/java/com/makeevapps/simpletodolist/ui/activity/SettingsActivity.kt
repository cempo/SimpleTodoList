package com.makeevapps.simpletodolist.ui.activity

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.TaskStackBuilder
import android.view.MenuItem
import android.view.View
import com.makeevapps.simpletodolist.R
import com.makeevapps.simpletodolist.databinding.ActivitySettingsBinding
import com.makeevapps.simpletodolist.enums.ThemeStyle
import com.makeevapps.simpletodolist.viewmodel.SettingsViewModel
import kotlinx.android.synthetic.main.toolbar.*


class SettingsActivity : BaseActivity() {
    lateinit var model: SettingsViewModel
    lateinit var binding: ActivitySettingsBinding

    companion object {
        fun getActivityIntent(context: Context): Intent = Intent(context, SettingsActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        model = ViewModelProviders.of(this).get(SettingsViewModel::class.java)
        setTheme(ThemeStyle.getThemeById(model.preferenceManager.getThemeId()).themeResId)
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings)
        binding.controller = this

        setSupportActionBar(toolbar, true, true, getString(R.string.settings))
    }

    fun changeThemeButton(view: View) {
        model.changeTheme()

        TaskStackBuilder.create(this)
                .addNextIntent(MainActivity.getActivityIntent(this, false))
                .addNextIntent(intent)
                .startActivities()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }
}