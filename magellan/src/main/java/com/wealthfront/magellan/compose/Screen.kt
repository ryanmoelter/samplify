package com.wealthfront.magellan.compose

import com.wealthfront.magellan.compose.lifecycle.LifecycleComponent
import com.wealthfront.magellan.compose.navigation.Navigable

abstract class Screen : Navigable, LifecycleComponent()
