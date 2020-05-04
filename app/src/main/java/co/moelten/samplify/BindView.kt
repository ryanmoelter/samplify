package co.moelten.samplify

import android.content.Context
import android.view.View
import androidx.annotation.IdRes
import com.wealthfront.magellan.compose.ViewWrapper
import com.wealthfront.magellan.compose.lifecycle.LifecycleAware
import com.wealthfront.magellan.compose.lifecycle.lifecycle
import kotlin.reflect.KProperty

fun <ViewType : View> ViewWrapper.bindView(@IdRes res: Int) =
  lifecycle(ViewFinder<ViewType>(res, this)) { it.myView }

class ViewFinder<ViewType : View>(@IdRes private val res : Int, private val viewWrapper: ViewWrapper) : LifecycleAware {
  var myView: ViewType? = null

  override fun show(context: Context) {
    myView = viewWrapper.view?.findViewById<ViewType>(res)!!
  }

  override fun hide(context: Context) {
    myView = null
  }
}
