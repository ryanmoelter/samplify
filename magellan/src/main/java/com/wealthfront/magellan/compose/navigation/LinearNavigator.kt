package com.wealthfront.magellan.compose.navigation

import android.content.Context
import android.view.ViewGroup
import com.wealthfront.magellan.compose.lifecycle.LifecycleComponent
import com.wealthfront.magellan.compose.lifecycle.LifecycleState
import com.wealthfront.magellan.compose.lifecycle.LifecycleState.Created
import com.wealthfront.magellan.compose.lifecycle.LifecycleState.Destroyed
import com.wealthfront.magellan.compose.lifecycle.LifecycleState.Resumed
import com.wealthfront.magellan.compose.lifecycle.LifecycleState.Shown
import com.wealthfront.magellan.compose.navigation.Direction.BACKWARD
import com.wealthfront.magellan.compose.navigation.Direction.FORWARD
import java.util.Deque
import java.util.LinkedList

class LinearNavigator(
  val getNavigationContainerWhenShown: () -> ViewGroup
) : LifecycleComponent() {
  var currentNavigable: Navigable? = null
    private set

  private val backstack: Deque<Navigable> = LinkedList()
  val context: Context? get() = when (val currentState = currentState) {
    is Destroyed -> null
    is Created -> currentState.context
    is Shown -> currentState.context
    is Resumed -> currentState.context
  }

  fun getEarlierOfCurrentStateAndCreated(): LifecycleState = when (currentState) {
    Destroyed -> Destroyed
    is Created, is Shown, is Resumed -> Created(context!!)
  }

  fun goTo(nextNavigable: Navigable, direction: Direction = FORWARD) {
    val currentView = currentNavigable?.let { currentNavigable ->
      val currentView = currentNavigable.view
      removeFromLifecycle(currentNavigable, detachedState = when (direction) {
        FORWARD -> getEarlierOfCurrentStateAndCreated()
        BACKWARD -> Destroyed
      })
      if (direction == FORWARD) {
        backstack.add(currentNavigable)  // TODO: Allow historyRewriters and other ways of representing the backstack
      }
      currentView
    }
    attachToLifecycle(nextNavigable)
    currentNavigable = nextNavigable
    when (currentState) {
      is Shown, is Resumed -> {
        val navigationContainer = getNavigationContainerWhenShown()
        navigationContainer.addView(nextNavigable.view!!)
        // TODO: Transition
        if (currentView != null) {
          navigationContainer.removeView(currentView)
        }
        Unit
      }
      is Destroyed, is Created -> { }
    }.let { }
  }

  private fun canGoBack() = backstack.isNotEmpty()

  override fun onCreate(context: Context) {
    backstack.forEach { it.currentState = Created(context) }
  }

  override fun onShow(context: Context) {
    currentNavigable?.let { currentNavigable ->
      getNavigationContainerWhenShown().addView(currentNavigable.view!!)
    }
  }

  override fun onDestroy(context: Context) {
    backstack.forEach { it.currentState = Destroyed }
  }

  override fun onBackPressed(): Boolean = currentNavigable?.backPressed() ?: false || goBack()

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
