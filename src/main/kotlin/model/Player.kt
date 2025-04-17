package model

import view.card.cardsPerColor

data class Player(
    val id: Int, val ip: String = "localhost", val hand: List<String> = generateRandomHand()
) {
    companion object {
        private fun generateRandomHand(): List<String> {
            val allCards = mutableListOf<String>()
            for ((color, cardList) in cardsPerColor) {
                for (card in cardList) {
                    allCards.add("$color/$card")
                }
            }
            return allCards.shuffled().take(7)
        }
    }
}
