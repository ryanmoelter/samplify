package co.moelten.samplify.spotify

import android.content.Context
import co.moelten.samplify.model.Loadable
import co.moelten.samplify.model.Loadable.Loading
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.protocol.client.CallResult
import com.spotify.protocol.client.PendingResult
import com.spotify.protocol.types.PlayerState
import com.wealthfront.magellan.compose.coroutine.CreatedCoroutineScope
import com.wealthfront.magellan.compose.lifecycle.LifecyclePropagator
import com.wealthfront.magellan.compose.lifecycle.lifecycle
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel.Factory.CONFLATED
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

const val CLIENT_ID = "e7ee02a6c50f4e8b8690bbb6b974db78"
const val REDIRECT_URI = "samplify://authenticated"

@Singleton
class SpotifyRemoteWrapper @Inject constructor(
  val applicationContext: Context
) : LifecyclePropagator() {

  private val createdScope by lifecycle(CreatedCoroutineScope())

  private val remoteMutex = Mutex()
  private var _appRemote: SpotifyAppRemote? = null

  @OptIn(ExperimentalCoroutinesApi::class)
  val playerState = MutableStateFlow<Loadable<PlayerState>>(Loading())

  @OptIn(ExperimentalCoroutinesApi::class)
  override fun onShow(context: Context) {
    createdScope.launch {
      getPlayerState().collect { playerState: PlayerState ->
        this@SpotifyRemoteWrapper.playerState.value = Loadable.Success(playerState)
      }
    }
  }

  override fun onHide(context: Context) {
    SpotifyAppRemote.disconnect(_appRemote)
    _appRemote = null
  }

  suspend fun getAppRemote(): SpotifyAppRemote {
    if (_appRemote == null) {
      remoteMutex.withLock {
        if (_appRemote == null) {
          _appRemote = connect(applicationContext)
        }
      }
    }
    return _appRemote!!
  }

  private suspend fun connect(context: Context): SpotifyAppRemote {
    val connectionParams = ConnectionParams.Builder(CLIENT_ID)
      .setRedirectUri(REDIRECT_URI)
      .showAuthView(true)
      .build()

    return suspendCoroutine { continuation ->
      SpotifyAppRemote.connect(context, connectionParams,
        object : Connector.ConnectionListener {
          override fun onConnected(spotifyAppRemote: SpotifyAppRemote) {
            continuation.resume(spotifyAppRemote)
          }

          override fun onFailure(throwable: Throwable) {
            continuation.resumeWithException(throwable)
          }
        })
    }
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  private fun getPlayerState(): Flow<PlayerState> =
    callbackFlow<PlayerState> {
      val appRemote = getAppRemote()
      val call = appRemote.playerApi.subscribeToPlayerState()
        .setEventCallback { playerState ->
          sendBlocking(playerState)
        }
        .setErrorCallback { throwable ->
          close(CancellationException("App remote error", throwable))
        }
      awaitClose {
        call.cancelIfConnected(appRemote)
      }
    }.buffer(CONFLATED)
}

suspend fun <Data> CallResult<Data>.awaitAsync(): Data =
  suspendCancellableCoroutine { cancellableContinuation ->
    setResultCallback { data ->
      cancellableContinuation.resume(data)
    }
    setErrorCallback { throwable ->
      cancellableContinuation.resumeWithException(throwable)
    }
    cancellableContinuation.invokeOnCancellation { cancel() }
  }

fun PendingResult<*>.cancelIfConnected(appRemote: SpotifyAppRemote) {
  if (appRemote.isConnected) {
    cancel()
  }
}
