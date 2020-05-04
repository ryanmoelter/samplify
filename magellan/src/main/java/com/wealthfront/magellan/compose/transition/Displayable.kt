package com.wealthfront.magellan.compose.transition

import android.view.View

interface Displayable {
  val view: View?
  // TODO: val navigationTransition: NavigationTransition? get() = null
  // TODO: val transitionElements: List<View>, for fancy transitions

  fun transitionStarted() {}

  fun transitionFinished() {}
}

interface DelegatedDisplayable :
  Displayable {
  val displayable: Displayable

  override val view: View? get() = displayable.view
  // TODO: override val navigationTransition get() = displayDelegate.navigationTransition
  // TODO: override val transitionElements get() = displayDelegate.transitionElements

  override fun transitionStarted() = displayable.transitionStarted()

  override fun transitionFinished() = displayable.transitionFinished()
}