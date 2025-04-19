package presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberWindowState
import net.TcpViewModel
import presentation.gameboard.GameBoard
import presentation.menu.Menu
import util.loadCursorIcon
import kotlin.system.exitProcess

@Composable
fun App(tcpViewModel: TcpViewModel = remember { TcpViewModel() }) {
    val appState = remember { AppState(tcpViewModel) }

    LaunchedEffect(appState.selection) {
        appState.handleSelection()
    }

    LaunchedEffect(tcpViewModel.connectedClients.toList()) {
        appState.updateConnectedClients()
    }

    LaunchedEffect(tcpViewModel.receivedTopDiscard.toList(), tcpViewModel.receivedPlayers.toList()) {
        appState.updateReceivedData()
    }

    Window(
        onCloseRequest = { exitProcess(0) },
        title = "Card Games",
        state = rememberWindowState(
            position = WindowPosition.Aligned(Alignment.Center),
            size = DpSize(1920.dp, 1080.dp),
            placement = WindowPlacement.Maximized,
        ),
        resizable = false,
        undecorated = true
    ) {
        MaterialTheme {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF113540))
                    .pointerHoverIcon(loadCursorIcon("assets/Cursors/Default.png")),
                contentAlignment = Alignment.Center
            ) {
                Menu(
                    selection = object : MutableState<String> {
                        override var value: String
                            get() = appState.selection
                            set(value) { appState.selection = value }

                        override fun component1(): String {
                            TODO("Not yet implemented")
                        }

                        override fun component2(): (String) -> Unit {
                            TODO("Not yet implemented")
                        }
                    }
                )
                appState.topDiscard?.let { GameBoard(appState.players, it) }
            }
        }
    }
}
