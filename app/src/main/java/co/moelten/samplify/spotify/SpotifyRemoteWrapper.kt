package co.moelten.samplify.spotify

import android.content.Context
import android.graphics.Bitmap
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.PlayerApi
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.protocol.client.CallResult
import com.spotify.protocol.client.PendingResult
import com.spotify.protocol.types.ImageUri
import com.spotify.protocol.types.PlayerState
import com.wealthfront.magellan.compose.lifecycle.LifecycleAware
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel.Factory.CONFLATED
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.callbackFlow
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
) : LifecycleAware {

  private val remoteMutex = Mutex()
  private var _appRemote: SpotifyAppRemote? = null

  val player = Player { getAppRemote().playerApi }

  private suspend fun getAppRemote(): SpotifyAppRemote {
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
  fun getPlayerState(): Flow<PlayerState> =
    callbackFlow<PlayerState> {
      val call = getAppRemote().playerApi.subscribeToPlayerState()
        .setEventCallback { playerState ->
          sendBlocking(playerState)
        }
        .setErrorCallback { throwable ->
          cancel(CancellationException("App remote error", throwable))
        }
      awaitClose {
        call.cancelSafely(_appRemote)
      }
    }.buffer(CONFLATED)

  suspend fun getImage(uri: ImageUri): Bitmap = getAppRemote().imagesApi.getImage(uri).awaitAsync()

  override fun hide(context: Context) {
    disconnect()
  }

  fun disconnect() {
    SpotifyAppRemote.disconnect(_appRemote)
    _appRemote = null
  }
}

class Player(
  val getPlayerApi: suspend () -> PlayerApi
) {
  suspend fun play(uri: String) = getPlayerApi().play(uri).awaitAsync()
  suspend fun play() = getPlayerApi().resume().awaitAsync()
  suspend fun pause() = getPlayerApi().pause().awaitAsync()
  suspend fun next() = getPlayerApi().skipNext().awaitAsync()
  suspend fun previous() = getPlayerApi().skipPrevious().awaitAsync()
  suspend fun playOrPause() {
    val state = getPlayerApi().playerState.awaitAsync()
    if (state.isPaused) {
      play()
    } else {
      pause()
    }
  }
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

fun PendingResult<*>.cancelSafely(appRemote: SpotifyAppRemote?) {
  if (appRemote != null && appRemote.isConnected) {
    cancel()
  }
}
