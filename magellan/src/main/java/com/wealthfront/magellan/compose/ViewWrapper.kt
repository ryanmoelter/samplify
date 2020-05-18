package com.wealthfront.magellan.compose

import android.content.Context
import android.view.View
import android.view.View.inflate
import androidx.annotation.LayoutRes
import androidx.annotation.VisibleForTesting
import com.wealthfront.magellan.compose.lifecycle.LifecycleComponent
import com.wealthfront.magellan.compose.lifecycle.LifecycleOwner
import com.wealthfront.magellan.compose.lifecycle.lifecycle
import com.wealthfront.magellan.compose.transition.Displayable

abstract class ViewWrapper(
  @LayoutRes val layoutRes: Int
) : Displayable, LifecycleComponent() {

  final override var view: View? by lifecycleView { inflate(it, layoutRes, null) }
    @VisibleForTesting set

  final override fun onShow(context: Context) {
    onShow(context, view!!)
  }

  final override fun onResume(context: Context) {
    onResume(context, view!!)
  }

  final override fun onPause(context: Context) {
    onPause(context, view!!)
  }

  final override fun onHide(context: Context) {
    onHide(context, view!!)
  }

  protected open fun onShow(context: Context, view: View) {}

  protected open fun onResume(context: Context, view: View) {}

  protected open fun onPause(context: Context, view: View) {}

  protected open fun onHide(context: Context, view: View) {}
}

// An easy wrapper to keep using customViews
class CustomViewWrapper<CustomView : View>(
  val createView: (Context) -> CustomView
) : LifecycleComponent() {
  var view: CustomView? = null
    private set

  override fun onShow(context: Context) {
    view = createView(context)
  }

  override fun onHide(context: Context) {
    view = null
  }
}

fun <CustomView : View> LifecycleOwner.lifecycleView(createView: (Context) -> CustomView) =
  lifecycle(CustomViewWrapper(createView), { it.view })
