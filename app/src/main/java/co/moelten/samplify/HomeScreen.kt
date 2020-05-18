package co.moelten.samplify

import android.content.Context
import android.view.View
import com.wealthfront.magellan.compose.Screen
import com.wealthfront.magellan.compose.lifecycleAttachedView
import com.wealthfront.magellan.compose.navigation.Navigable

class HomeScreen(
  val goToNowPlaying: () -> Unit
) : Screen(), Navigable {

  override val view by lifecycleAttachedView(R.layout.home)

  override fun onShow(context: Context) {
    val container: View = view!!.findViewById(R.id.container)

    container.setOnClickListener {
      goToNowPlaying()
    }
  }
}
