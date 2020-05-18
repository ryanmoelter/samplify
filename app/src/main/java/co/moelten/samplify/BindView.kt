package co.moelten.samplify

import android.content.Context
import android.view.View
import androidx.annotation.IdRes
import com.wealthfront.magellan.compose.ViewWrapper
import com.wealthfront.magellan.compose.lifecycle.LifecycleComponent
import com.wealthfront.magellan.compose.lifecycle.lifecycle

fun <T : View> ViewWrapper.bindView(@IdRes res: Int) =
  lifecycle(MutableBindViewDelegate<T>(this, res)) { it.cachedView }

class MutableBindViewDelegate<T : View>(
  val viewWrapper: ViewWrapper,
  @IdRes val id: Int
) : LifecycleComponent() {

  var cachedView: T? = null
    get() {
      if (field == null) {
        field = viewWrapper.view?.findViewById<T>(id)
      }
      return field
    }
    private set

  override fun onHide(context: Context) {
    cachedView = null
  }
}
