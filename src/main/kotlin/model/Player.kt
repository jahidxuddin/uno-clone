package model

import view.card.UNO_DECK

data class Player(
    val id: Int, val ip: String = "localhost", val hand: List<String> = takeCards()
) {
    companion object {
        private fun takeCards(): List<String> {
            val cards = mutableListOf<String>()
            repeat(7) {
                cards.add(UNO_DECK.removeFirst())
            }
            return cards
        }
    }
}
