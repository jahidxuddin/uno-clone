package net

import androidx.compose.runtime.mutableStateListOf
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import model.Card
import model.Player
import model.toDTO
import java.net.Socket

class TcpViewModel {
    private val gson = Gson()
    private var client: TcpClient? = null
    private var server: TcpServer? = null

    var receivedTopDiscard = mutableStateListOf<Card?>(null)
    var connectedClients = mutableStateListOf<Socket?>(null, null, null, null)
    var receivedPlayers = mutableStateListOf<Player?>(null, null, null, null)

    fun startHosting() {
        server = TcpServer(1234, connectedClients)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                server!!.start()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun connectToHost(host: String) {
        client = TcpClient(host, 1234, receivedTopDiscard, receivedPlayers)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                client!!.connect()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun broadcastTopDiscard(topDiscard: Card?) {
        server?.broadcastMessage("TOP DISCARD:${gson.toJson(topDiscard)}")
    }

    fun broadcastPlayers(players: List<Player?>) {
        val playersDTO = players.map { it?.toDTO() }
        val json = gson.toJson(playersDTO)
        server?.broadcastMessage("PLAYERS:$json")
    }


    fun getIp(): String? {
        if (server != null) {
            return server?.getIp()
        }

        return client?.getIp()
    }
}
