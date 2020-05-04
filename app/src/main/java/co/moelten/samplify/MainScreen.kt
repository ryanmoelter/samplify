package co.moelten.samplify

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.widget.ImageView
import android.widget.TextView
import co.moelten.samplify.AppComponentProvider.injector
import co.moelten.samplify.spotify.SpotifyRemoteWrapper
import com.spotify.protocol.types.ImageUri
import com.spotify.protocol.types.PlayerState
import com.wealthfront.magellan.compose.Screen
import com.wealthfront.magellan.compose.ViewWrapper
import com.wealthfront.magellan.compose.coroutine.CreatedCoroutineScope
import com.wealthfront.magellan.compose.coroutine.ShownCoroutineScope
import com.wealthfront.magellan.compose.lifecycle.lateinitLifecycle
import com.wealthfront.magellan.compose.lifecycle.lifecycle
import com.wealthfront.magellan.compose.transition.DelegatedDisplayable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.CONFLATED
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainScreen : Screen(), DelegatedDisplayable {

  private val createdScope by lifecycle(CreatedCoroutineScope())
  private val playerStateChannel = Channel<PlayerState>(CONFLATED)

  @OptIn(ExperimentalCoroutinesApi::class)
  override val displayable by lifecycle(MainView(
    this,
    playerStateProvider = playerStateChannel.receiveAsFlow()
  ))

  @set:Inject
  var spotifyRemoteWrapper: SpotifyRemoteWrapper by lateinitLifecycle()

  override fun onCreate(context: Context) {
    injector?.inject(this)
  }

  override fun onShow(context: Context) {
    createdScope.launch {
      spotifyRemoteWrapper.getPlayerState().collect { playerState ->
        playerStateChannel.send(playerState)
      }
    }
  }

  suspend fun getImage(imageUri: ImageUri): Bitmap = spotifyRemoteWrapper.getImage(imageUri)
}

class MainView(
  val screen: MainScreen,
  val playerStateProvider: Flow<PlayerState>
) : ViewWrapper(R.layout.main_screen) {
  val shownScope by lifecycle(ShownCoroutineScope())

  var nowPlayingAlbumArt: ImageView? by bindView(R.id.nowPlayingAlbumArt)
  var nowPlayingTitle: TextView? by bindView(R.id.nowPlayingTitle)
  var nowPlayingArtist: TextView? by bindView(R.id.nowPlayingArtist)

  override fun onShow(context: Context) {
    shownScope.launch(Dispatchers.Main) {
      playerStateProvider.collect { playerState ->
        nowPlayingTitle!!.text = playerState.track?.name ?: "Nothing is playing"
        nowPlayingArtist!!.text = playerState.track?.artist?.name
        nowPlayingAlbumArt!!.setImageDrawable(
          BitmapDrawable(context.resources, screen.getImage(playerState.track.imageUri))
        )
      }
    }
  }
}