package fr.publicissapient.planningpoker.ui.screen

import androidx.compose.animation.core.FloatPropKey
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.transitionDefinition
import androidx.compose.animation.transition
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.WithConstraints
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import fr.publicissapient.planningpoker.R
import fr.publicissapient.planningpoker.data.CardRepository
import fr.publicissapient.planningpoker.model.Card
import fr.publicissapient.planningpoker.model.CardSuitType
import fr.publicissapient.planningpoker.ui.body.BodyWithBlop
import fr.publicissapient.planningpoker.ui.card.CardBackSideContent
import fr.publicissapient.planningpoker.ui.card.CardContent
import fr.publicissapient.planningpoker.ui.theme.PlanningPokerTheme

val backRotationState = FloatPropKey("backRotationState")
val frontRotationState = FloatPropKey("frontRotationState")
val backOpacity = FloatPropKey("backOpacity")
val frontOpacity = FloatPropKey("frontOpacity")

enum class FlipState {
    IDLE, FLIPPED
}

val flipTransitionDefinition = transitionDefinition<FlipState> {
    state(FlipState.IDLE) {
        this[backRotationState] = 0f
        this[frontRotationState] = 270f
        this[backOpacity] = 1f
        this[frontOpacity] = 0f
    }
    state(FlipState.FLIPPED) {
        this[backRotationState] = 90f
        this[frontRotationState] = 360f
        this[backOpacity] = 0f
        this[frontOpacity] = 1f
    }
    val duration = 400
    transition(fromState = FlipState.IDLE, toState = FlipState.FLIPPED) {
        backRotationState using keyframes {
            durationMillis = duration
            90f at duration / 2
        }
        frontRotationState using keyframes {
            durationMillis = duration
            270f at duration / 2
            360f at duration
        }
        backOpacity using keyframes {
            durationMillis = duration
            1f at duration / 2
            0f at duration
        }
        frontOpacity using keyframes {
            durationMillis = duration
            1f at duration / 2
        }
    }
    transition(fromState = FlipState.FLIPPED, toState = FlipState.IDLE) {
        backRotationState using keyframes {
            durationMillis = duration
            90f at duration / 2
            0f at duration
        }
        frontRotationState using keyframes {
            durationMillis = duration
            270f at duration / 2
        }
        backOpacity using keyframes {
            durationMillis = duration
            1f at duration / 2
        }
        frontOpacity using keyframes {
            durationMillis = duration
            1f at duration / 2
            0f at duration
        }
    }
}

@Composable
fun CardScreen(
    cardSuit: CardSuitType,
    cardId: String,
    onBackClick: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Retour", textAlign = TextAlign.Center) },
                backgroundColor = MaterialTheme.colors.primary,
                contentColor = MaterialTheme.colors.onPrimary,
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(vectorResource(id = R.drawable.ic_baseline_arrow_back))
                    }
                }
            )
        },
        bodyContent = {
            BodyWithBlop {
                val cards = CardRepository().allCards(
                    MaterialTheme.colors.secondary,
                    MaterialTheme.colors.onSecondary
                )[cardSuit]
                cards?.let {
                    it.find { card ->
                        card.name == cardId
                    }?.let { card ->
                        CardScreenContent(card)
                    } ?: error("Cannot find card")
                } ?: error("Cannot find suit $cardSuit")
            }
        }
    )
}

@Composable
fun CardScreenContent(card: Card) {
    WithConstraints {
        val width = maxWidth * .85f
        Flip(backSide = {
            CardBackSideContent(
                modifier = it,
                width = width
            )
        }, frontSide = {
            CardContent(
                modifier = it,
                card = card,
                width = width
            )
        })
    }
}

@Composable
fun Flip(
    backSide: @Composable (Modifier) -> Unit,
    frontSide: @Composable (Modifier) -> Unit,
) {
    val animatingFlip = remember { mutableStateOf(false) }
    val flip = {
        animatingFlip.value = !animatingFlip.value
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val state = transition(
            definition = flipTransitionDefinition,
            initState = FlipState.IDLE,
            toState = if (!animatingFlip.value) {
                FlipState.IDLE
            } else {
                FlipState.FLIPPED
            }
        )
        Box(
            modifier = Modifier.fillMaxSize().alpha(state[backOpacity]),
            contentAlignment = Alignment.Center
        ) {
            backSide(Modifier.graphicsLayer(rotationY = state[backRotationState]))
        }
        Box(
            modifier = Modifier.fillMaxSize().alpha(state[frontOpacity]),
            contentAlignment = Alignment.Center
        ) {
            frontSide(Modifier.graphicsLayer(rotationY = state[frontRotationState]))
        }
        Box(
            modifier = Modifier.fillMaxSize().clickable(onClick = flip)
        )
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