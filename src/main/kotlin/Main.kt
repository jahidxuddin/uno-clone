@file:Suppress("DEPRECATION")

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import view.card.CardDeck
import view.card.CardDeckPosition
import view.card.cardsPerColor
import view.util.loadCursorIcon

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication, title = "Card Games", state = rememberWindowState(
            position = WindowPosition.Aligned(Alignment.Center),
            size = DpSize(1920.dp, 1080.dp),
            placement = WindowPlacement.Maximized,
        ), resizable = false, undecorated = true
    ) {
        App()
    }
}

@Composable
fun App() {
    val randomTopDiscardColor by remember {
        mutableStateOf(cardsPerColor.keys.random())
    }
    val topDiscard by remember {
        mutableStateOf(randomTopDiscardColor + "/" + cardsPerColor[randomTopDiscardColor]?.random())
    }

    val rotation by remember {
        mutableStateOf((-10..10).random().toFloat())
    }

    MaterialTheme {
        Box(
            modifier = Modifier.fillMaxSize().background(Color(0xFF113540))
                .pointerHoverIcon(loadCursorIcon("assets/Cursors/Default.png")), contentAlignment = Alignment.Center
        ) {
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
                painter = painterResource("assets/Uno/individual/$topDiscard"),
                contentDescription = "Top Discard",
                modifier = Modifier.size(150.dp).align(Alignment.Center).graphicsLayer {
                    rotationZ = rotation
                })

            Box(
                modifier = Modifier.align(Alignment.TopCenter).padding(top = 48.dp)
            ) {
                CardDeck(generateRandomUnoHand(), CardDeckPosition.TOP, hidden = true)
            }

            Box(
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 48.dp)
            ) {
                CardDeck(generateRandomUnoHand(), CardDeckPosition.BOTTOM)
            }

            Box(
                modifier = Modifier.align(Alignment.CenterStart).padding(start = 48.dp)
            ) {
                CardDeck(generateRandomUnoHand(), CardDeckPosition.LEFT, hidden = true)
            }

            Box(
                modifier = Modifier.align(Alignment.CenterEnd).padding(end = 48.dp)
            ) {
                CardDeck(generateRandomUnoHand(), CardDeckPosition.RIGHT, hidden = true)
            }
        }
    }
}

fun generateRandomUnoHand(): List<String> {
    val allCards = mutableListOf<String>()
    for ((color, cardList) in cardsPerColor) {
        for (card in cardList) {
            allCards.add("$color/$card")
        }
    }

    return allCards.shuffled().take(7)
}
