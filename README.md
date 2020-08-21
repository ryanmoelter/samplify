# Samplify
A simple Spotify client that provides an example implementation of a new version of [Magellan](https://github.com/wealthfront/magellan).

I've also been playing around with Kotlin coroutines a bit.

# Set up
Get a Spotify api key at [Spotify's developer website](https://developer.spotify.com/dashboard/) and paste it into `app/gradle.properties` like so:
```
spotifyClientId="<YOUR_CLIENT_ID>"
```

Then just build!

# Magellan update (WIP)
I've been playing around with an update to Magellan, centered around composability. The core change is basic: `Screen`s can now have livecycle listeners.

This opens up a wide range of possibilities, however, and this little app explores some of them.

**The rest of these sections are incomplete, for now.**

## `Screen`
Simple screens are the easiest scenario. These are screens that are simple enough to handle getting, transforming, and displaying their own data in a single class. They also handle all user interactions within themselves. For these screens, use a `Screen` all by itself.

To handle lifecycle events, like doing dependency injection in `onCreate` or cancelling network requrests in `onHide`, simply override the lifecycle event you're interested in.

To set up the view associated with the screen, start by overriding `val view: View?` to return a view whenever the screen is shown by delegating to `by lifecycleAttachedView(R.layout.my_screen_layout)`. This will inflate the view before `onShow` and tear it down after `onHide`. You can then set up the view in `onShow`, `onResume`, `onPause`, and `onHide` using the `view` reference.

(Note: Currently, `view` has to be nullable because it's unavailable before the screen is shown and after it's hidden. This is annoying, so I'm exploring ways to have a strictly non-null view reference during `Shown` and `Resumed`.)

Example of a simple screen:
```kotlin
class HomeScreen : Screen() {

  override val view by lifecycleAttachedView(R.layout.home)

  // You can ignore these for now
  // TODO: Add displayable properties

  override fun onShow(context: Context) {
    val button: Button = view!!.findViewById(R.id.my_button)

    button.setOnClickListener {
      // Handle click
    }
  }
}
```

## `DelegatingScreen` + `LinearNavigator` + `Navigable`
Most apps need more than just one screen of an app, however. 

## Child screens with `DelegatingScreen`
## `LifecycleComponent`s (name TBD)
## Attaching to the `Activity` and `Application` lifecycles
## Example scenarios
* Simple screen
* Complex screen (delegated view)
* Flow (delegated view + navigator)
* Lifecycle components (Screen + delegated logic)
## Under the hood (`Displayable`, `LifecycleAware`, and `LifecycleOwner`)

# Coroutines, `Flow`s, and RxJava (WIP)
For the uninitiated, Kotlin coroutines provide a lightweight framework to execute asynchronous code.

This section is for my coworkers who are familiar with RxJava.

TODO:
* `suspend fun` vs. `Single`/`Maybe`/`Completeable`
Network calls are a special case of an observable that only has one value, or an error. In RxJava, this is usually modeled as a `Single`, `Maybe`, or `Completeable`, depending on what kind of expectations you have for the resulting data.
* `Flow` vs. `Observable`
* `CoroutineScope` vs. `autoDispose()`
