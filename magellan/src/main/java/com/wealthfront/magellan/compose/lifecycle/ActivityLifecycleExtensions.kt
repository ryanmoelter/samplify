package com.wealthfront.magellan.compose.lifecycle

import android.app.Activity
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
  lifecycle.addObserver(ActivityLifecycleAdapter(this, context, containerRes))
}

class ActivityLifecycleAdapter(
  private val navigable: Navigable,
  private val context: Activity,
  private val containerRes: Int
) : DefaultLifecycleObserver {
  override fun onStart(owner: LifecycleOwner) {
    navigable.show(context)
    context.findViewById<FrameLayout>(containerRes).addView(navigable.view!!)
  }

  override fun onResume(owner: LifecycleOwner) {
    navigable.resume(context)
  }

  override fun onPause(owner: LifecycleOwner) {
    navigable.pause(context)
  }

  override fun onStop(owner: LifecycleOwner) {
    navigable.hide(context)
    context.findViewById<FrameLayout>(containerRes).removeView(navigable.view!!)
  }

  override fun onDestroy(owner: LifecycleOwner) {
    if (context.isFinishing) {
      navigable.destroy(context)
    }
  }
}
