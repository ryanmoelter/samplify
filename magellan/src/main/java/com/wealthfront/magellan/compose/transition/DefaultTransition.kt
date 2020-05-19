package com.wealthfront.magellan.compose.transition

import android.animation.Animator
import com.wealthfront.blend.Blend
import com.wealthfront.magellan.compose.navigation.Direction
import java.util.concurrent.TimeUnit.MILLISECONDS

class DefaultTransition : NavigationTransition {
  val blend = Blend()

  override fun createAnimator(
    from: TransitionData?,
    to: TransitionData,
    direction: Direction
  ): Animator {
    val fromElements = from?.elements ?: emptyList()

    // val startingTranslationOut = 0f
    val targetTranslationOut = when (direction) {
      Direction.FORWARD -> -(from?.frame?.width ?: 0)
      Direction.BACKWARD -> from!!.frame.width
    }.toFloat()

    val startingTranslationIn = when (direction) {
      Direction.FORWARD -> to.frame.width
      Direction.BACKWARD -> -to.frame.width
    }.toFloat()
    val targetTranslationIn = 0f

    val fromStaggerTimeMillis = if (fromElements.isNotEmpty()) {
      100 / fromElements.size
    } else {
      0
    }.toLong()

    val toStaggerTimeMillis = if (to.elements.isNotEmpty()) {
      100 / to.elements.size
    } else {
      0
    }.toLong()

    return blend {
      immediate()
      target(to.elements).animations {
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
      stagger(to.elements, toStaggerTimeMillis, MILLISECONDS) { currentView ->
        target(currentView).animations {
          translationX(targetTranslationIn)
        }
      }
    }.prepare()
  }
}

