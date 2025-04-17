package net

import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import model.Player
import model.TopDiscard
import java.io.*
import java.net.Socket

class TcpClient(
    private val host: String,
    private val port: Int,
    var receivedTopDiscard: SnapshotStateList<TopDiscard?>,
    val receivedPlayers: SnapshotStateList<Player?>
) {
    private val gson = Gson()
    private var socket: Socket? = null
    private val _messages = MutableSharedFlow<String>(extraBufferCapacity = 64)

    suspend fun connect() = withContext(Dispatchers.IO) {
        if (socket != null) return@withContext

        socket = Socket(host, port)
        val reader = BufferedReader(InputStreamReader(socket!!.getInputStream()))

        CoroutineScope(Dispatchers.IO).launch {
            try {
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    val message = line!!
                    if (message.startsWith("TOP DISCARD:")) {
                        val jsonPart = message.removePrefix("TOP DISCARD:")
                        val type = object : TypeToken<TopDiscard?>() {}.type
                        val topDiscard: TopDiscard? = gson.fromJson(jsonPart, type)

                        withContext(Dispatchers.Main) {
                            receivedTopDiscard.clear()
                            receivedTopDiscard.add(topDiscard)
                        }
                    }
                    else if (message.startsWith("PLAYERS:")) {
                        val jsonPart = message.removePrefix("PLAYERS:")
                        val type = object : TypeToken<List<Player?>>() {}.type
                        val players: List<Player?> = gson.fromJson(jsonPart, type)

                        withContext(Dispatchers.Main) {
                            receivedPlayers.clear()
                            receivedPlayers.addAll(players)
                        }
                    } else {
                        _messages.emit(message)
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun getIp(): String? {
        return socket?.inetAddress?.hostAddress
    }
}
