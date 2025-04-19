package net

import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import model.Player
import model.PlayerDTO
import model.TopDiscard
import model.toPlayer
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.Socket

class TcpClient(
    private val host: String,
    private val port: Int,
    var receivedTopDiscard: SnapshotStateList<TopDiscard?>,
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
                        message.startsWith("TOP DISCARD:") -> {
                            val jsonPart = message.removePrefix("TOP DISCARD:")
                            val type = object : TypeToken<TopDiscard?>() {}.type
                            val topDiscard: TopDiscard? = gson.fromJson(jsonPart, type)
                            withContext(Dispatchers.Main) {
                                receivedTopDiscard.clear()
                                receivedTopDiscard.add(topDiscard)
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

    fun getIp(): String? {
        return socket?.inetAddress?.hostAddress
    }
}
