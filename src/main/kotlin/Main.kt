@file:Suppress("DEPRECATION")

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import java.awt.Point
import java.awt.Toolkit
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

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

            // Top
            Box(
                modifier = Modifier.align(Alignment.TopCenter).padding(top = 48.dp)
            ) {
                CardDeck(
                    listOf(
                        "green/1_green.png", "blue/inverse_blue.png", "red/8_red.png", "yellow/block_yellow.png",
                        "green/1_green.png", "blue/inverse_blue.png", "red/8_red.png",
                    ), Position.TOP, hidden = true
                )
            }

            // Bottom
            Box(
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 48.dp)
            ) {
                CardDeck(
                    listOf(
                        "green/1_green.png", "blue/inverse_blue.png", "red/8_red.png", "yellow/block_yellow.png",
                        "green/1_green.png", "blue/inverse_blue.png", "red/8_red.png",
                    ), Position.BOTTOM
                )
            }

            // Left
            Box(
                modifier = Modifier.align(Alignment.CenterStart).padding(start = 48.dp)
            ) {
                CardDeck(
                    listOf(
                        "green/1_green.png", "blue/inverse_blue.png", "red/8_red.png", "yellow/block_yellow.png",
                        "green/1_green.png", "blue/inverse_blue.png", "red/8_red.png",
                    ), Position.LEFT, hidden = true
                )
            }

            // Right
            Box(
                modifier = Modifier.align(Alignment.CenterEnd).padding(end = 48.dp)
            ) {
                CardDeck(
                    listOf(
                        "green/1_green.png", "blue/inverse_blue.png", "red/8_red.png", "yellow/block_yellow.png",
                        "green/1_green.png", "blue/inverse_blue.png", "red/8_red.png",
                    ), Position.RIGHT, hidden = true
                )
            }
        }
    }
}

enum class Position {
    LEFT, RIGHT, TOP, BOTTOM
}

@Composable
fun CardDeck(cardList: List<String>, position: Position, hidden: Boolean = false) {
    val isHorizontal = position == Position.TOP || position == Position.BOTTOM

    val modifier = when (position) {
        Position.TOP, Position.BOTTOM -> Modifier.fillMaxWidth().height(120.dp)
        Position.LEFT, Position.RIGHT -> Modifier.fillMaxHeight().width(120.dp)
    }

    val alignment = when (position) {
        Position.TOP -> Alignment.TopCenter
        Position.BOTTOM -> Alignment.BottomCenter
        Position.LEFT -> Alignment.CenterStart
        Position.RIGHT -> Alignment.CenterEnd
    }

    Box(modifier = modifier, contentAlignment = alignment) {
        if (isHorizontal) {
            LazyRow {
                items(cardList) { card ->
                    CardImage(card, hidden, position)
                    CardSpacer(position)
                }
            }
        } else {
            LazyColumn {
                items(cardList) { card ->
                    CardImage(card, hidden, position)
                    CardSpacer(position)
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun CardImage(card: String, hidden: Boolean, position: Position) {
    val imagePath = if (hidden) "assets/Uno/individual/card back/card_back.png"
    else "assets/Uno/individual/${card}"

    var isHovered by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        if (position == Position.BOTTOM && isHovered) 0.95f else 1f, label = "cardScale"
    )

    Image(
        painter = painterResource(imagePath),
        contentDescription = "Uno Card",
        contentScale = if (isHovered) ContentScale.Inside else ContentScale.Fit,
        modifier = Modifier.size(90.dp).aspectRatio(0.66f).graphicsLayer(
            scaleX = scale, scaleY = scale
        ).rotateCard(position).pointerHoverIcon(
            if (position == Position.BOTTOM) loadCursorIcon("assets/Cursors/Hand.png") else loadCursorIcon("assets/Cursors/Default.png")
        ).onPointerEvent(PointerEventType.Enter) {
            if (position == Position.BOTTOM) isHovered = true
        }.onPointerEvent(PointerEventType.Exit) {
            isHovered = false
        })
}

fun loadCursorIcon(resourcePath: String): PointerIcon {
    val resourceStream = Thread.currentThread().contextClassLoader.getResourceAsStream(resourcePath)
        ?: error("Resource not found: $resourcePath")

    val bufferedImage: BufferedImage = ImageIO.read(resourceStream)
    val toolkit = Toolkit.getDefaultToolkit()
    val awtCursor = toolkit.createCustomCursor(bufferedImage, Point(0, 0), "customCursor")
    return PointerIcon(awtCursor)
}

@Composable
fun CardSpacer(position: Position) {
    when (position) {
        Position.TOP, Position.BOTTOM -> Spacer(modifier = Modifier.width(6.dp))
        Position.LEFT, Position.RIGHT -> Spacer(modifier = Modifier.height(6.dp))
    }
}

fun Modifier.rotateCard(position: Position): Modifier {
    val rotationAngle = when (position) {
        Position.LEFT -> 90f
        Position.RIGHT -> -90f
        Position.TOP -> 180f
        Position.BOTTOM -> 0f
    }
    return this.graphicsLayer(rotationZ = rotationAngle)
}
