package co.moelten.samplify

import android.app.Application
import co.moelten.samplify.AppComponentProvider.appComponent
import com.wealthfront.magellan.compose.Screen
import javax.inject.Inject

class SamplifyApp : Application() {
  @Inject
  lateinit var rootScreen: Screen

  init {
    appComponent = DaggerAppComponent.builder().appModule(AppModule(this)).build()
    appComponent!!.inject(this)
    rootScreen.create(this)
  }
}
