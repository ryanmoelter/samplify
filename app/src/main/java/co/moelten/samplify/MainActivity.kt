package co.moelten.samplify

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import co.moelten.samplify.AppComponentProvider.injector
import com.wealthfront.magellan.Navigator
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

  @Inject lateinit var navigator: Navigator

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    injector?.inject(this)
    setContentView(R.layout.activity_main)
  }
}
