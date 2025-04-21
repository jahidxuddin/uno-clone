package presentation

import androidx.compose.runtime.*
import model.Player
import model.Card
import net.TcpViewModel

class AppState(
    private val tcpViewModel: TcpViewModel
) {
    var selection by mutableStateOf("")

    val discardPile = mutableStateOf(ArrayDeque<Card>())
    var players = mutableStateListOf<Player?>(null, null, null, null)

    fun handleSelection() {
        when {
            selection == "START" -> handleGameStart()
            selection.startsWith("JOIN") -> handleGameJoin()
        }
    }

    private fun handleGameStart() {
        Card.takeTopDiscard().let {
            discardPile.value.addLast(it)
        }
        players[0] = Player(1)
        tcpViewModel.startHosting()
    }

    private fun handleGameJoin() {
        val joinCode = selection.removePrefix("JOIN ")
        tcpViewModel.connectToHost(joinCode)
    }

    fun updateConnectedClients() {
        if (selection != "START") return

        val connectedClients = tcpViewModel.connectedClients.toList()
        val emptySlotIndex = players.indexOfFirst { it == null }

        // Add new player
        if (emptySlotIndex != -1) {
            connectedClients.lastOrNull()?.inetAddress?.hostAddress?.let { ip ->
                players[emptySlotIndex] = Player(id = emptySlotIndex + 1, ip = ip)
            }
        }

        tcpViewModel.broadcastTopDiscard(discardPile.value.lastOrNull())
        tcpViewModel.broadcastPlayers(players.toList())
    }

    fun updateReceivedData() {
        val receivedTopDiscard = tcpViewModel.receivedTopDiscard.firstOrNull()
        val receivedPlayers = tcpViewModel.receivedPlayers.filterNotNull()

        if (!selection.startsWith("JOIN") || receivedTopDiscard == null || receivedPlayers.isEmpty()) {
            return
        }

        // Update discard pile
        if (discardPile.value.isEmpty() || discardPile.value.last() != receivedTopDiscard) {
            discardPile.value.addLast(receivedTopDiscard)
        }
        // Update players
        val sortedPlayers = receivedPlayers.sortedWith(compareBy<Player> { it.ip != tcpViewModel.getIp() }.thenBy { it.id })
        players.clear()
        players.addAll(sortedPlayers)
        while (players.size < 4) {
            players.add(null)
        }
    }

    fun addCardToDiscardPile(card: Card) {
        val newDeque = ArrayDeque(discardPile.value)
        newDeque.addLast(card)
        discardPile.value = newDeque
        // TODO: Broadcast the updated discard pile to all clients
    }
}
