package com.makeevapps.simpletodolist.ui.fragment

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v14.preference.SwitchPreference
import android.support.v4.app.TaskStackBuilder
import android.support.v7.preference.ListPreference
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceFragmentCompat
import android.support.v7.preference.PreferenceManager
import android.text.format.DateFormat
import com.makeevapps.simpletodolist.R
import com.makeevapps.simpletodolist.ui.activity.AboutActivity
import com.makeevapps.simpletodolist.ui.activity.MainActivity
import com.makeevapps.simpletodolist.ui.activity.SettingsActivity
import com.makeevapps.simpletodolist.viewmodel.SettingsViewModel


class SettingsFragment : PreferenceFragmentCompat(), Preference.OnPreferenceClickListener,
        Preference.OnPreferenceChangeListener {
    private lateinit var model: SettingsViewModel
    private lateinit var themeListPreference: ListPreference

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preference_settings)
        model = ViewModelProviders.of(this).get(SettingsViewModel::class.java)

        //PreferenceManager.setDefaultValues(context, R.xml.settings, false)

        themeListPreference = findPreference(getString(R.string.themeId)) as ListPreference
        themeListPreference.onPreferenceChangeListener = this

        val is24HourFormat = findPreference(getString(R.string.is24HourFormat)) as SwitchPreference
        is24HourFormat.isChecked = model.preferenceManager.is24HourFormat()

        findPreference(getString(R.string.aboutScreen)).onPreferenceClickListener = this
    }

    override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
        when (preference?.key) {
            getString(R.string.themeId) -> {
                restartApp()
            }
        }
        return true
    }

    override fun onPreferenceClick(preference: Preference?): Boolean {
        when (preference?.key) {
            getString(R.string.aboutScreen) -> {
                startActivity(AboutActivity.getActivityIntent(context!!))
            }
        }
        return false
    }

    fun restartApp() {
        TaskStackBuilder.create(context!!)
                .addNextIntent(MainActivity.getActivityIntent(context!!, false))
                .addNextIntent(SettingsActivity.getActivityIntent(context!!))
                .startActivities()
    }
}