package com.wealthfront.magellan.compose.transition

import android.view.View
import com.wealthfront.magellan.compose.lifecycle.LifecycleAware
import com.wealthfront.magellan.compose.lifecycle.LifecycleState.Created
import com.wealthfront.magellan.compose.lifecycle.LifecycleState.Destroyed
import com.wealthfront.magellan.compose.lifecycle.LifecycleState.Resumed
import com.wealthfront.magellan.compose.lifecycle.LifecycleState.Shown

interface Displayable : LifecycleAware {
  val view: View?
  val preferredTransition: NavigationTransition? get() = null
  val transitionElements: List<View>?

  val transitionData: TransitionData? get() = when (currentState) {
    is Destroyed, is Created -> null
    is Shown, is Resumed -> TransitionData(view!!, transitionElements!!, preferredTransition)
  }

  fun transitionStarted() {}

  fun transitionFinished() {}
}

interface DelegatedDisplayable : Displayable {
  val displayable: Displayable

  override val view: View?
    get() = displayable.view
  override val preferredTransition: NavigationTransition?
    get() = super.preferredTransition
  override val transitionElements
    get() = displayable.transitionElements

  override fun transitionStarted() = displayable.transitionStarted()

  override fun transitionFinished() = displayable.transitionFinished()
}
