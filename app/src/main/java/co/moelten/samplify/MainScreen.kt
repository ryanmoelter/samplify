package co.moelten.samplify

import android.content.Context
import android.view.View
import com.wealthfront.magellan.BaseScreenView
import com.wealthfront.magellan.Screen
import com.wealthfront.magellan.ScreenView

class MainScreen : Screen<MainView>() {
  override fun createView(context: Context): MainView = MainView(context)
}

class MainView(context: Context) : BaseScreenView<MainScreen>(context)