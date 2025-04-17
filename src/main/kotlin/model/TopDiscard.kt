package model

import view.card.cardsPerColor
import kotlin.collections.random

data class TopDiscard(val imagePath: String, val rotation: Float) {
    companion object {
        fun generateTopDiscard(): TopDiscard {
            val randomTopDiscardColor = cardsPerColor.keys.random()
            val image = randomTopDiscardColor + "/" + cardsPerColor[randomTopDiscardColor]?.random()
            val rotation = (-10..10).random().toFloat()
            return TopDiscard(image, rotation)
        }
    }
}
