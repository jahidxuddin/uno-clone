@file:Suppress("DEPRECATION")

package view.card

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import view.util.loadCursorIcon

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CardImage(card: String, hidden: Boolean, cardDeckPosition: CardDeckPosition) {
    val imagePath = if (hidden) "assets/Uno/individual/card back/card_back.png"
    else "assets/Uno/individual/${card}"

    var isHovered by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        if (cardDeckPosition == CardDeckPosition.BOTTOM && isHovered) 0.95f else 1f, label = "cardScale"
    )

    Image(
        painter = painterResource(imagePath),
        contentDescription = "Uno Card",
        contentScale = if (isHovered) ContentScale.Inside else ContentScale.Fit,
        modifier = Modifier.size(90.dp).aspectRatio(0.66f).graphicsLayer(
            scaleX = scale, scaleY = scale
        ).rotateCard(cardDeckPosition).pointerHoverIcon(
            if (cardDeckPosition == CardDeckPosition.BOTTOM) loadCursorIcon("assets/Cursors/Hand.png") else loadCursorIcon("assets/Cursors/Default.png")
        ).onPointerEvent(PointerEventType.Enter) {
            if (cardDeckPosition == CardDeckPosition.BOTTOM) isHovered = true
        }.onPointerEvent(PointerEventType.Exit) {
            isHovered = false
        })
}

fun Modifier.rotateCard(cardDeckPosition: CardDeckPosition): Modifier {
    val rotationAngle = when (cardDeckPosition) {
        CardDeckPosition.LEFT -> 90f
        CardDeckPosition.RIGHT -> -90f
        CardDeckPosition.TOP -> 180f
        CardDeckPosition.BOTTOM -> 0f
    }
    return this.graphicsLayer(rotationZ = rotationAngle)
}
