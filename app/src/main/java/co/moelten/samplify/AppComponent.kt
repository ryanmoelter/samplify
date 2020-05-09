package co.moelten.samplify

import dagger.Component
import javax.inject.Singleton

@Component(
  modules = [
    AppModule::class
  ]
)
@Singleton
interface AppComponent {
  fun inject(samplifyApp: SamplifyApp)

  fun inject(mainActivity: MainActivity)

  fun inject(rootScreen: RootScreen)

  fun inject(nowPlayingScreen: NowPlayingScreen)
}

object AppComponentProvider {

  var appComponent: AppComponent? = null

  val injector: AppComponent?
    get() {
      return appComponent
    }
}
