package presentation

import androidx.compose.runtime.*
import model.Player
import model.TopDiscard
import net.TcpViewModel

class AppState(
    private val tcpViewModel: TcpViewModel
) {
    var selection by mutableStateOf("")
    var topDiscard by mutableStateOf<TopDiscard?>(null)
    var players = mutableStateListOf<Player?>(null, null, null, null)

    fun handleSelection() {
        when {
            selection == "START" -> handleGameStart()
            selection.startsWith("JOIN") -> handleGameJoin()
        }
    }

    private fun handleGameStart() {
        topDiscard = TopDiscard.takeTopDiscard()
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

        if (emptySlotIndex != -1) {
            connectedClients.lastOrNull()?.inetAddress?.hostAddress?.let { ip ->
                players[emptySlotIndex] = Player(id = emptySlotIndex + 1, ip = ip)
            }
        }

        tcpViewModel.broadcastTopDiscard(topDiscard)
        tcpViewModel.broadcastPlayers(players.toList())
    }

    fun updateReceivedData() {
        val receivedTopDiscard = tcpViewModel.receivedTopDiscard.firstOrNull()
        val receivedPlayers = tcpViewModel.receivedPlayers.filterNotNull()

        if (!selection.startsWith("JOIN") || receivedTopDiscard == null || receivedPlayers.isEmpty()) {
            return
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
}
