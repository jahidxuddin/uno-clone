@file:Suppress("DEPRECATION")

package view.gameboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import model.Player
import model.TopDiscard
import view.card.CardDeck
import view.card.CardDeckPosition

@Composable
fun GameBoard(players: List<Player?>, topDiscard: TopDiscard) {
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

        Image(
            painter = painterResource("assets/Uno/individual/${topDiscard.imagePath}"),
            contentDescription = "Top Discard",
            modifier = Modifier.size(150.dp).align(Alignment.Center).graphicsLayer {
                rotationZ = topDiscard.rotation
            })

        if (players[1] != null) {
            Box(
                modifier = Modifier.align(Alignment.TopCenter).padding(top = 48.dp)
            ) {
                CardDeck(players[1]!!.hand, CardDeckPosition.TOP, hidden = true)
            }
        }

        if (players[0] != null) {
            Box(
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 48.dp)
            ) {
                CardDeck(players[0]!!.hand, CardDeckPosition.BOTTOM)
            }
        }

        if (players[2] != null) {
            Box(
                modifier = Modifier.align(Alignment.CenterStart).padding(start = 48.dp)
            ) {
                CardDeck(players[2]!!.hand, CardDeckPosition.LEFT, hidden = true)
            }
        }

        if (players[3] != null) {
            Box(
                modifier = Modifier.align(Alignment.CenterEnd).padding(end = 48.dp)
            ) {
                CardDeck(players[3]!!.hand, CardDeckPosition.RIGHT, hidden = true)
            }
        }
    }
}
