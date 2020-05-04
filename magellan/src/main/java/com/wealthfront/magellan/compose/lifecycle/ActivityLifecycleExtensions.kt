package com.wealthfront.magellan.compose.lifecycle

import android.app.Activity
import android.app.Application
import android.content.Context
import android.widget.FrameLayout
import androidx.annotation.IdRes
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.wealthfront.magellan.compose.navigation.Navigable

fun Navigable.attachToActivity(
  context: Activity,
  lifecycle: Lifecycle,
  @IdRes containerRes: Int
) {
  lifecycle.addObserver(object : DefaultLifecycleObserver {
    override fun onStart(owner: LifecycleOwner) {
      show(context)
      context.findViewById<FrameLayout>(containerRes).addView(view!!)
    }

    override fun onResume(owner: LifecycleOwner) {
      resume(context)
    }

    override fun onPause(owner: LifecycleOwner) {
      pause(context)
    }

    override fun onStop(owner: LifecycleOwner) {
      hide(context)
      context.findViewById<FrameLayout>(containerRes).removeView(view!!)
    }

    override fun onDestroy(owner: LifecycleOwner) {
      if (context.isFinishing) {
        destroy(context)
      }
    }
  })
}
