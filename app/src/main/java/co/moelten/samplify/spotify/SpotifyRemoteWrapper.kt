package co.moelten.samplify.spotify

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import javax.inject.Inject

const val CLIENT_ID = "e7ee02a6c50f4e8b8690bbb6b974db78"
const val REDIRECT_URI = "samplify://authenticated"

class SpotifyRemoteWrapper @Inject constructor() {
  fun connect(context: Context) {
    val connectionParams = ConnectionParams.Builder(CLIENT_ID)
      .setRedirectUri(REDIRECT_URI)
      .showAuthView(true)
      .build()

    SpotifyAppRemote.connect(context, connectionParams,
      object : Connector.ConnectionListener {
        override fun onConnected(spotifyAppRemote: SpotifyAppRemote) {
          // mSpotifyAppRemote = spotifyAppRemote
          Log.e(this::class.java.simpleName, "Connected! Yay!")

          // Now you can start interacting with App Remote
          // connected()
          Toast.makeText(context, "Connected!", Toast.LENGTH_LONG).show()
        }

        override fun onFailure(throwable: Throwable) {
          Log.e(this::class.java.simpleName, throwable.message, throwable)

          // Something went wrong when attempting to connect! Handle errors here
        }
      })
  }
}
