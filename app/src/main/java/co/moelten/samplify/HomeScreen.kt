package co.moelten.samplify

import android.content.Context
import android.view.View
import com.wealthfront.magellan.compose.ViewWrapper
import com.wealthfront.magellan.compose.navigation.Navigable

class HomeScreen(
  val goToNowPlaying: () -> Unit
) : ViewWrapper(R.layout.home), Navigable {

  override fun onShow(context: Context, view: View) {
    val container: View = view.findViewById(R.id.container)

    container.setOnClickListener {
      goToNowPlaying()
    }
  }
}
