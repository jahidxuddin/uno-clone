package model

import ui.components.card.UNO_DECK

data class Card(val imagePath: String, val rotation: Float) {
    companion object {
        fun takeTopDiscard(): Card {
            val topCard = UNO_DECK.removeFirst()
            if (topCard.contains("wild") || topCard.contains("inverse") || topCard.contains("2plus")) {
                UNO_DECK.addLast(topCard)
                return takeTopDiscard()
            }
            val rotation = (-10..10).random().toFloat()
            return Card(topCard, rotation)
        }
    }
}
