package co.moelten.samplify

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import co.moelten.samplify.AppComponentProvider.injector
import co.moelten.samplify.spotify.SpotifyRemoteWrapper
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.protocol.types.PlayerState
import com.spotify.protocol.types.Track
import com.wealthfront.magellan.BaseScreenView
import com.wealthfront.magellan.Screen
import com.wealthfront.magellan.ScreenView
import javax.inject.Inject

class MainScreen : Screen<MainView>() {

  @Inject
  lateinit var spotifyRemoteWrapper: SpotifyRemoteWrapper
  var spotifyAppRemote: SpotifyAppRemote? = null

  override fun createView(context: Context): MainView {
    injector?.inject(this)
    return MainView(context)
  }

  override fun onShow(context: Context) {
    spotifyRemoteWrapper.connect(context) { spotifyAppRemote ->
      this.spotifyAppRemote = spotifyAppRemote
      spotifyAppRemote.playerApi.subscribeToPlayerState()
        .setEventCallback { playerState ->
          view.setTrack(playerState.track)
        }
    }
  }

  override fun onHide(context: Context?) {
    spotifyRemoteWrapper.disconnect(spotifyAppRemote)
  }
}

class MainView(context: Context) : BaseScreenView<MainScreen>(context) {
  var nowPlayingAlbumArt: ImageView by bindView(R.id.nowPlayingAlbumArt)
  var nowPlayingTitle: TextView by bindView(R.id.nowPlayingTitle)

  init {
    inflate(R.layout.main_screen)
  }

  fun setTrack(track: Track) {
    nowPlayingTitle.text = track.name
    track.imageUri
  }
}