import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberWindowState
import state.GameState
import state.NetState
import ui.screens.GameBoard
import ui.screens.Menu
import state.util.bindState
import ui.util.loadCursorIcon
import kotlin.system.exitProcess

@Composable
fun App(netState: NetState = remember { NetState() }) {
    val gameState = remember { GameState(netState) }

    LaunchedEffect(gameState.selection) {
        when {
            gameState.selection == "START" -> gameState.handleGameStart()
            gameState.selection.startsWith("JOIN") -> gameState.handleGameJoin()
        }
    }

    LaunchedEffect(netState.connectedClients.toList()) {
        gameState.handlePlayerJoin()
        gameState.sendCurrentStack()
    }

    LaunchedEffect(netState.receivedStack.toList(), netState.receivedPlayers.toList()) {
        gameState.handleReceivedData()
    }

    val windowState = rememberWindowState(
        position = WindowPosition.Aligned(Alignment.Center),
        size = DpSize(1920.dp, 1080.dp),
        placement = WindowPlacement.Maximized,
    )

    Window(
        onCloseRequest = { exitProcess(0) }, title = "UNO", state = windowState, resizable = false, undecorated = true
    ) {
        MaterialTheme {
            Box(
                modifier = Modifier.fillMaxSize().background(Color(0xFF113540))
                    .pointerHoverIcon(loadCursorIcon("assets/Cursors/Default.png")), contentAlignment = Alignment.Center
            ) {
                Menu(gameState.showMenu, bindState({ gameState.selection }, { gameState.selection = it }))
                GameBoard(gameState.players, gameState.stack.value) { card ->
                    val isWildCard = gameState.checkForWildCard(card)
                    if (gameState.checkForSameColor(card) || gameState.checkForSameNumber(card) || isWildCard) {
                        gameState.discardCards(card)
                        gameState.syncGameState()

                        if (isWildCard) {
                            gameState.showColorPicker.value = true
                        }

                        return@GameBoard true
                    }
                    return@GameBoard false
                }
                if (gameState.showColorPicker.value) {
                    UnoColorPicker { chosenColor ->
                        gameState.chooseWildColor(chosenColor)
                        gameState.showColorPicker.value = false
                    }
                }
            }
        }
    }
}

@Composable
fun UnoColorPicker(
    onColorSelected: (String) -> Unit
) {
    val colors = listOf(
        "red" to Color.Red,
        "green" to Color.Green,
        "blue" to Color.Blue,
        "yellow" to Color.Yellow
    )

    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(24.dp)
    ) {
        colors.forEach { (name, color) ->
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(color)
                    .clickable { onColorSelected(name) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = name,
                    color = Color.Black,
                    fontSize = 14.sp
                )
            }
        }
    }
}
