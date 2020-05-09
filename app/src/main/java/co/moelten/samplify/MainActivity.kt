package co.moelten.samplify

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import co.moelten.samplify.AppComponentProvider.injector
import com.wealthfront.magellan.compose.Screen
import com.wealthfront.magellan.compose.lifecycle.attachToActivity
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

  @Inject lateinit var rootScreen: Screen

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    injector?.inject(this)
    setContentView(R.layout.activity_main)
    rootScreen.attachToActivity(this, R.id.magellanContainer)
  }

  override fun onBackPressed() {
    if (!rootScreen.backPressed()) {
      super.onBackPressed()
    }
  }
}
