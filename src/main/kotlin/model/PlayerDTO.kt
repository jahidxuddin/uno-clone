package model

import java.time.LocalDate

data class PlayerDTO(
    val id: Int, val ip: String, val hand: List<String>, val joinedAt: String
)

fun PlayerDTO.toPlayer() = Player(
    id = id, ip = ip, initialHand = hand, joinedAt = LocalDate.parse(joinedAt)
)
