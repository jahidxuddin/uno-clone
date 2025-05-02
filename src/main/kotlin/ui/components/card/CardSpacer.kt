package ui.components.card

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CardSpacer(cardDeckPosition: CardDeckPosition) {
    when (cardDeckPosition) {
        CardDeckPosition.TOP, CardDeckPosition.BOTTOM -> Spacer(modifier = Modifier.width(6.dp))
        CardDeckPosition.LEFT, CardDeckPosition.RIGHT -> Spacer(modifier = Modifier.height(6.dp))
    }
}
