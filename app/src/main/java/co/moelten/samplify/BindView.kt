package co.moelten.samplify

import android.content.Context
import android.view.View
import androidx.annotation.IdRes
import com.wealthfront.magellan.compose.lifecycle.LifecycleComponent
import com.wealthfront.magellan.compose.lifecycle.LifecycleOwner
import com.wealthfront.magellan.compose.lifecycle.lifecycleAttached
import com.wealthfront.magellan.compose.transition.Displayable

fun <T : View, Displayed> Displayed.bindView(@IdRes res: Int) where Displayed : Displayable, Displayed : LifecycleOwner =
  lifecycleAttached(MutableBindViewDelegate<T>(this, res)) { it.cachedView }

class MutableBindViewDelegate<T : View>(
  val displayable: Displayable,
  @IdRes val id: Int
) : LifecycleComponent() {

  var cachedView: T? = null
    get() {
      if (field == null) {
        field = displayable.view?.findViewById<T>(id)
      }
      return field
    }
    private set

  override fun onHide(context: Context) {
    cachedView = null
  }
}
