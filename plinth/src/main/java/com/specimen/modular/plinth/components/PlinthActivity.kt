package com.specimen.modular.plinth.components

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

abstract class PlinthActivity<T : ViewDataBinding> : AppCompatActivity() {

    private lateinit var binding: T

    // Lifecycle Methods
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, contentLayout())
        bindViewModel()
        bundleValues()
        onViewReady(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        onViewVisible()
        injectUIEnhancements()
    }

    override fun onRestart() {
        super.onRestart()
        onViewReVisible()
    }

    override fun onStop() {
        super.onStop()
        onViewRemoved()
    }

    override fun onDestroy() {
        super.onDestroy()
        onViewDestroyed()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        onResult(requestCode, resultCode, data)
    }

    // Abstract Functions
    abstract fun onViewReady(savedInstanceState: Bundle?)

    abstract fun onViewVisible()
    abstract fun onViewReVisible()
    abstract fun onViewRemoved()
    abstract fun onViewDestroyed()
    abstract fun onResult(requestCode: Int, resultCode: Int, data: Intent?)
    abstract fun contentLayout(): Int

    // Open Functions
    open fun bindViewModel() {}

    open fun injectUIEnhancements() {}

    open fun load(visible: Boolean) {}

    open fun bundleValues() {}
}