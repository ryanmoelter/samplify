package com.wealthfront.magellan.compose.navigation

import android.content.Context
import android.view.ViewGroup
import com.wealthfront.magellan.compose.lifecycle.LifecyclePropagator
import com.wealthfront.magellan.compose.lifecycle.LifecycleState.Created
import com.wealthfront.magellan.compose.lifecycle.LifecycleState.Destroyed
import com.wealthfront.magellan.compose.lifecycle.LifecycleState.Resumed
import com.wealthfront.magellan.compose.lifecycle.LifecycleState.Shown
import com.wealthfront.magellan.compose.lifecycle.transitionBetweenStates
import com.wealthfront.magellan.compose.navigation.Direction.BACKWARD
import com.wealthfront.magellan.compose.navigation.Direction.FORWARD
import java.util.*

class LinearNavigator(
  val getNavigationContainer: () -> ViewGroup
) : LifecyclePropagator() {
  var currentNavigable: Navigable? = null
    private set

  private val backstack: Deque<Navigable> = LinkedList()
  val context: Context? get() = when (val currentState = currentState) {
    is Destroyed -> null
    is Created -> currentState.context
    is Shown -> currentState.context
    is Resumed -> currentState.context
  }

  fun goTo(nextNavigable: Navigable, direction: Direction = FORWARD) {
    when (currentState) {
      is Destroyed, is Created -> throw IllegalStateException("Cannot navigate when not shown")
      is Shown, is Resumed -> {
        val currentView = currentNavigable?.let { currentNavigable ->
          val currentView = currentNavigable.view!!
          removeFromLifecycle(currentNavigable, when (direction) {
            FORWARD -> Created(context!!)
            BACKWARD -> Destroyed
          })
          if (direction == FORWARD) {
            backstack.add(currentNavigable)  // TODO: Allow historyRewriters and other ways of representing the backstack
          }
          currentView
        }
        nextNavigable.transitionBetweenStates(when (direction) {
          FORWARD -> Destroyed
          BACKWARD -> Created(context!!)
        }, Shown(context!!))
        val navigationContainer = getNavigationContainer()
        navigationContainer.addView(nextNavigable.view!!)
        // TODO: Transition
        if (currentView != null) {
          navigationContainer.removeView(currentView)
        }
        attachToLifecycle(nextNavigable, Shown(context!!))
        currentNavigable = nextNavigable
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
