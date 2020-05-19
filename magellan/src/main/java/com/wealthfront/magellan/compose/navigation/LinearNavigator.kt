package com.wealthfront.magellan.compose.navigation

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.view.ViewGroup
import androidx.core.view.doOnLayout
import com.wealthfront.magellan.compose.lifecycle.LifecycleComponent
import com.wealthfront.magellan.compose.lifecycle.LifecycleState
import com.wealthfront.magellan.compose.lifecycle.LifecycleState.Created
import com.wealthfront.magellan.compose.lifecycle.LifecycleState.Destroyed
import com.wealthfront.magellan.compose.lifecycle.LifecycleState.Resumed
import com.wealthfront.magellan.compose.lifecycle.LifecycleState.Shown
import com.wealthfront.magellan.compose.navigation.Direction.BACKWARD
import com.wealthfront.magellan.compose.navigation.Direction.FORWARD
import com.wealthfront.magellan.compose.transition.DefaultTransition
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
    val currentTransitionData = currentNavigable?.let { currentNavigable ->
      val currentTransitionData = currentNavigable.transitionData!!
      removeFromLifecycle(currentNavigable, detachedState = when (direction) {
        FORWARD -> getEarlierOfCurrentStateAndCreated()
        BACKWARD -> Destroyed
      })
      if (direction == FORWARD) {
        backstack.add(currentNavigable)  // TODO: Allow historyRewriters and other ways of representing the backstack
      }
      currentTransitionData
    }
    attachToLifecycle(nextNavigable)
    currentNavigable = nextNavigable
    when (currentState) {
      is Shown, is Resumed -> {
        val navigationContainer = getNavigationContainerWhenShown()
        val nextTransitionData = nextNavigable.transitionData!!
        navigationContainer.addView(nextTransitionData.frame)

        val transition = when (direction) {
          FORWARD -> nextNavigable.preferredTransition ?: DefaultTransition()
          BACKWARD -> currentTransitionData?.preferredTransition ?: DefaultTransition()
        }
        nextNavigable.view!!.doOnLayout {
          val animator = transition.createAnimator(currentTransitionData, nextTransitionData, direction)
          animator.addListener(object: AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
              if (currentTransitionData != null) {
                navigationContainer.removeView(currentTransitionData.frame)
              }
            }
          })
          animator.start()
        }
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
