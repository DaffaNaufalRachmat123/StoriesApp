package com.storiesapp.common.base

import android.os.Bundle
import android.view.MenuItem
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.storiesapp.core.AppDispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.coroutines.CoroutineContext
import kotlin.reflect.KClass

abstract class BaseActivity<VM : ViewModel> : AppCompatActivity, CoroutineScope {

    constructor() : super()

    constructor(@LayoutRes layoutId: Int) : super(layoutId)

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main


    protected val viewModel: VM by viewModel(clazz = getViewModel())

    val appDispatchers by inject<AppDispatchers>()

    abstract fun getViewModel(): KClass<VM>
    abstract fun onViewCreated(savedInstanceState: Bundle?)
    abstract fun observerViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onViewCreated(savedInstanceState)
        observerViewModel()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                super.onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    protected fun <T> LiveData<T>.onResult(action: (T) -> Unit) {
        observe(this@BaseActivity, Observer { data -> data?.let(action) })
    }

    protected fun initToolbar(
        toolbar: Toolbar,
        paramStrTitle: String = "",
        paramBooleanBack: Boolean = true
    ) {
        setSupportActionBar(toolbar)
        supportActionBar?.title = paramStrTitle
        supportActionBar?.setDisplayHomeAsUpEnabled(paramBooleanBack)
    }

    override fun onDestroy() {
        coroutineContext.cancel()
        super.onDestroy()
    }
}
