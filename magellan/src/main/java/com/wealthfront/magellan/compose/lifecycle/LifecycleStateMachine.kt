package com.wealthfront.magellan.compose.lifecycle

import android.content.Context
import com.wealthfront.magellan.compose.lifecycle.LifecycleState.Created
import com.wealthfront.magellan.compose.lifecycle.LifecycleState.Destroyed
import com.wealthfront.magellan.compose.lifecycle.LifecycleState.Resumed
import com.wealthfront.magellan.compose.lifecycle.LifecycleState.Shown
import com.wealthfront.magellan.compose.lifecycle.LifecycleStateDirection.BACKWARDS
import com.wealthfront.magellan.compose.lifecycle.LifecycleStateDirection.FORWARD
import com.wealthfront.magellan.compose.lifecycle.LifecycleStateDirection.NO_MOVEMENT

fun Iterable<LifecycleAware>.transitionBetweenStates(
  oldState: LifecycleState,
  newState: LifecycleState
) {
  transitionBetweenLifecycleStates(
    this, oldState, newState
  )
}

fun LifecycleAware.transitionBetweenStates(
  oldState: LifecycleState,
  newState: LifecycleState
) {
  transitionBetweenLifecycleStates(
    listOf(this), oldState, newState
  )
}

private fun transitionBetweenLifecycleStates(
  subjects: Iterable<LifecycleAware>,
  oldState: LifecycleState,
  newState: LifecycleState
) {
  var currentState = oldState
  while (currentState != newState) {
    currentState = when (currentState.getTheDirectionIShouldGoToGetTo(newState)) {
      FORWARD -> next(
        subjects = subjects,
        currentState = currentState,
        context = newState.context!!
      )
      BACKWARDS -> previous(
        subjects = subjects,
        currentState = currentState,
        context = getContext(
          newState = newState,
          oldState = oldState
        )
      )
      NO_MOVEMENT ->
        throw IllegalStateException("Attempting to transition from $currentState to itself")
    }
  }
}

private fun getContext(newState: LifecycleState, oldState: LifecycleState) = newState.context ?: oldState.context!!

private fun next(
  subjects: Iterable<LifecycleAware>,
  currentState: LifecycleState,
  context: Context
): LifecycleState {
  return when (currentState) {
    is Destroyed -> {
      subjects.forEach { it.create(context) }
      Created(context)
    }
    is Created -> {
      subjects.forEach { it.show(context) }
      Shown(context)
    }
    is Shown -> {
      subjects.forEach { it.resume(context) }
      Resumed(context)
    }
    is Resumed -> {
      throw IllegalStateException("Cannot go forward from resumed")
    }
  }
}

private fun previous(
  subjects: Iterable<LifecycleAware>,
  currentState: LifecycleState,
  context: Context
): LifecycleState {
  return when (currentState) {
    is Destroyed -> {
      throw IllegalStateException("Cannot go backward from destroyed")
    }
    is Created -> {
      subjects.forEach { it.destroy(context) }
      Destroyed
    }
    is Shown -> {
      subjects.forEach { it.hide(context) }
      Created(context)
    }
    is Resumed -> {
      subjects.forEach { it.pause(context) }
      Shown(context)
    }
  }
}

val LifecycleState.context: Context? get() = when (this) {
  Destroyed -> null
  is Created -> context
  is Shown -> context
  is Resumed -> context
}

fun LifecycleState.getTheDirectionIShouldGoToGetTo(other: LifecycleState) = when {
  order > other.order -> BACKWARDS
  order == other.order -> NO_MOVEMENT
  order < other.order -> FORWARD
  else -> throw IllegalStateException("Unhandled order comparison: this is $order and other is ${other.order}")
}

enum class LifecycleStateDirection {
  FORWARD, BACKWARDS, NO_MOVEMENT
}
