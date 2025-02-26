package com.conrradocamacho.exoplayerwithcompose

import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerControlView
import androidx.media3.ui.PlayerView
import com.conrradocamacho.exoplayerwithcompose.ui.theme.ExoPlayerWithComposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ExoPlayerWithComposeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PlayerScreen(
                        url = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@OptIn(UnstableApi::class)
@Composable
fun PlayerView(url: String, player: Player) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        player.also {
            it.setMediaItem(MediaItem.fromUri(url))
            it.prepare()
            it.play()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            player.release()
        }
    }

    AndroidView(
        factory = {
            PlayerView(context).apply {
                this.player = player
                setShowPreviousButton(false)
                setShowNextButton(false)
                setShowFastForwardButton(false)
                setShowRewindButton(false)
                setShowSubtitleButton(false)
                setShowShuffleButton(false)
                useController = true
                player.addListener(object : Player.Listener {
                    override fun onEvents(player: Player, events: Player.Events) {
                        super.onEvents(player, events)
                        if (events.contains(Player.EVENT_PLAYBACK_STATE_CHANGED)) {
                            val controller = this@apply
                                .findViewById<PlayerControlView>(
                                    androidx.media3.ui.R.id.exo_controller
                                )
                            controller?.findViewById<ImageButton>(
                                androidx.media3.ui.R.id.exo_shuffle
                            )?.visibility = android.view.View.GONE
                            controller?.findViewById<ImageButton>(
                                androidx.media3.ui.R.id.exo_prev
                            )?.visibility = android.view.View.GONE
                            controller?.findViewById<ImageButton>(
                                androidx.media3.ui.R.id.exo_rew
                            )?.visibility = android.view.View.GONE
                            controller?.findViewById<ImageButton>(
                                androidx.media3.ui.R.id.exo_play
                            )?.visibility = android.view.View.GONE
                            controller?.findViewById<ImageButton>(
                                androidx.media3.ui.R.id.exo_pause
                            )?.visibility = android.view.View.GONE
                            controller?.findViewById<ImageButton>(
                                androidx.media3.ui.R.id.exo_ffwd
                            )?.visibility = android.view.View.GONE
                            controller?.findViewById<ImageButton>(
                                androidx.media3.ui.R.id.exo_next
                            )?.visibility = android.view.View.GONE

                            controller?.findViewById<ImageButton>(
                                androidx.media3.ui.R.id.exo_play_pause
                            )?.visibility = android.view.View.GONE

                            controller?.findViewById<ImageButton>(
                                androidx.media3.ui.R.id.exo_settings
                            )?.visibility = android.view.View.GONE
                        }
                    }
                })
            }
        }
    )
}

@Composable
fun PlayerControls(player: Player) {
    var isPlaying by remember { mutableStateOf(player.isPlaying) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f))
            .clickable {
                if (isPlaying) {
                    player.pause()
                } else {
                    player.play()
                }
                isPlaying = !isPlaying
            },
        contentAlignment = Alignment.Center
    ) {
        if (isPlaying) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_pause_24),
                contentDescription = "Pause",
                tint = Color.White,
                modifier = Modifier
                    .size(64.dp)
                    .padding(16.dp)
            )
        }
        else {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Play",
                tint = Color.White,
                modifier = Modifier
                    .size(64.dp)
                    .padding(16.dp)
            )
        }
    }
}

@Composable
fun PlayerScreen(url: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val player = remember {
        ExoPlayer.Builder(context).build()
    }
    Box(modifier = modifier
        .fillMaxSize()
    ) {
        PlayerView(
            url = url,
            player = player
        )
        PlayerControls(
            player = player
        )
    }
}

