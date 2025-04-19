package presentation.components.card

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import model.Player

@Composable
fun PlayerDeck(player: Player, cardDeckPosition: CardDeckPosition, hidden: Boolean = false) {
    val isHorizontal = cardDeckPosition == CardDeckPosition.TOP || cardDeckPosition == CardDeckPosition.BOTTOM

    val modifier = when (cardDeckPosition) {
        CardDeckPosition.TOP, CardDeckPosition.BOTTOM -> Modifier.fillMaxWidth().height(120.dp)
        CardDeckPosition.LEFT, CardDeckPosition.RIGHT -> Modifier.fillMaxHeight().width(120.dp)
    }

    val alignment = when (cardDeckPosition) {
        CardDeckPosition.TOP -> Alignment.TopCenter
        CardDeckPosition.BOTTOM -> Alignment.BottomCenter
        CardDeckPosition.LEFT -> Alignment.CenterStart
        CardDeckPosition.RIGHT -> Alignment.CenterEnd
    }

    Box(modifier = modifier, contentAlignment = alignment) {
        if (isHorizontal) {
            LazyRow {
                items(player.hand) { card ->
                    CardImage(card, hidden, cardDeckPosition) {
                        player.removeCard(card)
                    }
                    CardSpacer(cardDeckPosition)
                }
            }
        } else {
            LazyColumn {
                items(player.hand) { card ->
                    CardImage(card, hidden, cardDeckPosition) {
                        player.removeCard(card)
                    }
                    CardSpacer(cardDeckPosition)
                }
            }
        }
    }
}
