package com.wealthfront.magellan.compose

import com.wealthfront.magellan.compose.lifecycle.LifecycleComponent
import com.wealthfront.magellan.compose.transition.Displayable

abstract class Screen : Displayable, LifecycleComponent()
