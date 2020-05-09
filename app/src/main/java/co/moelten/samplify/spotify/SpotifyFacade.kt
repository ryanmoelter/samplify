package co.moelten.samplify.spotify

import co.moelten.samplify.model.Loadable
import com.spotify.protocol.types.PlayerState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow

interface SpotifyFacade {
  val spotifyRemoteWrapper: SpotifyRemoteWrapper

  @OptIn(ExperimentalCoroutinesApi::class)
  val playerState: StateFlow<Loadable<PlayerState>>
    get() = spotifyRemoteWrapper.playerState

  suspend fun getAppRemote() = spotifyRemoteWrapper.getAppRemote()
}