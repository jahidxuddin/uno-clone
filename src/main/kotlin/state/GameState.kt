package state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import model.Card
import model.Player
import model.toDTO

class GameState(
    private val netState: NetState
) {
    var selection by mutableStateOf("")
    val stack = mutableStateOf(ArrayDeque<Card>())
    val players = mutableStateListOf<Player?>(null, null, null, null)

    fun handleGameStart() {
        Card.Companion.takeTopDiscard().let {
            stack.value.addLast(it)
        }
        players[0] = Player(1)
        netState.startHosting()
    }

    fun handleGameJoin() {
        selection.removePrefix("JOIN ").let { joinCode ->
            netState.connectToHost(joinCode)
        }
    }

    fun handlePlayerJoin() {
        if (selection != "START") return

        val ip = netState.connectedClients.lastOrNull()?.inetAddress?.hostAddress

        if (players.find { it?.ip == ip } != null) return

        val index = players.indexOfFirst { it == null }

        if (ip != null && index != -1) {
            players[index] = Player(id = index + 1, ip = ip)
            sendCurrentPlayers()
        }
    }

    fun discardCards(vararg receivedCards: Card) {
        val newDeque = ArrayDeque(stack.value)
        receivedCards.forEach { newDeque.addLast(it) }
        stack.value = newDeque
    }

    fun syncGameState() {
        sendCurrentStack()
        sendCurrentPlayers()
    }

    fun sendCurrentStack() {
        netState.broadcastData("STACK", stack.value.toList())
    }

    fun sendCurrentPlayers() {
        players.map { it?.toDTO() }.let {
            netState.broadcastData("PLAYERS", it)
        }
    }

    fun handleReceivedData() {
        val stack = netState.receivedStack.filterNotNull()
        val players = netState.receivedPlayers.filterNotNull()

        if (stack.isNotEmpty() && players.isNotEmpty()) {
            discardCards(*stack.toTypedArray())
            updatePlayers(players)
        }
    }

    private fun updatePlayers(receivedPlayers: List<Player>) {
        val currentPlayer = receivedPlayers.firstOrNull { it.ip == netState.getIp() }
        val otherPlayers = receivedPlayers.filter { it.id != currentPlayer?.id }.sortedBy { it.id }
        players.apply {
            clear()
            if (currentPlayer != null) {
                add(currentPlayer)
            }
            addAll(otherPlayers)
            while (size < 4) {
                add(null)
            }
        }
    }
}
