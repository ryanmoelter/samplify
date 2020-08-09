package com.wealthfront.magellan.compose.transition

import android.animation.Animator
import com.wealthfront.blend.Blend
import com.wealthfront.magellan.compose.navigation.Direction
import java.util.concurrent.TimeUnit.MILLISECONDS

class StaggeredVerticalTransition : NavigationTransition {
  val blend = Blend()

  override fun createAnimator(
    behind: TransitionData?,
    front: TransitionData,
    direction: Direction
  ): Animator {
    val fromElements = behind?.elements ?: emptyList()

    // TODO: write this
    val targetTranslationOut = when (direction) {
      Direction.FORWARD -> 0
      Direction.BACKWARD -> behind!!.frame.width
    }.toFloat()

    val startingTranslationIn = when (direction) {
      Direction.FORWARD -> front.frame.width
      Direction.BACKWARD -> -front.frame.width
    }.toFloat()
    val targetTranslationIn = 0f

    val fromStaggerTimeMillis = if (fromElements.isNotEmpty()) {
      100 / fromElements.size
    } else {
      0
    }.toLong()

    val toStaggerTimeMillis = if (front.elements.isNotEmpty()) {
      100 / front.elements.size
    } else {
      0
    }.toLong()

    return blend {
      immediate()
      target(front.elements).animations {
        translationX(startingTranslationIn)
      }
    }.then {
      stagger(fromElements, fromStaggerTimeMillis, MILLISECONDS) { currentView ->
        target(currentView).animations {
          translationX(targetTranslationOut)
        }
      }
    }.with {
      startDelay(50, MILLISECONDS)
      stagger(front.elements, toStaggerTimeMillis, MILLISECONDS) { currentView ->
        target(currentView).animations {
          translationX(targetTranslationIn)
        }
      }
    }.prepare()
  }
}

