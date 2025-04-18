package model

import view.card.UNO_DECK

data class TopDiscard(val imagePath: String, val rotation: Float) {
    companion object {
        fun takeTopDiscard(): TopDiscard {
            val topCard = UNO_DECK.removeFirst()
            if (topCard.contains("wild") || topCard.contains("inverse") || topCard.contains("2plus")) {
                UNO_DECK.addLast(topCard)
                return takeTopDiscard()
            }
            val rotation = (-10..10).random().toFloat()
            return TopDiscard(topCard, rotation)
        }
    }
}
