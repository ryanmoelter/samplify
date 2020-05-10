package co.moelten.samplify

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import co.moelten.samplify.AppComponentProvider.injector
import co.moelten.samplify.model.Loadable.Error
import co.moelten.samplify.model.Loadable.Loading
import co.moelten.samplify.model.Loadable.Success
import co.moelten.samplify.model.mapLoadableValue
import co.moelten.samplify.spotify.NowPlayingFacade
import coil.api.load
import com.wealthfront.blend.ANIM_DURATION_DEFAULT_MS
import com.wealthfront.magellan.compose.ViewWrapper
import com.wealthfront.magellan.compose.coroutine.ShownCoroutineScope
import com.wealthfront.magellan.compose.lifecycle.lifecycle
import com.wealthfront.magellan.compose.navigation.Navigable
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class NowPlayingScreen : ViewWrapper(R.layout.now_playing), Navigable {

  private val shownScope by lifecycle(ShownCoroutineScope())

  @Inject
  lateinit var nowPlayingFacade: NowPlayingFacade

  override fun onCreate(context: Context) {
    injector?.inject(this)
  }

  override fun onShow(context: Context, view: View) {
    shownScope.launch { subscribeToTrackUpdates(view) }
    shownScope.launch { subscribeToAlbumArtUpdates(view) }
  }

  private suspend fun subscribeToTrackUpdates(view: View) {
    val nowPlayingTitle: TextView = view.findViewById(R.id.nowPlayingTitle)
    val nowPlayingArtist: TextView = view.findViewById(R.id.nowPlayingArtist)

    nowPlayingFacade.trackFlow.collect { loadableTrack ->
      when (loadableTrack) {
        is Success -> {
          nowPlayingTitle.text =
            loadableTrack.value?.name ?: view.context.getString(R.string.empty_track_name)
          nowPlayingArtist.text = loadableTrack.value?.artist?.name
        }
        is Loading -> {
          nowPlayingTitle.setText(R.string.loading)
          nowPlayingArtist.text = null
        }
        is Error -> {
          nowPlayingTitle.setText(R.string.error)
          nowPlayingArtist.text = null
        }
      }.let { }
    }
  }

  private suspend fun subscribeToAlbumArtUpdates(view: View) {
    val nowPlayingAlbumArt: ImageView = view.findViewById(R.id.nowPlayingAlbumArt)
    nowPlayingAlbumArt.setOnClickListener {
      shownScope.launch { nowPlayingFacade.playOrPause() }
    }
    nowPlayingFacade.albumArtFlow
      .mapLoadableValue { bitmap -> BitmapDrawable(view.context.resources, bitmap) }
      .collect { loadableBitmapDrawable ->
        when (loadableBitmapDrawable) {
          is Success -> {
            nowPlayingAlbumArt.load(loadableBitmapDrawable.value) {
              placeholder(R.drawable.ic_launcher_background)
              crossfade(ANIM_DURATION_DEFAULT_MS.toInt())
            }
          }
          is Loading, is Error -> {
            nowPlayingAlbumArt.load(R.drawable.ic_launcher_background) {
              crossfade(ANIM_DURATION_DEFAULT_MS.toInt())
            }
          }
        }.let { }
      }
  }
}
