package co.moelten.samplify

import android.app.Application
import co.moelten.samplify.AppComponentProvider.appComponent

class SamplifyApp : Application() {
  init {
    appComponent = DaggerAppComponent.builder().appModule(AppModule(this)).build()
  }
}
