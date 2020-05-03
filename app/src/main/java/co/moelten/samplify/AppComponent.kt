package co.moelten.samplify

import dagger.Component

@Component(modules = [
  AppModule::class
])
interface AppComponent {
  fun inject(mainActivity: MainActivity)
}

object AppComponentProvider {

  var appComponent: AppComponent? = null

  val injector: AppComponent? get() {
    return appComponent
  }
}
