package com.wealthfront.magellan.compose.lifecycle

import android.content.Context

interface LifecycleAware {

  fun create(context: Context) {}

  fun show(context: Context) {}

  fun resume(context: Context) {}

  fun pause(context: Context) {}

  fun hide(context: Context) {}

  fun destroy(context: Context) {}

  fun backPressed(): Boolean = false
}

interface LifecycleOwner {

  val currentState: LifecycleState

  fun attachToLifecycle(lifecycleAware: LifecycleAware, detachedState: LifecycleState = LifecycleState.Destroyed)

  fun removeFromLifecycle(lifecycleAware: LifecycleAware, detachedState: LifecycleState = LifecycleState.Destroyed)
}
