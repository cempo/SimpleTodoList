package com.makeevapps.simpletodolist.ui.activity

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.MenuItem
import com.makeevapps.simpletodolist.R
import com.makeevapps.simpletodolist.databinding.ActivityAboutBinding
import com.makeevapps.simpletodolist.databinding.FragmentTodayBinding
import com.makeevapps.simpletodolist.viewmodel.AboutViewModel
import kotlinx.android.synthetic.main.toolbar.*

class AboutActivity : BaseActivity() {
    lateinit var model: AboutViewModel
    private lateinit var binding: ActivityAboutBinding

    companion object {
        fun getActivityIntent(context: Context): Intent = Intent(context, AboutActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        model = ViewModelProviders.of(this).get(AboutViewModel::class.java)
        setTheme(model.getThemeResId())
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_about)
        binding.controller = this
        binding.model = model

        setSupportActionBar(toolbar, true, true, getString(R.string.about))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }
}