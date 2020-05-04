package com.wealthfront.magellan.compose.lifecycle

import android.content.Context

sealed class LifecycleState(val order: Int) {
  object Destroyed : LifecycleState(0)
  data class Created(val context: Context) : LifecycleState(1)
  data class Shown(val context: Context) : LifecycleState(2)
  data class Resumed(val context: Context) : LifecycleState(3)
}
