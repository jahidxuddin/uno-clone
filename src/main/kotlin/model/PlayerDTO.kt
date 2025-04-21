package model

data class PlayerDTO(
    val id: Int, val ip: String, val hand: List<String>
)

fun PlayerDTO.toPlayer() = Player(
    id = id, ip = ip, initialHand = hand
)
