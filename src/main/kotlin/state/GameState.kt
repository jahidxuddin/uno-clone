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
    val showMenu = mutableStateOf(true)
    val showColorPicker = mutableStateOf(false)
    var wildColor by mutableStateOf<String?>(null)
        private set
    var selection by mutableStateOf("")
    val stack = mutableStateOf(ArrayDeque<Card>())
    val players = mutableStateListOf<Player?>(null, null, null, null)

    fun chooseWildColor(color: String) {
        wildColor = color.lowercase()
        println("Wild color set to: $wildColor")
        syncGameState()
    }

    fun handleGameStart() {
        Card.takeTopDiscard().let {
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

        val alreadyExistingPlayer = players.firstOrNull { it?.ip == ip }
        if (alreadyExistingPlayer == null) {
            val freeIndex = players.indexOfFirst { it == null }

            if (ip != null && freeIndex != -1) {
                players[freeIndex] = Player(id = freeIndex + 1, ip = ip)
                sendCurrentPlayers()
            }
        } else {
            netState.connectedClients.removeLast()
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
        val receivedStack = netState.receivedStack.filterNotNull()
        val receivedPlayers = netState.receivedPlayers.filterNotNull()

        if (receivedStack.isEmpty() || receivedPlayers.isEmpty()) return

        discardCards(*receivedStack.toTypedArray())
        updatePlayers(receivedPlayers)
    }

    fun checkForSameColor(card: Card): Boolean {
        val topDiscard = stack.value.lastOrNull() ?: return false


        when {
            topDiscard.imagePath.contains("blue") && card.imagePath.contains("blue") -> return true
            topDiscard.imagePath.contains("red") && card.imagePath.contains("red") -> return true
            topDiscard.imagePath.contains("yellow") && card.imagePath.contains("yellow") -> return true
        }

        return checkForWildCardOnStack()
    }

    fun checkForSameNumber(card: Card): Boolean {
        val topDiscard = stack.value.lastOrNull() ?: return false
        val numberRegex = Regex("""(\d+)(?=_)""") // searches for a number before a "_"

        val topNumber = numberRegex.find(topDiscard.imagePath)?.value
        val cardNumber = numberRegex.find(card.imagePath)?.value

        return topNumber != null && topNumber == cardNumber
    }

    fun checkForWildCard(card: Card): Boolean {
        return card.imagePath.startsWith("wild/")
    }

    private fun checkForWildCardOnStack(): Boolean {
        val topDiscard = stack.value.lastOrNull() ?: return false

        return topDiscard.imagePath.startsWith("wild/")
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
        removeNewerDuplicateByIp()
    }
    // NOTE: Check for host game state updating
    private fun removeNewerDuplicateByIp() {
        val nonNullPlayers = players.filterNotNull()
        val groupedByIp = nonNullPlayers.groupBy { it.ip }

        groupedByIp.forEach { (_, group) ->
            if (group.size > 1) {
                val newest = group.maxByOrNull { it.joinedAt }
                players.replaceAll { player -> if (player == newest) null else player }
            }
        }
    }
}
