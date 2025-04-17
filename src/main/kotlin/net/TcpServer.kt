package net

import androidx.compose.runtime.snapshots.SnapshotStateList
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket

class TcpServer(private val port: Int, private val connectedClients: SnapshotStateList<Socket?>) {
    private var serverSocket: ServerSocket? = null

    fun start() {
        this.serverSocket = ServerSocket(port)
        while (true) {
            val clientSocket = this.serverSocket!!.accept()
            connectedClients.add(clientSocket)
        }
    }

    fun broadcastMessage(message: String) {
        for (client in connectedClients) {
            if (client != null) {
                try {
                    val out = PrintWriter(client.getOutputStream(), true)
                    out.println(message)
                } catch (e: Exception) {
                    println("Fehler beim Senden an ${client.inetAddress.hostAddress}: ${e.message}")
                }
            }
        }
    }

    fun getIp(): String? {
        return serverSocket?.inetAddress?.hostAddress
    }
}
