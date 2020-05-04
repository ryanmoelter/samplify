package com.wealthfront.magellan.compose.lifecycle

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner

fun LifecycleAware.attachToActivity(context: Activity, lifecycle: Lifecycle) {
  lifecycle.addObserver(object : DefaultLifecycleObserver {
    override fun onStart(owner: LifecycleOwner) {
      show(context)
    }

    override fun onResume(owner: LifecycleOwner) {
      resume(context)
    }

    override fun onPause(owner: LifecycleOwner) {
      pause(context)
    }

    override fun onStop(owner: LifecycleOwner) {
      hide(context)
    }
  })
}
