package co.moelten.samplify

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import co.moelten.samplify.AppComponentProvider.injector
import com.wealthfront.magellan.Navigator
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

  @Inject
  lateinit var navigator: Navigator

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    injector?.inject(this)
    setContentView(R.layout.activity_main)
    navigator.onCreate(this, savedInstanceState)
  }

  override fun onResume() {
    super.onResume()
    navigator.onResume(this)
  }

  override fun onPause() {
    super.onPause()
    navigator.onPause(this)
  }

  override fun onDestroy() {
    super.onDestroy()
    navigator.onDestroy(this)
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    navigator.onSaveInstanceState(outState)
  }
}
