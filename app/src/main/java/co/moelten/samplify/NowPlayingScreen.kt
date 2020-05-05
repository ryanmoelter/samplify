package co.moelten.samplify

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.widget.ImageView
import android.widget.TextView
import co.moelten.samplify.AppComponentProvider.injector
import co.moelten.samplify.model.Loadable
import co.moelten.samplify.model.Loadable.Error
import co.moelten.samplify.model.Loadable.Loading
import co.moelten.samplify.model.Loadable.Success
import co.moelten.samplify.model.mapLoadableValue
import co.moelten.samplify.spotify.Player
import co.moelten.samplify.spotify.SpotifyRemoteWrapper
import coil.api.load
import com.spotify.protocol.types.ImageUri
import com.spotify.protocol.types.PlayerState
import com.spotify.protocol.types.Track
import com.wealthfront.blend.ANIM_DURATION_DEFAULT_MS
import com.wealthfront.magellan.compose.Screen
import com.wealthfront.magellan.compose.ViewWrapper
import com.wealthfront.magellan.compose.coroutine.ShownCoroutineScope
import com.wealthfront.magellan.compose.lifecycle.lateinitLifecycle
import com.wealthfront.magellan.compose.lifecycle.lifecycle
import com.wealthfront.magellan.compose.transition.DelegatedDisplayable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

class NowPlayingScreen : Screen(), DelegatedDisplayable {

  private val shownScope by lifecycle(ShownCoroutineScope())

  @OptIn(ExperimentalCoroutinesApi::class)
  private val playerStateChannel = ConflatedBroadcastChannel<Loadable<PlayerState>>()

  @OptIn(FlowPreview::class)
  val trackFlow
    get() = playerStateChannel.asFlow()
      .mapLoadableValue { playerState -> playerState.track }

  @OptIn(ExperimentalCoroutinesApi::class)
  val albumArtFlow
    get() = trackFlow
      .flatMapLatest { loadable ->
        flow {
          when (loadable) {
            is Success -> {
              emit(Loading())
              emit(Success(getImage(loadable.value.imageUri)))
            }
            is Loading -> {
              emit(Loading())
            }
            is Error -> {
              emit(Error<Bitmap>(loadable.throwable))
            }
          }
        }
      }

  override val displayable by lifecycle(
    NowPlayingViewWrapper(
      trackFlow = trackFlow,
      albumArtFlow = albumArtFlow,
      getPlayer = { spotifyRemoteWrapper.player }
    )
  )

  @set:Inject
  var spotifyRemoteWrapper: SpotifyRemoteWrapper by lateinitLifecycle()

  override fun onCreate(context: Context) {
    injector?.inject(this)
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  override fun onShow(context: Context) {
    shownScope.launch {
      playerStateChannel.send(Loading())
      spotifyRemoteWrapper.getPlayerState().collect { playerState ->
        playerStateChannel.send(Success(playerState))
      }
    }
  }

  suspend fun getImage(imageUri: ImageUri): Bitmap = spotifyRemoteWrapper.getImage(imageUri)
}

class NowPlayingViewWrapper(
  val trackFlow: Flow<Loadable<Track?>>,
  val albumArtFlow: Flow<Loadable<Bitmap>>,
  val getPlayer: () -> Player
) : ViewWrapper(R.layout.now_playing) {
  val shownScope by lifecycle(ShownCoroutineScope())

  var nowPlayingAlbumArt: ImageView? by bindView(R.id.nowPlayingAlbumArt)
  var nowPlayingTitle: TextView? by bindView(R.id.nowPlayingTitle)
  var nowPlayingArtist: TextView? by bindView(R.id.nowPlayingArtist)

  override fun onShow(context: Context) {
    nowPlayingAlbumArt!!.setOnClickListener {
      shownScope.launch { getPlayer().playOrPause() }
    }
    shownScope.launch(Dispatchers.Main) {
      launch {
        trackFlow.collect { loadableTrack ->
          when (loadableTrack) {
            is Success -> {
              nowPlayingTitle!!.text = loadableTrack.value?.name ?: context.getString(R.string.empty_track_name)
              nowPlayingArtist!!.text = loadableTrack.value?.artist?.name
            }
            is Loading -> {
              nowPlayingTitle!!.text = context.getString(R.string.loading)
              nowPlayingArtist!!.text = null
            }
            is Error -> {
              nowPlayingTitle!!.text = context.getString(R.string.error)
              nowPlayingArtist!!.text = null
            }
          }.let { }
        }
      }
      launch {
        albumArtFlow
          .mapLoadableValue { bitmap -> BitmapDrawable(context.resources, bitmap) }
          .collect { loadableBitmapDrawable ->
            when (loadableBitmapDrawable) {
              is Success -> {
                nowPlayingAlbumArt!!.load(loadableBitmapDrawable.value) {
                  placeholder(R.drawable.ic_launcher_background)
                  crossfade(ANIM_DURATION_DEFAULT_MS.toInt())
                }
              }
              is Loading, is Error -> {
                nowPlayingAlbumArt!!.load(R.drawable.ic_launcher_background) {
                  crossfade(ANIM_DURATION_DEFAULT_MS.toInt())
                }
              }
            }.let { }
          }
      }
    }
  }
}