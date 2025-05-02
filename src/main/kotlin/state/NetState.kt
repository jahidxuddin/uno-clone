package state

import androidx.compose.runtime.mutableStateListOf
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import model.Card
import model.Player
import net.TcpClient
import net.TcpServer
import java.io.PrintWriter
import java.net.Socket

class NetState {
    private val gson = Gson()
    private var client: TcpClient? = null
    private var server: TcpServer? = null

    var connectedClients = mutableStateListOf<Socket?>(null, null, null, null)
    var receivedStack = mutableStateListOf<Card?>(null)
    var receivedPlayers = mutableStateListOf<Player?>(null, null, null, null)

    fun startHosting() {
        server = TcpServer(1234, connectedClients, receivedStack, receivedPlayers)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                server!!.start()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun connectToHost(host: String) {
        client = TcpClient(host, 1234, receivedStack, receivedPlayers)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                client!!.connect()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun <T> broadcastData(key: String, value: T) {
        if (server != null) {
            broadcastJson("${key}:${gson.toJson(value)}")
        }
        if (client != null) {
            sendBroadcastRequest("${key}:${gson.toJson(value)}")
        }
    }

    fun getIp(): String? {
        return client?.getIp() ?: server?.getIp()
    }

    private fun broadcastJson(json: String) {
        for (client in connectedClients.filterNotNull()) {
            try {
                val out = PrintWriter(client.getOutputStream(), true)
                out.println(json)
            } catch (e: Exception) {
                println("Error while sending to ${client.inetAddress.hostAddress}: ${e.message}")
            }
        }
    }

    private fun sendBroadcastRequest(jsonBody: String) {
        client?.sendMessage(jsonBody)
    }
}
