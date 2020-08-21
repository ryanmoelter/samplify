package com.wealthfront.magellan.compose.lifecycle

import android.content.Context
import com.wealthfront.magellan.compose.lifecycle.LifecycleState.Created
import com.wealthfront.magellan.compose.lifecycle.LifecycleState.Destroyed
import com.wealthfront.magellan.compose.lifecycle.LifecycleState.Resumed
import com.wealthfront.magellan.compose.lifecycle.LifecycleState.Shown
import com.wealthfront.magellan.compose.lifecycle.LifecycleStateDirection.BACKWARDS
import com.wealthfront.magellan.compose.lifecycle.LifecycleStateDirection.FORWARD
import com.wealthfront.magellan.compose.lifecycle.LifecycleStateDirection.NO_MOVEMENT

abstract class LifecycleComponent : LifecycleAware, LifecycleOwner {

  private val children = mutableListOf<LifecycleAware>()

  final override var currentState: LifecycleState = Destroyed
    set(newState) {
      var workingState: LifecycleState
      val direction = field.getTheDirectionIShouldGoToGetTo(newState)
      while (field != newState) {
        when (direction) {
          FORWARD -> {
            workingState = field.next(newState.context!!)
            children.forEach { it.currentState = workingState }
            transitionForwardTo(workingState)
            field = workingState
          }
          BACKWARDS -> {
            workingState = field.previous(field.context!!)
            transitionBackwardFrom(field)
            field = workingState
            children.reversed().forEach { it.currentState = workingState }
          }
          NO_MOVEMENT -> throw IllegalStateException("No movement between $this and $newState")
        }.let { }
      }
    }

  private fun transitionForwardTo(newState: LifecycleState) {
    when (newState) {
      Destroyed -> throw IllegalArgumentException("Cannot transition forward to Destroyed")
      is Created -> onCreate(newState.context)
      is Shown -> onShow(newState.context)
      is Resumed -> onResume(newState.context)
    }
  }

  private fun transitionBackwardFrom(oldState: LifecycleState) {
    when (oldState) {
      Destroyed -> throw IllegalArgumentException("Cannot transition backward from Destroyed")
      is Created -> onDestroy(oldState.context)
      is Shown -> onHide(oldState.context)
      is Resumed -> onPause(oldState.context)
    }
  }

  final override fun backPressed(): Boolean =
    children.asSequence().map { it.backPressed() }.any { it } || onBackPressed()

  override fun attachToLifecycle(lifecycleAware: LifecycleAware) {
    lifecycleAware.currentState = currentState
    children += lifecycleAware
  }

  override fun removeFromLifecycle(lifecycleAware: LifecycleAware, detachedState: LifecycleState) {
    lifecycleAware.currentState = detachedState
    children -= lifecycleAware
  }

  protected open fun onCreate(context: Context) {}

  protected open fun onShow(context: Context) {}

  protected open fun onResume(context: Context) {}

  protected open fun onPause(context: Context) {}

  protected open fun onHide(context: Context) {}

  protected open fun onDestroy(context: Context) {}

  protected open fun onBackPressed(): Boolean = false
}
