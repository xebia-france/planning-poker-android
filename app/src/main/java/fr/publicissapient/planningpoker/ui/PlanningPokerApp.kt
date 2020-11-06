package fr.publicissapient.planningpoker.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigate
import androidx.navigation.compose.rememberNavController
import fr.publicissapient.planningpoker.model.CardSuitType
import fr.publicissapient.planningpoker.ui.card.CardScreen
import fr.publicissapient.planningpoker.ui.nav.Screen
import fr.publicissapient.planningpoker.ui.theme.PlanningPokerTheme

@Composable
fun PlanningPokerApp() {
    val navController = rememberNavController()
    val screenCard = Screen.Card()
    PlanningPokerTheme {
        NavHost(navController = navController, startDestination = Screen.CardType.route) {
            composable(Screen.CardType.route) {
                CardTypeScreen {
                    navController.navigate("cards/${it.type}")
                }
            }
            composable(Screen.CardList.route, Screen.CardList.arguments) {
                it.arguments?.let { bundle ->
                    val cardSuitTypeBundle = bundle.getString(Screen.CardList.navArgCardSuit)
                    cardSuitTypeBundle?.let { cardSuitTypeString ->
                        val cardSuitType = when (cardSuitTypeString) {
                            "fibonacci" -> CardSuitType.Fibonacci
                            "t-shirt" -> CardSuitType.TShirt
                            else -> error("Unknown card suit type! : $cardSuitTypeString")
                        }
                        CardListScreen(
                            cardSuitType,
                            { cardId ->
                                navController.navigate("cards/${cardSuitType.type}/$cardId")
                            },
                            navController::popBackStack
                        )
                    } ?: error("Card suit required!")
                } ?: error("Arguments required!")
            }
            composable(screenCard.route, screenCard.arguments) {
                it.arguments?.let { bundle ->
                    val cardSuitTypeBundle = bundle.getString(Screen.CardList.navArgCardSuit)
                    cardSuitTypeBundle?.let { cardSuitTypeString ->
                        val cardSuitType = when (cardSuitTypeString) {
                            "fibonacci" -> CardSuitType.Fibonacci
                            "t-shirt" -> CardSuitType.TShirt
                            else -> error("Unknown card suit type! : $cardSuitTypeString")
                        }
                        bundle.getString(screenCard.navArgCardId)?.let { cardId ->
                            CardScreen(
                                cardSuitType,
                                cardId,
                                navController::popBackStack
                            )
                        }
                    } ?: error("Card suit required!")
                } ?: error("Card screen should have arguments!")
            }
        }
    }
}
