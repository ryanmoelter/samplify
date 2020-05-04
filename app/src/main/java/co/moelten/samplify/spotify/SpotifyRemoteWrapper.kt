package co.moelten.samplify.spotify

import android.content.Context
import android.graphics.Bitmap
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
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
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

const val CLIENT_ID = "e7ee02a6c50f4e8b8690bbb6b974db78"
const val REDIRECT_URI = "samplify://authenticated"

class SpotifyRemoteWrapper @Inject constructor() : LifecycleAware {

  lateinit var appRemote: SpotifyAppRemote

  suspend fun connect(context: Context) {
    val connectionParams = ConnectionParams.Builder(CLIENT_ID)
      .setRedirectUri(REDIRECT_URI)
      .showAuthView(true)
      .build()

    return suspendCoroutine { continuation ->
      SpotifyAppRemote.connect(context, connectionParams,
        object : Connector.ConnectionListener {
          override fun onConnected(spotifyAppRemote: SpotifyAppRemote) {
            appRemote = spotifyAppRemote
            continuation.resume(Unit)
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
      appRemote.playerApi.subscribeToPlayerState()
        .setEventCallback { playerState ->
          sendBlocking(playerState)
        }
        .setErrorCallback { throwable ->
          cancel(CancellationException("App remote error", throwable))
        }
      awaitClose()
    }.buffer(CONFLATED)

  suspend fun getImage(uri: ImageUri): Bitmap = suspendCoroutine { continuation ->
    appRemote.imagesApi.getImage(uri)
      .setResultCallback { bitmap -> continuation.resume(bitmap) }
      .setErrorCallback { throwable -> continuation.resumeWithException(throwable) }
  }

  fun disconnect() {
    SpotifyAppRemote.disconnect(appRemote)
  }
}
