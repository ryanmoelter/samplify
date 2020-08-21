package com.wealthfront.magellan.compose.lifecycle

import android.content.Context
import com.wealthfront.magellan.compose.lifecycle.LifecycleState.Created
import com.wealthfront.magellan.compose.lifecycle.LifecycleState.Destroyed
import com.wealthfront.magellan.compose.lifecycle.LifecycleState.Resumed
import com.wealthfront.magellan.compose.lifecycle.LifecycleState.Shown

sealed class LifecycleState(val order: Int) {
  object Destroyed : LifecycleState(0)
  data class Created(val context: Context) : LifecycleState(1)
  data class Shown(val context: Context) : LifecycleState(2)
  data class Resumed(val context: Context) : LifecycleState(3)
}

val LifecycleState.context: Context? get() = when (this) {
  Destroyed -> null
  is Created -> context
  is Shown -> context
  is Resumed -> context
}

internal fun LifecycleState.previous(context: Context): LifecycleState = when (this) {
  Destroyed -> throw IllegalArgumentException("No previous state before Destroyed")
  is Created -> Destroyed
  is Shown -> Created(context)
  is Resumed -> Shown(context)
}

internal fun LifecycleState.next(context: Context): LifecycleState = when (this) {
  Destroyed -> Created(context)
  is Created -> Shown(context)
  is Shown -> Resumed(context)
  is Resumed -> throw IllegalArgumentException("No next state after Resumed")
}

internal fun LifecycleState.getTheDirectionIShouldGoToGetTo(other: LifecycleState) = when {
  order > other.order -> LifecycleStateDirection.BACKWARDS
  order == other.order -> LifecycleStateDirection.NO_MOVEMENT
  order < other.order -> LifecycleStateDirection.FORWARD
  else -> throw IllegalStateException("Unhandled order comparison: this is $order and other is ${other.order}")
}

internal enum class LifecycleStateDirection {
  FORWARD, BACKWARDS, NO_MOVEMENT
}
