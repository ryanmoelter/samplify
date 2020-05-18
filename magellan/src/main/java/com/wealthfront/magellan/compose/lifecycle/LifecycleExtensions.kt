package com.wealthfront.magellan.compose.lifecycle

import kotlin.reflect.KProperty

fun <CustomLifecycleAware : LifecycleAware, PropertyType> LifecycleOwner.lifecycleAttached(
  lifecycleAware: CustomLifecycleAware,
  getter: (CustomLifecycleAware) -> PropertyType
): Lifecycle<CustomLifecycleAware, PropertyType> {
  return Lifecycle(
    this,
    lifecycleAware,
    getter
  )
}

fun <CustomLifecycleAware : LifecycleAware> LifecycleOwner.lifecycleAttached(
  lifecycleAware: CustomLifecycleAware
): Lifecycle<CustomLifecycleAware, CustomLifecycleAware> {
  return Lifecycle(
    this,
    lifecycleAware,
    { lifecycleAware })
}

class Lifecycle<CustomLifecycleAware : LifecycleAware, PropertyType>(
  parent: LifecycleOwner,
  lifecycleAware: CustomLifecycleAware,
  val getter: (CustomLifecycleAware) -> PropertyType
) {

  var lifecycleAware = lifecycleAware
    private set

  var overrideValue: PropertyType? = null

  init {
    parent.attachToLifecycle(lifecycleAware)
  }

  operator fun getValue(thisRef: Any?, property: KProperty<*>): PropertyType {
    return overrideValue ?: getter(lifecycleAware)
  }

  operator fun setValue(thisRef: Any?, property: KProperty<*>, value: PropertyType) {
    overrideValue = value
  }
}

fun <CustomLifecycleAware : LifecycleAware> LifecycleOwner.lateinitLifecycleAttached(): LateinitLifecycle<CustomLifecycleAware> {
  return LateinitLifecycle(this)
}

class LateinitLifecycle<CustomLifecycleAware : LifecycleAware>(
  val parent: LifecycleOwner
) {

  lateinit var lifecycleAware: CustomLifecycleAware
    private set

  operator fun getValue(thisRef: Any?, property: KProperty<*>): CustomLifecycleAware {
    return lifecycleAware
  }

  operator fun setValue(thisRef: Any?, property: KProperty<*>, value: CustomLifecycleAware) {
    lifecycleAware = value
    parent.attachToLifecycle(lifecycleAware)
  }
}
