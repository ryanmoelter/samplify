package com.wealthfront.magellan.compose.lifecycle

import com.wealthfront.magellan.compose.lifecycle.LifecycleState.Destroyed

interface LifecycleAware {

  var currentState: LifecycleState

  fun backPressed(): Boolean = false
}

interface LifecycleOwner {

  fun attachToLifecycle(lifecycleAware: LifecycleAware)

  fun removeFromLifecycle(lifecycleAware: LifecycleAware, detachedState: LifecycleState = Destroyed)
}
