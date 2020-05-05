package com.wealthfront.magellan.compose.lifecycle

import android.content.Context
import com.wealthfront.magellan.compose.lifecycle.LifecycleState.Created
import com.wealthfront.magellan.compose.lifecycle.LifecycleState.Destroyed
import com.wealthfront.magellan.compose.lifecycle.LifecycleState.Resumed
import com.wealthfront.magellan.compose.lifecycle.LifecycleState.Shown

/** The FSM for the LifecycleOwner */
class LifecycleFSM : LifecycleAware {

  var listeners: List<LifecycleAware> = emptyList()

  var currentState: LifecycleState = Destroyed
    private set(newState) {
      val oldState = field
      field = newState
      notifyStateChanged(oldState, newState)
    }

  private fun notifyStateChanged(
    oldState: LifecycleState,
    newState: LifecycleState
  ) {
    listeners.transitionBetweenStates(oldState, newState)
  }

  fun attachToLifecycle(lifecycleAware: LifecycleAware, detachedState: LifecycleState = Destroyed) {
    lifecycleAware.transitionBetweenStates(detachedState, currentState)
    listeners = listeners + lifecycleAware
  }

  fun removeFromLifecycle(
    lifecycleAware: LifecycleAware,
    detachedState: LifecycleState = Destroyed
  ) {
    listeners = listeners - lifecycleAware
    lifecycleAware.transitionBetweenStates(currentState, detachedState)
  }

  override fun create(context: Context) {
    currentState = Created(context)
  }

  override fun show(context: Context) {
    currentState = Shown(context)
  }

  override fun resume(context: Context) {
    currentState = Resumed(context)
  }

  override fun pause(context: Context) {
    currentState = Shown(context)
  }

  override fun hide(context: Context) {
    currentState = Created(context)
  }

  override fun destroy(context: Context) {
    currentState = Destroyed
  }

  override fun backPressed(): Boolean = onAllListenersUntilTrue { it.backPressed() }

  private fun onAllListeners(action: (LifecycleAware) -> Unit) = listeners.forEach(action)

  private fun onAllListenersUntilTrue(action: (LifecycleAware) -> Boolean): Boolean =
    listeners.asSequence().map(action).any { it }
}
