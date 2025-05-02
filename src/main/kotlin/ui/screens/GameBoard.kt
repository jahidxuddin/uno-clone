@file:Suppress("DEPRECATION")

package ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import model.Card
import model.Player
import ui.components.card.CardDeckPosition
import ui.components.player.PlayerDeck


@Composable
fun GameBoard(
    players: List<Player?>, stack: List<Card>, onCardPlayed: (Card) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Image(
            painter = painterResource("assets/Backgrounds/background_2.png"),
            contentDescription = "Background Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Image(
            painter = painterResource("assets/Tables/table_red.png"),
            contentDescription = "Table Red",
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize(0.65f)
        )

        Box(
            modifier = Modifier.align(Alignment.Center)
        ) {
            stack.takeLast(5).forEachIndexed { index, card ->
                Image(
                    painter = painterResource("assets/Uno/individual/${card.imagePath}"),
                    contentDescription = "Card ${card.imagePath}",
                    modifier = Modifier.size(150.dp).offset(
                        x = (index * 0.5).dp, y = (index * 0.5).dp
                    ).zIndex(index.toFloat()).graphicsLayer {
                        rotationZ = card.rotation
                    })
            }
        }

        if (players[1] != null) {
            Box(
                modifier = Modifier.align(Alignment.TopCenter).padding(top = 48.dp)
            ) {
                PlayerDeck(players[1]!!, CardDeckPosition.TOP, hidden = true, onCardPlayed)
            }
        }

        if (players[0] != null) {
            Box(
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 48.dp)
            ) {
                PlayerDeck(players[0]!!, CardDeckPosition.BOTTOM, onCardPlayed = onCardPlayed)
            }
        }

        if (players[2] != null) {
            Box(
                modifier = Modifier.align(Alignment.CenterStart).padding(start = 48.dp)
            ) {
                PlayerDeck(players[2]!!, CardDeckPosition.LEFT, hidden = true, onCardPlayed)
            }
        }

        if (players[3] != null) {
            Box(
                modifier = Modifier.align(Alignment.CenterEnd).padding(end = 48.dp)
            ) {
                PlayerDeck(players[3]!!, CardDeckPosition.RIGHT, hidden = true, onCardPlayed)
            }
        }
    }
}
