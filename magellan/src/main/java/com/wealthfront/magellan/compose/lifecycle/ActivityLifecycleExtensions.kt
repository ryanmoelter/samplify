package com.wealthfront.magellan.compose.lifecycle

import android.app.Activity
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.annotation.IdRes
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.wealthfront.magellan.compose.lifecycle.LifecycleState.Created
import com.wealthfront.magellan.compose.lifecycle.LifecycleState.Destroyed
import com.wealthfront.magellan.compose.lifecycle.LifecycleState.Resumed
import com.wealthfront.magellan.compose.lifecycle.LifecycleState.Shown
import com.wealthfront.magellan.compose.transition.Displayable

fun Displayable.attachToActivity(
  context: ComponentActivity,
  @IdRes containerRes: Int
) {
  context.lifecycle.addObserver(ActivityLifecycleAdapter(this, context, containerRes))
}

class ActivityLifecycleAdapter(
  private val navigable: Displayable,
  private val context: Activity,
  private val containerRes: Int
) : DefaultLifecycleObserver {
  override fun onStart(owner: LifecycleOwner) {
    navigable.currentState = Shown(context)
    context.findViewById<FrameLayout>(containerRes).addView(navigable.view!!)
  }

  override fun onResume(owner: LifecycleOwner) {
    navigable.currentState = Resumed(context)
  }

  override fun onPause(owner: LifecycleOwner) {
    navigable.currentState = Shown(context)
  }

  override fun onStop(owner: LifecycleOwner) {
    navigable.currentState = Created(context)
    context.findViewById<FrameLayout>(containerRes).removeAllViews()
  }

  override fun onDestroy(owner: LifecycleOwner) {
    if (context.isFinishing) {
      navigable.currentState = Destroyed
    }
  }
}
