package co.moelten.samplify

import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import co.moelten.samplify.AppComponentProvider.injector
import co.moelten.samplify.spotify.SpotifyRemoteWrapper
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.protocol.types.Track
import com.wealthfront.magellan.compose.Screen
import com.wealthfront.magellan.compose.ViewWrapper
import com.wealthfront.magellan.compose.lifecycle.lifecycle
import com.wealthfront.magellan.compose.transition.DelegatedDisplayable
import javax.inject.Inject

class MainScreen : Screen(), DelegatedDisplayable {

  override val displayable by lifecycle(MainView())

  @Inject
  lateinit var spotifyRemoteWrapper: SpotifyRemoteWrapper
  var spotifyAppRemote: SpotifyAppRemote? = null

  override fun onCreate(context: Context) {
    injector?.inject(this)
    super.onCreate(context)
  }

  override fun onShow(context: Context) {
    spotifyRemoteWrapper.connect(context) { spotifyAppRemote ->
      this.spotifyAppRemote = spotifyAppRemote
      spotifyAppRemote.playerApi.subscribeToPlayerState()
        .setEventCallback { playerState ->
          displayable.setTrack(playerState.track)
        }
    }
  }

  override fun onHide(context: Context) {
    spotifyRemoteWrapper.disconnect(spotifyAppRemote)
  }
}

class MainView : ViewWrapper(R.layout.main_screen) {
  var nowPlayingAlbumArt: ImageView? by bindView(R.id.nowPlayingAlbumArt)
  var nowPlayingTitle: TextView? by bindView(R.id.nowPlayingTitle)

  fun setTrack(track: Track) {
    nowPlayingTitle?.text = track.name
    track.imageUri
  }
}