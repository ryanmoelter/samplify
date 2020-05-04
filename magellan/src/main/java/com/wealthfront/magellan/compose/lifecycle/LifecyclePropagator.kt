package com.wealthfront.magellan.compose.lifecycle

import android.content.Context

abstract class LifecyclePropagator : LifecycleAware,
  LifecycleOwner {

  private val lifecycleHost =
    LifecycleFSM()

  override val currentState get() = lifecycleHost.currentState

  final override fun create(context: Context) {
    lifecycleHost.create(context)
    onCreate(context)
  }

  final override fun show(context: Context) {
    lifecycleHost.show(context)
    onShow(context)
  }

  final override fun resume(context: Context) {
    lifecycleHost.resume(context)
    onResume(context)
  }

  final override fun pause(context: Context) {
    lifecycleHost.pause(context)
    onPause(context)
  }

  final override fun hide(context: Context) {
    lifecycleHost.hide(context)
    onHide(context)
  }

  final override fun destroy(context: Context) {
    lifecycleHost.destroy(context)
    onDestroy(context)
  }

  final override fun backPressed(): Boolean {
    return lifecycleHost.backPressed() || onBackPressed()
  }

  override fun attachToLifecycle(lifecycleAware: LifecycleAware, detachedState: LifecycleState) =
    lifecycleHost.attachToLifecycle(lifecycleAware, detachedState)

  override fun removeFromLifecycle(lifecycleAware: LifecycleAware, detachedState: LifecycleState) =
    lifecycleHost.removeFromLifecycle(lifecycleAware, detachedState)

  protected open fun onCreate(context: Context) {}

  protected open fun onShow(context: Context) {}

  protected open fun onResume(context: Context) {}

  protected open fun onPause(context: Context) {}

  protected open fun onHide(context: Context) {}

  protected open fun onDestroy(context: Context) {}

  protected open fun onBackPressed(): Boolean = false
}
