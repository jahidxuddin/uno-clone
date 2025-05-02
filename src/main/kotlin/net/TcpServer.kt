package net

import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import model.Card
import model.Player
import model.PlayerDTO
import model.toPlayer
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket

class TcpServer(
    private val port: Int,
    private val connectedClients: SnapshotStateList<Socket?>,
    val receivedStack: SnapshotStateList<Card?>,
    val receivedPlayers: SnapshotStateList<Player?>
) {
    private var serverSocket: ServerSocket? = null
    private val gson = Gson()

    private fun broadcastToOthers(message: String, sender: Socket) {
        connectedClients.filterNotNull().filter { it != sender }.forEach { client ->
            try {
                val writer = PrintWriter(client.getOutputStream(), true)
                writer.println(message)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private suspend fun handleClient(clientSocket: Socket) {
        try {
            val reader = BufferedReader(InputStreamReader(clientSocket.getInputStream()))

            var line: String?
            while (reader.readLine().also { line = it } != null) {
                val message = line!!

                when {
                    message.startsWith("STACK:") -> {
                        val jsonPart = message.removePrefix("STACK:")
                        val type = object : TypeToken<List<Card?>>() {}.type
                        val stack: List<Card?> = gson.fromJson(jsonPart, type)
                        withContext(Dispatchers.Main) {
                            receivedStack.clear()
                            receivedStack.addAll(stack)
                        }
                    }

                    message.startsWith("PLAYERS:") -> {
                        val jsonPart = message.removePrefix("PLAYERS:")
                        val type = object : TypeToken<List<PlayerDTO?>>() {}.type
                        val playersDTO: List<PlayerDTO?> = gson.fromJson(jsonPart, type)
                        withContext(Dispatchers.Main) {
                            receivedPlayers.clear()
                            receivedPlayers.addAll(playersDTO.map { it?.toPlayer() })
                        }
                    }
                }

                broadcastToOthers(message, clientSocket)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            connectedClients.remove(clientSocket)
        }
    }

    fun start() {
        this.serverSocket = ServerSocket(port)
        while (true) {
            val clientSocket = this.serverSocket!!.accept()
            connectedClients.add(clientSocket)

            CoroutineScope(Dispatchers.IO).launch {
                handleClient(clientSocket)
            }
        }
    }

    fun getIp(): String? {
        return serverSocket?.inetAddress?.hostAddress
    }
}
