package com.android.common.view.statelayout

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.*
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.github.ajalt.timberkt.i
import com.storiesapp.common.R

class StateLayout : FrameLayout {
    private var isCustomEmptyView: Boolean = false
    var state = State.None // default state
    var loadingView: View = LayoutInflater.from(context).inflate(R.layout.sl_loading, this, false)
    var emptyView: View = LayoutInflater.from(context).inflate(R.layout.sl_empty, this, false)
    var errorView: View = LayoutInflater.from(context).inflate(R.layout.sl_error, this, false)
    var contentView: View? = null
    var animDuration = 250L
    var useContentBgWhenLoading = true
    var enableLoadingShadow = false

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    fun wrap(view: View?): StateLayout {
        if (view == null) {
            throw IllegalArgumentException("view can not be null")
        }
        with(emptyView) {
            visibility = View.INVISIBLE
            alpha = 0f
        }
        with(errorView) {
            visibility = View.INVISIBLE
            alpha = 0f
            findViewById<View>(R.id.btn_retry)?.setOnClickListener { retry() }
            setOnClickListener { retry() }
        }
        with(loadingView) {
            visibility = View.INVISIBLE
            alpha = 0f
        }
        prepareStateView()

        view.visibility = View.INVISIBLE
        view.alpha = 0f
        if (view.parent == null) {
            //no attach parent.
            addView(view, 0, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
            contentView = view
        } else {
            // 1.remove self from parent
            val parent = view.parent as ViewGroup
            val lp = view.layoutParams
            val index = parent.indexOfChild(view)
            parent.removeView(view)
            // 2.wrap view as a parent
            addView(view, 0, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))

            parent.addView(this, index, lp)
            contentView = view
        }
        switchLayout(State.Content)
        return this
    }

    fun wrap(activity: Activity): StateLayout = wrap((activity.findViewById<View>(android.R.id.content) as ViewGroup).getChildAt(0))

    fun wrap(fragment: Fragment): StateLayout = wrap(fragment.view)

    override fun onFinishInflate() {
        super.onFinishInflate()
        requireNotNull(contentView) { "contentView can not be null!" }
    }

    private fun prepareStateView() {
        addView(emptyView)
        addView(errorView)
        addView(loadingView)
    }

    private fun switchLayout(s: State) {
        if (state == s) return
        state = s

        i { "switchLayout State $state" }
        when (state) {
            State.Loading -> {
                switch(loadingView)
                if (useContentBgWhenLoading && contentView?.background != null) {
                    background = contentView?.background
                }
                if (enableLoadingShadow) {
                    loadingView.setBackgroundColor(Color.parseColor("#66000000"))
                } else {
                    loadingView.setBackgroundResource(0)
                }
            }
            State.Empty -> {
                i { "switchLayoutEmpty called" }
                switch(emptyView)
            }
            State.Error -> {
                switch(errorView)
            }
            State.Content -> {
                switch(contentView)
            }
            else -> switch(contentView)
        }
    }

    fun showShimmerLoading() : StateLayout {
        switchLayout(State.ShimmerLoading)
        return this
    }

    fun showLoading(showText: Boolean = false, loadingText: String = "Loading..."): StateLayout {
        switchLayout(State.Loading)
        val textView = loadingView.findViewById<TextView>(R.id.tvLoading)
        textView?.text = loadingText
        textView?.visibility = if (showText) View.VISIBLE else View.GONE
        return this
    }
    fun showCustomLoading(customView : Int) : StateLayout {
        context?.let {
            loadingView = LayoutInflater.from(it).inflate(customView , this)
        }
        switchLayout(State.Loading)
        return this
    }
    fun showBlank() : StateLayout {
        switchLayout(State.Blank)
        return this
    }

    fun showContent(): StateLayout {
        switchLayout(State.Content)
        return this
    }

