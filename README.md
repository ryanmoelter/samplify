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

TODO:
* `Screen`
* `DelegatingScreen` + `Navigator`
* `LifecyclePropagator` (name TBD)
* Under the hood

# Coroutines, `Flow`s, and RxJava (WIP)
For the uninitiated, Kotlin coroutines provide a lightweight framework to execute asynchronous code.

This section is for my coworkers who are familiar with RxJava.

TODO:
* `suspend fun` vs. `Single`/`Maybe`/`Completeable`
Network calls are a special case of an observable that only has one value, or an error. In RxJava, this is usually modeled as a `Single`, `Maybe`, or `Completeable`, depending on what kind of expectations you have for the resulting data.
* `Flow` vs. `Observable`
* `CoroutineScope` vs. `autoDispose()`
