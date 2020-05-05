package co.moelten.samplify

import android.content.Context
import android.widget.FrameLayout
import com.wealthfront.magellan.compose.Screen
import com.wealthfront.magellan.compose.lifecycle.lifecycle
import com.wealthfront.magellan.compose.lifecycleView
import com.wealthfront.magellan.compose.navigation.LinearNavigator
import com.wealthfront.magellan.compose.transition.DelegatedDisplayable
import com.wealthfront.magellan.compose.transition.Displayable

class RootScreen : Screen(), DelegatedDisplayable {
  override val view by lifecycleView { FrameLayout(it) }
  override val displayable: Displayable
    get() = navigator.currentNavigable!!

  var navigator by lifecycle(LinearNavigator { view!! })

  override fun onShow(context: Context) {
    navigator.goTo(NowPlayingScreen())
  }
}