    fun showEmpty(noDataText: String = "Belum ada data saat ini", noDataIconRes: Int = 0, customEmpty: Int = 0): StateLayout {
        if (customEmpty != 0) {
            emptyView = LayoutInflater.from(context).inflate(customEmpty, this, false)
            (emptyView.layoutParams as LayoutParams).gravity = Gravity.CENTER
            i{"showEmpty parent null ${parent == null}"}
            if (parent == null) {
                //no attach parent.
                addView(emptyView, 0, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
                i{"showEmpty add empty"}
            } else {
                // 1.remove self from parent
                val parent = parent as ViewGroup
                val lp = layoutParams
                val index = parent.indexOfChild(emptyView)
                parent.removeView(emptyView)
                // 2.wrap view as a parent
                addView(emptyView, 0, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))

                // 3.add this to original parent，暂不支持parent为ConstraintLayout
                parent.addView(this, index, lp)
                i{"showEmpty add parent empty"}
            }
            switchLayout(State.Empty)
        } else {
            i { "IsCustomEmptyView $isCustomEmptyView" }
            if (!isCustomEmptyView) {
                val textView = emptyView.findViewById<TextView?>(R.id.tvNoDataText)
                textView?.text = noDataText
                val imageView = emptyView.findViewById<ImageView?>(R.id.ivNoDataIcon)
                imageView?.setImageResource(noDataIconRes)
            }
        }

        switchLayout(State.Empty)
        return this
    }

    fun showError(): StateLayout {
        switchLayout(State.Error)
        return this
    }

    private fun switch(v: View?) {
        if (switchTask != null) {
            removeCallbacks(switchTask)
        }
        switchTask = v?.let { SwitchTask(it) }
        post(switchTask)
    }

    private fun retry() {
        showLoading()
        handler.postDelayed({
            mRetryAction?.invoke(errorView)
        }, animDuration)
    }

    var switchTask: SwitchTask? = null

    inner class SwitchTask(private var target: View) : Runnable {
        override fun run() {
            for (i in 0..childCount) {
                if (state == State.Loading && enableLoadingShadow && getChildAt(i) == contentView) continue
                hideAnim(getChildAt(i))
            }
            showAnim(target)
        }
    }

    private fun showAnim(v: View?) {
        if (v == null) return
        v.animate().cancel()
        v.animate().alpha(1f).setDuration(animDuration)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationStart(animation: Animator?) {
                        v.visibility = View.VISIBLE
                    }
                })
                .start()
    }

    private fun hideAnim(v: View?) {
        if (v == null) return
        v.animate().cancel()
        v.animate().alpha(0f).setDuration(animDuration)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        v.visibility = View.INVISIBLE
                    }
                })
                .start()
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (state == State.Loading && loadingView.visibility == View.VISIBLE) return true
        return super.dispatchTouchEvent(ev)
    }


    private var mRetryAction: ((errView: View) -> Unit)? = null

    private fun setLoadingLayout(layoutId: Int): StateLayout {
        loadingView = LayoutInflater.from(context).inflate(layoutId, this, false)
        (loadingView.layoutParams as LayoutParams).gravity = Gravity.CENTER
        return this
    }

    fun setEmptyLayout(layoutId: Int): StateLayout {
        emptyView = LayoutInflater.from(context).inflate(layoutId, this, false)
        (emptyView.layoutParams as LayoutParams).gravity = Gravity.CENTER
        isCustomEmptyView = true
        return this
    }

    private fun setErrorLayout(layoutId: Int): StateLayout {
        errorView = LayoutInflater.from(context).inflate(layoutId, this, false)
        (errorView.layoutParams as LayoutParams).gravity = Gravity.CENTER
        return this
    }

    fun onRetry(retryAction: ((errView: View) -> Unit)? = null): StateLayout {
        mRetryAction = retryAction
        return this
    }

    fun config(loadingLayoutId: Int = 0,
               emptyLayoutId: Int = 0,
               errorLayoutId: Int = 0,
               useContentBgWhenLoading: Boolean = false,
               animDuration: Long = 0L,
               enableLoadingShadow: Boolean = false,
               retryAction: ((errView: View) -> Unit)? = null): StateLayout {
        if (loadingLayoutId != 0) setLoadingLayout(loadingLayoutId)
        if (emptyLayoutId != 0) setEmptyLayout(emptyLayoutId)
        if (errorLayoutId != 0) setErrorLayout(errorLayoutId)
        if (useContentBgWhenLoading) {
            this.useContentBgWhenLoading = useContentBgWhenLoading
        }
        if (animDuration != 0L) {
            this.animDuration = animDuration
        }
        this.enableLoadingShadow = enableLoadingShadow
        mRetryAction = retryAction
        return this
    }
}