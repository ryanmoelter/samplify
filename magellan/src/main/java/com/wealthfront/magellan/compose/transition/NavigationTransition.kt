package com.wealthfront.magellan.compose.transition

import android.animation.Animator
import android.view.View
import com.wealthfront.magellan.compose.navigation.Direction

/**
 * Define a transition (animation) between two screens. By default, transitions are implemented by
 * [DefaultTransition]. You can either set a different default one when building your Navigator,
 * set a screen's preferred entry/back transition using [Displayable.preferredTransition], or
 * override a particular transition with (TBD).
 */
interface NavigationTransition {
  /**
   * Animate between 2 [Displayable]s representing screens.
   *
   * @param from the view of the screen we are coming from
   * @param to the view of the screen we are going to
   * @param direction the direction of the navigation, see [Direction]
   */
  fun createAnimator(
    from: TransitionData?,
    to: TransitionData,
    direction: Direction
  ): Animator
}

data class TransitionData(
  val frame: View,
  val elements: List<View>,
  val preferredTransition: NavigationTransition?
)
