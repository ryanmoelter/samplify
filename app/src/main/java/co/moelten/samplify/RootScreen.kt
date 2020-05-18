package co.moelten.samplify

import android.content.Context
import android.widget.FrameLayout
import co.moelten.samplify.AppComponentProvider.injector
import co.moelten.samplify.spotify.SpotifyRemoteWrapper
import com.wealthfront.magellan.compose.Screen
import com.wealthfront.magellan.compose.lifecycle.lateinitLifecycleAttached
import com.wealthfront.magellan.compose.lifecycle.lifecycleAttached
import com.wealthfront.magellan.compose.lifecycleAttachedView
import com.wealthfront.magellan.compose.navigation.LinearNavigator
import com.wealthfront.magellan.compose.transition.DelegatedDisplayable
import com.wealthfront.magellan.compose.transition.Displayable
import javax.inject.Inject

class RootScreen : Screen(), DelegatedDisplayable {
  override val view by lifecycleAttachedView { FrameLayout(it) }
  override val displayable: Displayable
    get() = navigator.currentNavigable!!

  var navigator by lifecycleAttached(LinearNavigator { view!! })

  @set:Inject
  var spotifyRemoteWrapper: SpotifyRemoteWrapper by lateinitLifecycleAttached()

  override fun onCreate(context: Context) {
    injector?.inject(this)
    goToHome()
  }

  private fun goToHome() {
    navigator.goTo(HomeScreen(
      goToNowPlaying = { goToNowPlaying() }
    ))
  }

  private fun goToNowPlaying() {
    navigator.goTo(NowPlayingScreen())
  }
}
