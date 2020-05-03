package co.moelten.samplify

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.wealthfront.magellan.BaseScreenView
import com.wealthfront.magellan.Screen
import com.wealthfront.magellan.ScreenView

class MainScreen : Screen<MainView>() {
  override fun createView(context: Context): MainView = MainView(context)
}

class MainView(context: Context) : BaseScreenView<MainScreen>(context) {
  var nowPlayingAlbumArt: ImageView by bindView(R.id.nowPlayingAlbumArt)
  var nowPlayingTitle: TextView by bindView(R.id.nowPlayingTitle)

  init {
    inflate(R.layout.main_screen)
  }
}