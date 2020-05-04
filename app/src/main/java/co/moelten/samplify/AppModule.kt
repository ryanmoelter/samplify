package co.moelten.samplify

import android.content.Context
import com.wealthfront.magellan.compose.Screen
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(private val applicationContext: Context) {

  @Provides
  fun provideApplicationContext(): Context = applicationContext

  @Provides
  @Singleton
  fun provideRootScreen(): Screen = MainScreen()
}

