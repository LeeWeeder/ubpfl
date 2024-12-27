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

package com.leeweeder.ubpfl.ui

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.leeweeder.ubpfl.R
import com.leeweeder.ubpfl.ui.progression_selection.ProgressionSelectionScreen
import com.leeweeder.ubpfl.ui.routine.RoutineScreen
import com.leeweeder.ubpfl.ui.warm_up.WarmUpScreen
import com.leeweeder.ubpfl.util.Screen
import androidx.compose.material3.NavigationBar as Material3NavigationBar

val LocalNavController = compositionLocalOf<NavHostController> { error("NavHostController error") }

@Composable
fun MainNavigation(uiState: MainActivityUiState.Success) {
    val isNavBarVisible = rememberSaveable {
        mutableStateOf(true)
    }

    val navController = rememberNavController()

    CompositionLocalProvider(value = LocalNavController provides navController) {

        isNavBarVisible.value = NavigationBarItems.entries.any {
            isCurrentScreen(it.screen)
        }

        Scaffold(bottomBar = {
            NavigationBar(isNavBarVisible = isNavBarVisible.value)
        }) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = Screen.Routine,
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

@Composable
fun NavigationBar(isNavBarVisible: Boolean) {
    AnimatedVisibility(
        visible = isNavBarVisible,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it })
    ) {
        Material3NavigationBar {
            val navController = LocalNavController.current

            NavigationBarItems.entries.forEach { item ->
                val selected = isCurrentScreen(item.screen)
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

@SuppressLint("RestrictedApi")
@Composable
/**
 * A utility function that checks if the given screen is the current screen
 *
 * @param screen The screen to be compared to
 *
 * @return true if current screen is the given screen
 * */
private fun isCurrentScreen(screen: Screen): Boolean {
    val navController = LocalNavController.current
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    val currentDestination = navBackStackEntry?.destination

    return currentDestination?.hasRoute(screen::class) == true
}
