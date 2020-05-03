package co.moelten.samplify

import android.content.Context
import com.wealthfront.magellan.Navigator
import dagger.Module
import dagger.Provides

@Module
class AppModule(private val applicationContext: Context) {

  @Provides
  fun provideApplicationContext(): Context = applicationContext

  @Provides
  fun provideNavigator(): Navigator = Navigator.withRoot(MainScreen()).build()
}

