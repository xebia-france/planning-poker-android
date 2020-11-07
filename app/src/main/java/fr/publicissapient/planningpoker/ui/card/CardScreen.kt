package fr.publicissapient.planningpoker.ui.card

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.ui.tooling.preview.Preview
import fr.publicissapient.planningpoker.R
import fr.publicissapient.planningpoker.data.CardRepository
import fr.publicissapient.planningpoker.model.Card
import fr.publicissapient.planningpoker.model.CardSuitType
import fr.publicissapient.planningpoker.ui.theme.PlanningPokerTheme

@Composable
fun CardScreen(
    cardSuit: CardSuitType,
    cardId: String,
    onBackClick: () -> Unit = {},
    backOfTheCard: Boolean = true,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Retour", textAlign = TextAlign.Center) },
                backgroundColor = Color.Black,
                contentColor = Color.White,
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(vectorResource(id = R.drawable.ic_baseline_arrow_back))
                    }
                }
            )
        },
        bodyContent = {
            val suit = CardRepository().allCards()[cardSuit]
            suit?.let {
                it.cards.find { card ->
                    card.id == cardId
                }?.let { card ->
                    CardScreenContent(card, backOfTheCard)
                } ?: error("Cannot find card")
            } ?: error("Cannot find suit $cardSuit")
        }
    )
}

@Composable
fun CardScreenContent(card: Card, backOfTheCard: Boolean) {
    val isBackOfTheCard = remember { mutableStateOf(backOfTheCard) }
    Box(
        modifier = Modifier.fillMaxSize(),
        alignment = Alignment.Center,
    ) {
        val toggleVisibility = {
            isBackOfTheCard.value = !isBackOfTheCard.value
        }
        if (isBackOfTheCard.value) {
            CardBackSideContent(
                onClick = toggleVisibility
            )
        } else {
            CardContent(
                card = card,
                onClick = toggleVisibility
            )
        }
    }
}

@Preview
@Composable
fun CardScreenPreview() {
    PlanningPokerTheme {
        CardScreen(
            cardSuit = CardSuitType.Fibonacci,
            cardId = "fib0"
        )
    }
}