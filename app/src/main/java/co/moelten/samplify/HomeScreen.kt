package co.moelten.samplify

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import com.wealthfront.magellan.compose.Screen
import com.wealthfront.magellan.compose.lifecycleAttachedView
import com.wealthfront.magellan.compose.navigation.Navigable

class HomeScreen(
  val goToNowPlaying: () -> Unit
) : Screen(), Navigable {

  override val view by lifecycleAttachedView(R.layout.home)
  override val transitionElements: List<View>?
    get() = view?.findViewById<ViewGroup>(R.id.container)?.children?.toList()

  override fun onShow(context: Context) {
    val container: View = view!!.findViewById(R.id.container)

    container.setOnClickListener {
      goToNowPlaying()
    }
  }
}
