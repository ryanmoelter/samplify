package co.moelten.samplify.spotify

import android.graphics.Bitmap
import co.moelten.samplify.model.Loadable
import co.moelten.samplify.model.mapLoadableValue
import com.spotify.protocol.types.ImageUri
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class NowPlayingFacade @Inject constructor(
  override val spotifyRemoteWrapper: SpotifyRemoteWrapper
) : SpotifyFacade {

  val trackFlow
    get() = playerState
      .mapLoadableValue { playerState -> playerState.track }

  @OptIn(ExperimentalCoroutinesApi::class)
  val albumArtFlow
    get() = trackFlow
      .mapLoadableValue { track -> track.imageUri }
      .distinctUntilChanged()
      .flatMapLatest { loadable ->
        flow {
          when (loadable) {
            is Loadable.Success -> {
              emit(Loadable.Loading())
              emit(Loadable.Success(getImage(loadable.value)))
            }
            is Loadable.Loading -> {
              emit(Loadable.Loading())
            }
            is Loadable.Error -> {
              emit(Loadable.Error<Bitmap>(loadable.throwable))
            }
          }
        }
      }

  suspend fun getImage(uri: ImageUri): Bitmap = getAppRemote().imagesApi.getImage(uri).awaitAsync()

  suspend fun play(uri: String) = getAppRemote().playerApi.play(uri).awaitAsync()
  suspend fun play() = getAppRemote().playerApi.resume().awaitAsync()
  suspend fun pause() = getAppRemote().playerApi.pause().awaitAsync()
  suspend fun next() = getAppRemote().playerApi.skipNext().awaitAsync()
  suspend fun previous() = getAppRemote().playerApi.skipPrevious().awaitAsync()
  suspend fun playOrPause() {
    val state = getAppRemote().playerApi.playerState.awaitAsync()
    if (state.isPaused) {
      play()
    } else {
      pause()
    }
  }
}

fun SpotifyFacade.toNowPlayingFacade() = NowPlayingFacade(spotifyRemoteWrapper)
