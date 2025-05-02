package model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ui.components.card.UNO_DECK

data class Player(
    val id: Int, val ip: String = "localhost", private val initialHand: List<String>? = takeCards()
) {
    private var _hand by mutableStateOf(initialHand)
    var hand: List<String>
        get() = _hand ?: emptyList()
        private set(value) {
            _hand = value
        }

    fun removeCard(card: String) {
        val index = hand.indexOf(card)
        if (index != -1) {
            hand = hand.subList(0, index) + hand.subList(index + 1, hand.size)
        }
    }

    fun addCard(card: String) {
        hand = hand + card
    }

    companion object {
        private fun takeCards(): List<String> = buildList {
            repeat(7) {
                add(UNO_DECK.removeFirst())
            }
        }
    }
}

fun Player.toDTO() = PlayerDTO(
    id = id, ip = ip, hand = hand
)
