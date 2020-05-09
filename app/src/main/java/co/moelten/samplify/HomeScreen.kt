package co.moelten.samplify

import android.content.Context
import android.view.View
import com.wealthfront.magellan.compose.ViewWrapper
import com.wealthfront.magellan.compose.navigation.Navigable

class HomeScreen(
  val goToNowPlaying: () -> Unit
) : ViewWrapper(R.layout.home), Navigable {
  var container: View? by bindView(R.id.container)

  override fun onShow(context: Context) {
    container!!.setOnClickListener {
      goToNowPlaying()
    }
  }
}
