package fr.publicissapient.planningpoker.ui.card

import androidx.compose.foundation.Image
import androidx.compose.foundation.InteractionState
import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawShadow
import androidx.compose.ui.drawLayer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.ui.tooling.preview.Preview
import fr.publicissapient.planningpoker.data.CardRepository
import fr.publicissapient.planningpoker.model.Card
import fr.publicissapient.planningpoker.model.CardSuit
import fr.publicissapient.planningpoker.model.CardSuitType
import fr.publicissapient.planningpoker.ui.theme.PlanningPokerTheme
import fr.publicissapient.planningpoker.ui.theme.getThemeColor

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CardContent(
    cardSuit: CardSuit,
    card: Card,
    onClick: () -> Unit,
    ratio: Float = 1f
) {
    val shape = RoundedCornerShape(32.dp * ratio)
    val defaultElevation = ButtonConstants.defaultElevation(4.dp)
    val interactionState = remember { InteractionState() }
    val elevation = defaultElevation.elevation(true, interactionState)
    Card(
        modifier = Modifier
            .padding(16.dp * ratio)
            .width(316.dp * ratio)
            .height(470.dp * ratio)
            // We need to draw the shadow manually otherwise it'll not be drawn by the Card since
            // we clip the modifier for the ripple. This could be avoided if the card would let us
            // pass the clickable as an "afterModifier" that would be applied after the Card internals.
            .drawShadow(elevation, shape)
            .clip(shape)
            .clickable(
                onClick = onClick,
                interactionState = interactionState
            ),
        shape = shape,
        backgroundColor = Color.White,
        elevation = elevation
    ) {
        Surface(
            modifier = Modifier.padding(16.dp * ratio),
            color = cardSuit.color.getThemeColor(),
            shape = RoundedCornerShape(16.dp * ratio)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Count(card.name, ratio = ratio)
                Image(
                    asset = imageResource(id = card.imageResourceId),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp * ratio),
                    contentScale = ContentScale.FillWidth
                )
                Text(
                    card.description,
                    modifier = Modifier.padding(horizontal = 16.dp * ratio),
                    style = MaterialTheme.typography.body1.copy(
                        textAlign = TextAlign.Center,
                        fontSize = MaterialTheme.typography.body1.fontSize * ratio
                    )
                )
                Count(card.name, Modifier.drawLayer(rotationZ = -180f), ratio)
            }
        }
    }
}

@Composable
private fun Count(cardName: String, modifier: Modifier = Modifier, ratio: Float = 1f) =
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp * ratio),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val style = MaterialTheme.typography.body1.copy(
            fontSize = 40.sp * ratio,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = cardName,
            color = Color.White,
            style = style,
            modifier = modifier
        )
        Text(
            text = cardName,
            color = Color.White,
            style = style,
            modifier = modifier
        )
    }

@Preview
@Composable
fun CardContentPreview() {
    PlanningPokerTheme {
        val cardSuit = CardRepository().allCards()[CardSuitType.Fibonacci]
        cardSuit?.let {
            CardContent(
                cardSuit = cardSuit,
                card = cardSuit.cards[0],
                onClick = {}
            )
        } ?: error("Should not happen!")
    }
}