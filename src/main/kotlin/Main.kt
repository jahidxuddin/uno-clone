import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import model.Player
import model.TopDiscard
import net.TcpViewModel
import presentation.gameboard.GameBoard
import presentation.menu.Menu
import util.loadCursorIcon
import kotlin.system.exitProcess

@Composable
fun App(tcpViewModel: TcpViewModel = remember { TcpViewModel() }) {
    val selection = remember { mutableStateOf("") }
    var topDiscard by remember { mutableStateOf<TopDiscard?>(null) }
    val players = remember { mutableStateListOf<Player?>(null, null, null, null) }

    LaunchedEffect(selection.value) {
        when {
            selection.value == "START" -> {
                topDiscard = TopDiscard.takeTopDiscard()
                players[0] = Player(1)
                tcpViewModel.startHosting()
            }

            selection.value.startsWith("JOIN") -> {
                val joinCode = selection.value.removePrefix("JOIN ")
                tcpViewModel.connectToHost(joinCode)
            }
        }
    }

    LaunchedEffect(tcpViewModel.connectedClients.toList()) {
        if (selection.value != "START") return@LaunchedEffect

        val connectedClients = tcpViewModel.connectedClients.toList()
        val emptySlotIndex = players.indexOfFirst { it == null }

        if (emptySlotIndex != -1) {
            connectedClients.lastOrNull()?.inetAddress?.hostAddress?.let { ip ->
                players[emptySlotIndex] = Player(id = emptySlotIndex + 1, ip = ip)
            }
        }

        tcpViewModel.broadcastTopDiscard(topDiscard)
        tcpViewModel.broadcastPlayers(players.toList())
    }

    LaunchedEffect(tcpViewModel.receivedTopDiscard.toList(), tcpViewModel.receivedPlayers.toList()) {
        val receivedTopDiscard = tcpViewModel.receivedTopDiscard.firstOrNull()
        val receivedPlayers = tcpViewModel.receivedPlayers.filterNotNull()

        if (!selection.value.startsWith("JOIN") || receivedTopDiscard == null || receivedPlayers.isEmpty()) {
            return@LaunchedEffect
        }

        topDiscard = receivedTopDiscard
        val currentIp = tcpViewModel.getIp()

        players.clear()
        val sortedPlayers = receivedPlayers.sortedWith(compareBy<Player> { it.ip != currentIp }.thenBy { it.id })
        players.addAll(sortedPlayers)

        while (players.size < 4) {
            players.add(null)
        }
    }

    Window(
        onCloseRequest = { exitProcess(0) }, title = "Card Games", state = rememberWindowState(
            position = WindowPosition.Aligned(Alignment.Center),
            size = DpSize(1920.dp, 1080.dp),
            placement = WindowPlacement.Maximized,
        ), resizable = false, undecorated = true
    ) {
        MaterialTheme {
            Box(
                modifier = Modifier.fillMaxSize().background(Color(0xFF113540))
                    .pointerHoverIcon(loadCursorIcon("assets/Cursors/Default.png")), contentAlignment = Alignment.Center
            ) {
                Menu(selection)
                topDiscard?.let { GameBoard(players, it) }
            }
        }
    }
}

fun main() = application {
    App()
}
