/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.leeweeder.yluxthenics.ui

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.leeweeder.yluxthenics.R
import com.leeweeder.yluxthenics.ui.preparation.PreparationScreen
import com.leeweeder.yluxthenics.ui.progression_selection.ProgressionSelectionScreen
import com.leeweeder.yluxthenics.ui.routine.RoutineScreen
import com.leeweeder.yluxthenics.ui.warm_up.WarmUpScreen
import com.leeweeder.yluxthenics.util.Screen
import androidx.compose.material3.NavigationBar as Material3NavigationBar

val LocalNavController = compositionLocalOf<NavHostController> { error("NavHostController error") }

@Composable
fun MainNavigation(uiState: MainActivityUiState.Success) {
    val isNavBarVisible = rememberSaveable {
        mutableStateOf(true)
    }
    val isStartWorkoutVisible = rememberSaveable {
        mutableStateOf(true)
    }
    val navController = rememberNavController()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    isNavBarVisible.value = NavigationBarItems.entries.any {
        currentDestination?.hasRoute(it.screen::class) == true
    }

    isStartWorkoutVisible.value = currentDestination?.hasRoute(Screen.Routine::class) == true

    CompositionLocalProvider(value = LocalNavController provides navController) {
        Scaffold(bottomBar = {
            NavigationBar(
                isNavBarVisible = isNavBarVisible.value,
                StartWorkoutButtonState(isStartWorkoutVisible.value) {
                    navController.navigate(Screen.Preparation)
                })
        }) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = Screen.WarmUp,
                enterTransition = {
                    EnterTransition.None
                },
                exitTransition = {
                    ExitTransition.None
                }
            ) {
                composable<Screen.Routine> {
                    RoutineScreen(
                        paddingValues = paddingValues,
                        onNavigateToProgressionSelectionScreen = {
                            navController.navigate(
                                Screen.ProgressionSelection(
                                    it
                                )
                            )
                        },
                        uiState = uiState
                    )
                }
                composable<Screen.Statistics> {
                    Column {
                        Text(text = "Statistics Screen")
                    }
                }
                composable<Screen.Settings> {
                    Column {
                        Text(text = "Settings Screen")
                    }
                }
                composable<Screen.ProgressionSelection> {
                    ProgressionSelectionScreen(
                        onCloseProgressionScreen = { navController.popBackStack() }
                    )
                }
                composable<Screen.WarmUp> {
                    WarmUpScreen()
                }
                composable<Screen.Preparation>(enterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Down,
                        animationSpec = tween(durationMillis = 500)
                    )
                }) {
                    PreparationScreen(onFinishCountDown = {
                        navController.navigate(Screen.WarmUp) {
                            popUpTo(Screen.Routine)
                        }
                    }) {
                        navController.popBackStack()
                    }
                }
            }
        }
    }
}

enum class NavigationBarItems(
    val text: String, val icon: NavigationBarItemIcon, val screen: Screen
) {
    Routine(
        text = "Routine", icon = NavigationBarItemIcon(
            selectedIcon = R.drawable.exercise_24px_filled,
            deselectedIcon = R.drawable.exercise_24px
        ), screen = Screen.Routine
    ),
    Statistics(
        text = "Statistics", icon = NavigationBarItemIcon(
            selectedIcon = R.drawable.monitoring_24px, deselectedIcon = R.drawable.monitoring_24px
        ), screen = Screen.Statistics
    ),
    Settings(
        text = "Settings", icon = NavigationBarItemIcon(
            selectedIcon = R.drawable.settings_24px_filled,
            deselectedIcon = R.drawable.settings_24px_outlined
        ), screen = Screen.Settings
    ),
}

data class NavigationBarItemIcon(
    @DrawableRes val selectedIcon: Int, @DrawableRes val deselectedIcon: Int
)

data class StartWorkoutButtonState(
    val visible: Boolean,
    val onClick: () -> Unit
)

@Composable
fun NavigationBar(isNavBarVisible: Boolean, startWorkoutButtonState: StartWorkoutButtonState) {
    AnimatedVisibility(
        visible = isNavBarVisible,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it })
    ) {
        Column {
            AnimatedVisibility(
                startWorkoutButtonState.visible,
                enter = slideInVertically(initialOffsetY = {
                    it
                }),
                exit = slideOutVertically(targetOffsetY = {
                    it
                })
            ) {
                BottomAppBar(windowInsets = WindowInsets(0)) {
                    Button(
                        onClick = startWorkoutButtonState.onClick, modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Text(text = "Start workout")
                    }
                }
            }
            Material3NavigationBar {
                val navController = LocalNavController.current
                val navBackStackEntry by navController.currentBackStackEntryAsState()

                val currentDestination = navBackStackEntry?.destination

                NavigationBarItems.entries.forEach { item ->
                    val selected = currentDestination?.hasRoute(item.screen::class) ?: false
                    NavigationBarItem(selected = selected, onClick = {
                        navController.navigate(item.screen) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }, icon = {
                        Icon(
                            painter = painterResource(id = if (selected) item.icon.selectedIcon else item.icon.deselectedIcon),
                            contentDescription = null
                        )
                    }, label = {
                        Text(text = item.text)
                    })
                }
            }
        }
    }
}
