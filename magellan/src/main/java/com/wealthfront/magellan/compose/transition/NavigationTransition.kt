package com.wealthfront.magellan.compose.transition

import android.animation.Animator
import android.view.View
import com.wealthfront.magellan.compose.navigation.Direction

/**
 * Define a transition (animation) between two screens. By default, transitions are implemented by
 * [StaggeredHorizontalTransition]. You can either set a different default one when building your Navigator,
 * set a screen's preferred entry/back transition using [Displayable.preferredTransition], or
 * override a particular transition with (TBD).
 */
interface NavigationTransition {
  /**
   * Animate between 2 [TransitionData] representing screens. The [direction] determines whether we're
   * going from [behind] to [front] ([Direction.FORWARD]) or [front] to [behind] ([Direction.BACKWARD]).
   *
   * @param behind the [TransitionData] of the screen that comes before the other in the backstack
   * @param front the [TransitionData] of the screen that comes after in the backstack
   * @param direction the direction of the navigation, see [Direction]
   */
  fun createAnimator(
    behind: TransitionData?,
    front: TransitionData,
    direction: Direction
  ): Animator
}

data class TransitionData(
  val frame: View,
  val elements: List<View>,
  val preferredTransition: NavigationTransition?
)
