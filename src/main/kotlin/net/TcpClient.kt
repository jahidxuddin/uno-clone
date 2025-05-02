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
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

class TcpClient(
    private val host: String,
    private val port: Int,
    val receivedStack: SnapshotStateList<Card?>,
    val receivedPlayers: SnapshotStateList<Player?>
) {
    private val gson = Gson()
    private var socket: Socket? = null

    suspend fun connect() = withContext(Dispatchers.IO) {
        if (socket != null) return@withContext

        socket = Socket(host, port)
        val reader = BufferedReader(InputStreamReader(socket!!.getInputStream()))

        CoroutineScope(Dispatchers.IO).launch {
            try {
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
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun sendMessage(message: String) {
        socket?.let { socket ->
            try {
                val writer = PrintWriter(socket.getOutputStream(), true)
                writer.println(message)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun getIp(): String? {
        return socket?.inetAddress?.hostAddress
    }
}
