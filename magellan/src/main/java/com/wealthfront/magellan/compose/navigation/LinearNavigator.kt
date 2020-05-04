package com.wealthfront.magellan.compose.navigation

import android.content.Context
import android.view.ViewGroup
import com.wealthfront.magellan.compose.navigation.Direction.BACKWARD
import com.wealthfront.magellan.compose.navigation.Direction.FORWARD
import com.wealthfront.magellan.compose.lifecycle.LifecycleState.Created
import com.wealthfront.magellan.compose.lifecycle.LifecycleState.Destroyed
import com.wealthfront.magellan.compose.lifecycle.LifecycleState.Resumed
import com.wealthfront.magellan.compose.lifecycle.LifecycleState.Shown
import com.wealthfront.magellan.compose.lifecycle.LifecycleAware
import com.wealthfront.magellan.compose.lifecycle.LifecyclePropagator
import com.wealthfront.magellan.compose.lifecycle.transitionBetweenStates
import java.util.Deque
import java.util.LinkedList

class LinearNavigator(
  val getNavigationContainer: () -> ViewGroup
) : LifecycleAware, LifecyclePropagator() {
  var currentNavigable: Navigable? = null
    private set

  private val backstack: Deque<Navigable> = LinkedList()
  val context: Context? = when (val currentState = currentState) {
    is Destroyed -> null
    is Created -> currentState.context
    is Shown -> currentState.context
    is Resumed -> currentState.context
  }

  fun goTo(nextNavigable: Navigable, direction: Direction = FORWARD) {
    when (currentState) {
      is Destroyed, is Created -> throw IllegalStateException("Cannot navigate when not shown")
      is Shown, is Resumed -> {
        val currentView = currentNavigable?.let {
          val currentNavigable = currentNavigable!!
          val currentView = currentNavigable.view!!
          removeFromLifecycle(currentNavigable, Created(context!!))
          if (direction == FORWARD) {
            backstack.add(currentNavigable)  // TODO: Allow historyRewriters and other ways of representing the backstack
          }
          currentView
        }
        nextNavigable.transitionBetweenStates(Destroyed, Shown(context!!))
        val navigationContainer = getNavigationContainer()
        navigationContainer.addView(nextNavigable.view!!)
        // TODO: Transition
        if (currentView != null) {
          navigationContainer.removeView(currentView)
        }
        attachToLifecycle(nextNavigable)
      }
    }.let { }
  }

  fun canGoBack() = backstack.isNotEmpty()

  override fun onDestroy(context: Context) {
    backstack.transitionBetweenStates(Created(context), Destroyed)
  }

  override fun onBackPressed(): Boolean {
    return currentNavigable?.backPressed() ?: false || goBack()
  }

  fun goBack(): Boolean {
    return if (canGoBack()) {
      goTo(backstack.pop(), BACKWARD)
      true
    } else {
      false
    }
  }
}

enum class Direction {
  FORWARD, BACKWARD
}
