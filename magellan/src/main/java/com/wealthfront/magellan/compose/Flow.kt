package com.wealthfront.magellan.compose

import com.wealthfront.magellan.compose.lifecycle.LifecyclePropagator
import com.wealthfront.magellan.compose.navigation.Navigable

abstract class Flow : Navigable, LifecyclePropagator()
