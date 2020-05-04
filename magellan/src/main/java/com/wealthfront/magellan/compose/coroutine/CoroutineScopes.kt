package com.wealthfront.magellan.compose.coroutine

import android.content.Context
import com.wealthfront.magellan.compose.lifecycle.LifecycleAware
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import java.util.concurrent.CancellationException
import kotlin.coroutines.CoroutineContext

class ShownCoroutineScope : LifecycleAware, CoroutineScope {
  private var job = SupervisorJob().apply { cancel(CancellationException("Not shown yet")) }
  override val coroutineContext: CoroutineContext get() = job + Dispatchers.Main

  override fun show(context: Context) {
    job = SupervisorJob()
  }

  override fun hide(context: Context) {
    job.cancel(CancellationException("Hidden"))
  }
}

class CreatedCoroutineScope : LifecycleAware, CoroutineScope {
  private var job = SupervisorJob().apply { cancel(CancellationException("Not created yet")) }
  override val coroutineContext: CoroutineContext get() = job + Dispatchers.Main

  override fun create(context: Context) {
    job = SupervisorJob()
  }

  override fun destroy(context: Context) {
    job.cancel(CancellationException("Destroyed"))
  }
}
