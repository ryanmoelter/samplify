package com.wealthfront.magellan.compose

import android.content.Context
import android.view.View
import androidx.annotation.LayoutRes
import com.wealthfront.magellan.compose.lifecycle.LifecycleAware
import com.wealthfront.magellan.compose.lifecycle.LifecycleFSM
import com.wealthfront.magellan.compose.lifecycle.LifecycleOwner
import com.wealthfront.magellan.compose.lifecycle.LifecycleState
import com.wealthfront.magellan.compose.lifecycle.lifecycle
import com.wealthfront.magellan.compose.transition.Displayable

abstract class ViewWrapper(
  @LayoutRes val layoutRes: Int
) : Displayable, LifecycleAware, LifecycleOwner {

  private val lifecycleHost = LifecycleFSM()
  override val currentState get() = lifecycleHost.currentState

  final override var view: View? = null
    private set

  final override fun create(context: Context) {
    lifecycleHost.create(context)
    onCreate(context)
  }

  final override fun show(context: Context) {
    view = View.inflate(context, layoutRes, null)
    lifecycleHost.show(context)
    onShow(context, view!!)
  }

  final override fun resume(context: Context) {
    lifecycleHost.resume(context)
    onResume(context, view!!)
  }

  final override fun pause(context: Context) {
    onPause(context, view!!)
    lifecycleHost.pause(context)
  }

  final override fun hide(context: Context) {
    onHide(context, view!!)
    lifecycleHost.hide(context)
    view = null
  }

  final override fun destroy(context: Context) {
    onDestroy(context)
    lifecycleHost.destroy(context)
  }

  final override fun backPressed(): Boolean {
    return lifecycleHost.backPressed() || onBackPressed()
  }

  override fun attachToLifecycle(lifecycleAware: LifecycleAware, detachedState: LifecycleState) =
    lifecycleHost.attachToLifecycle(lifecycleAware, detachedState)

  override fun removeFromLifecycle(lifecycleAware: LifecycleAware, detachedState: LifecycleState) =
    lifecycleHost.removeFromLifecycle(lifecycleAware, detachedState)

  protected open fun onCreate(context: Context) {}

  protected open fun onShow(context: Context, view: View) {}

  protected open fun onResume(context: Context, view: View) {}

  protected open fun onPause(context: Context, view: View) {}

  protected open fun onHide(context: Context, view: View) {}

  protected open fun onDestroy(context: Context) {}

  protected open fun onBackPressed(): Boolean = false
}

// An easy wrapper to keep using customViews
class CustomViewWrapper<CustomView : View>(
  val createView: (Context) -> CustomView
) : LifecycleAware {
  var view: CustomView? = null
    protected set

  override fun show(context: Context) {
    view = createView(context)
  }

  override fun hide(context: Context) {
    view = null
  }
}

fun <CustomView : View> LifecycleOwner.lifecycleView(createView: (Context) -> CustomView) =
  lifecycle(CustomViewWrapper(createView), { it.view })
