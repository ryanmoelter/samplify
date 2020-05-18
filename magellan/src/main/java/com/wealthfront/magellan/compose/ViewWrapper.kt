package com.wealthfront.magellan.compose

import android.content.Context
import android.view.View
import android.view.View.inflate
import androidx.annotation.LayoutRes
import com.wealthfront.magellan.compose.lifecycle.LifecycleComponent
import com.wealthfront.magellan.compose.lifecycle.LifecycleOwner
import com.wealthfront.magellan.compose.lifecycle.lifecycleAttached

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

fun <CustomView : View> LifecycleOwner.lifecycleAttachedView(createView: (Context) -> CustomView) =
  lifecycleAttached(CustomViewWrapper(createView), { it.view })

fun LifecycleOwner.lifecycleAttachedView(@LayoutRes layoutRes: Int) =
  lifecycleAttached(CustomViewWrapper { inflate(it, layoutRes, null) }, { it.view })
