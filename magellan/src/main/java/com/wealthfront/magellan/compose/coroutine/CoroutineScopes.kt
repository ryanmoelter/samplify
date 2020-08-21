package com.wealthfront.magellan.compose.coroutine

import android.content.Context
import com.wealthfront.magellan.compose.lifecycle.LifecycleComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import java.util.concurrent.CancellationException
import kotlin.coroutines.CoroutineContext

class ShownCoroutineScope : LifecycleComponent(), CoroutineScope {
  private var job = SupervisorJob().apply { cancel(CancellationException("Not shown yet")) }
  override val coroutineContext: CoroutineContext get() = job + Dispatchers.Main

  override fun onShow(context: Context) {
    job = SupervisorJob()
  }

  override fun onHide(context: Context) {
    job.cancel(CancellationException("Hidden"))
  }
}

class CreatedCoroutineScope : LifecycleComponent(), CoroutineScope {
  private var job = SupervisorJob().apply { cancel(CancellationException("Not created yet")) }
  override val coroutineContext: CoroutineContext get() = job + Dispatchers.Main

  override fun onCreate(context: Context) {
    job = SupervisorJob()
  }

  override fun onDestroy(context: Context) {
    job.cancel(CancellationException("Destroyed"))
  }
}
