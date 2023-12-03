package com.immanlv.trem.presentation

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.TableChart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.immanlv.trem.domain.model.ColorMode
import com.immanlv.trem.presentation.screens.attendance.AttendanceView
import com.immanlv.trem.presentation.screens.attendance.AttendanceViewModel
import com.immanlv.trem.presentation.screens.home.HomeScreen
import com.immanlv.trem.presentation.screens.home.HomeScreenViewModel
import com.immanlv.trem.presentation.screens.login.LoginScreen
import com.immanlv.trem.presentation.screens.profile.ProfileScreen
import com.immanlv.trem.presentation.screens.profile.ProfileScreenViewModel
import com.immanlv.trem.presentation.screens.settings.SettingScreenViewModel
import com.immanlv.trem.presentation.screens.settings.SettingsScreen
import com.immanlv.trem.presentation.screens.timetable.TimetableViewModel
import com.immanlv.trem.presentation.screens.timetable.Tts
import com.immanlv.trem.presentation.screens.timetableBuilder.TimetableBuilderScreen
import com.immanlv.trem.presentation.theme.TremTheme
import com.immanlv.trem.presentation.util.BottomNavigationItem
import com.immanlv.trem.presentation.util.FloatingBottomNavigation
import com.immanlv.trem.presentation.util.FloatingNavigationBarItem
import com.immanlv.trem.presentation.util.Screen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.actionBar?.hide()
        val splashScreen = installSplashScreen()


        splashScreen.apply {
            setKeepOnScreenCondition {
                !viewModel.hasInitialized.value
            }
        }


        setContent {

            var sysDarkTheme by remember { mutableStateOf(false) }

            val windowClass = calculateWindowSizeClass(this)

            val state = remember {
                RailStatus()
            }


            val showNavigationRail = windowClass.widthSizeClass != WindowWidthSizeClass.Compact

            sysDarkTheme = when (viewModel.appPreference.value.colorMode) {
                ColorMode.Unspecified -> isSystemInDarkTheme()
                ColorMode.Dark -> true
                ColorMode.Light -> false
            }

            Box(
                Modifier
//                .safeDrawingPadding()
            ) {
                TremTheme(darkTheme = sysDarkTheme) {
                    // A surface container using the 'background' color from the theme
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        val navController = rememberNavController()
                        val loggedIn = viewModel.localLoginStatus.value
                        val scope = rememberCoroutineScope()
                        val context = LocalContext.current


                        LaunchedEffect(key1 = loggedIn) {
                            Log.d("TAG", "onCreate: $loggedIn")
                            if (loggedIn) {
                                scope.launch {
                                    Toast.makeText(context, "Logged In", Toast.LENGTH_SHORT).show()
                                    navController.navigate(Screen.MainNavGraph.route) {
                                        popUpTo(Screen.AuthNavGraph.route) {
                                            inclusive = true
                                        }
                                    }
                                }
                            } else {
                                scope.launch {
                                    Toast.makeText(context, "Logged In", Toast.LENGTH_SHORT).show()
                                    navController.navigate(Screen.Login.route) {
                                        popUpTo(Screen.MainNavGraph.route) {
                                            inclusive = true
                                        }
                                    }
                                }
                            }
                        }
                        val navigationItems = bottomNavigationItems()
                        val currentBackstack = navController.currentBackStackEntryAsState()
                        val currentDestination = currentBackstack.value?.destination

                        Scaffold(bottomBar = {
                            if (currentDestination?.route != Screen.Login.route && currentDestination?.route != Screen.SettingsMain.route) if (!showNavigationRail) {
                                Box(
                                    Modifier
                                        .padding(vertical = 12.dp)
                                        .fillMaxWidth()
                                ) {
                                    FloatingBottomNavigation {
                                        navigationItems.forEachIndexed { _, item ->
                                            FloatingNavigationBarItem(selected = currentDestination?.route == item.route,
                                                onClick = {
                                                    if (item.route != currentDestination?.route) navController.navigate(
                                                        item.route
                                                    )
                                                },
                                                icon = {
                                                    Icon(
                                                        item.icon,
                                                        contentDescription = item.label,
                                                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                                                    )
                                                }) {
                                                Text(
                                                    text = item.label,
                                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                                )
                                            }
                                        }
                                    }
                                }
                            } else {
                                AnimatedVisibility(!state.hideRail) {
                                    NavigationRail {
                                        Column(
                                            modifier = Modifier.fillMaxHeight(),
//                                        verticalArrangement = Arrangement.spacedBy(
//                                            12.dp, Alignment.Top
//                                        )
                                        ) {
                                            navigationItems.forEachIndexed { _, item ->
                                                NavigationRailItem(selected = currentDestination?.route == item.route,
                                                    onClick = {
                                                        if (item.route != currentDestination?.route) navController.navigate(
                                                            item.route
                                                        )
                                                    },
                                                    icon = {
                                                        Icon(
                                                            item.icon,
                                                            contentDescription = item.label,
                                                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                                                        )
                                                    },
                                                    label = {
                                                        Text(
                                                            text = item.label,
                                                            color = MaterialTheme.colorScheme.onSecondaryContainer
                                                        )
                                                    })
                                            }
                                            NavigationRailItem(selected = currentDestination?.route == Screen.TimetableBuilder.route,
                                                onClick = {
                                                    if (Screen.TimetableBuilder.route != currentDestination?.route) navController.navigate(
                                                        Screen.TimetableBuilder.route
                                                    )
                                                },
                                                icon = {
                                                    Icon(
                                                        Icons.Outlined.TableChart,
                                                        contentDescription = "Builder",
                                                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                                                    )
                                                },
                                                label = {
                                                    Text(
                                                        text = "Builder",
                                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                                    )
                                                })

                                            Row(
                                                modifier = Modifier
                                                    .weight(20f)
                                                    .fillMaxHeight(),
                                                verticalAlignment = Alignment.Bottom
                                            ) {
                                                AnimatedVisibility(
                                                    visible = currentDestination?.route == Screen.TimetableBuilder.route,
                                                    enter = slideInHorizontally { width -> -width } + fadeIn(),
                                                    exit = slideOutHorizontally { width -> -width } + fadeOut()
                                                ) {
                                                    NavigationRailItem(selected = state.showMenu,

                                                        onClick = {
                                                            state.showMenu = !state.showMenu
                                                        },
                                                        icon = {
                                                            Icon(
                                                                Icons.Outlined.Menu,
                                                                contentDescription = "Menu",
                                                                tint = MaterialTheme.colorScheme.onSecondaryContainer
                                                            )
                                                        },
                                                        label = {
                                                            Text(
                                                                text = "Menu",
                                                                color = MaterialTheme.colorScheme.onSecondaryContainer
                                                            )
                                                        })
                                                }

                                            }
                                        }
                                    }
                                }

                            }

                        }) { _ ->
                            CompositionLocalProvider(LocalRailStatus provides state) {
                                Column(Modifier.padding(start = if (showNavigationRail) 80.dp else 0.dp)) {
                                    Navigation(navController = navController, modifier = Modifier)
                                }
                            }
                        }
                    }
                }
            }

        }
    }

    @Composable
    fun Navigation(navController: NavHostController, modifier: Modifier) {
        NavHost(
            navController = navController,
            startDestination = Screen.AuthNavGraph.route,
            modifier = modifier
        ) {
            navigation(
                startDestination = Screen.Login.route, route = Screen.AuthNavGraph.route
            ) {
                composable(Screen.Login.route) {
                    LoginScreen(navController = navController)
                }
            }
            navigation(
                startDestination = Screen.TimetableBuilder.route, route = Screen.MainNavGraph.route
            ) {
                composable(Screen.Home.route, enterTransition = { fadeIn(tween(0)) }) {
                    val viewModel = it.sharedViewModel<HomeScreenViewModel>(navController)
                    HomeScreen(navController = navController, viewModel = viewModel)
                }
                composable(Screen.Timetable.route, enterTransition = { fadeIn(tween(0)) }) {
                    val viewModel = it.sharedViewModel<TimetableViewModel>(navController)
                    Tts(navController = navController, viewModel = viewModel)
                }
                composable(Screen.Attendance.route, enterTransition = { fadeIn(tween(0)) }) {
                    val viewModel = it.sharedViewModel<AttendanceViewModel>(navController)
                    AttendanceView(
                        navController = navController, viewModel = viewModel
                    )
                }
                composable(Screen.Profile.route, enterTransition = { fadeIn(tween(0)) }) {
                    val viewModel = it.sharedViewModel<ProfileScreenViewModel>(navController)
                    ProfileScreen(navController = navController, viewModel = viewModel)
                }
                composable(Screen.TimetableBuilder.route, enterTransition = { fadeIn(tween(0)) }) {
                    TimetableBuilderScreen(navController = navController)
                }
            }
            navigation(
                startDestination = Screen.SettingsMain.route, route = Screen.SettingsNavGraph.route
            ) {
                composable(Screen.SettingsMain.route) {
                    val viewModel = it.sharedViewModel<SettingScreenViewModel>(navController)
                    SettingsScreen(navController = navController, viewModel = viewModel)
                }
//                composable(Screen.SettingsNotification.route) {
//                    NotificationSettingScreen(navController = navController)
//                }
            }
        }
    }
}

fun bottomNavigationItems(): List<BottomNavigationItem> {
    return listOf(
        BottomNavigationItem(
            label = "Home", icon = Icons.Outlined.Home, route = Screen.Home.route
        ),
        BottomNavigationItem(
            label = "Timetable", icon = Icons.Outlined.TableChart, route = Screen.Timetable.route
        ),
        BottomNavigationItem(
            label = "Attendance", icon = Icons.Outlined.Badge, route = Screen.Attendance.route
        ),
        BottomNavigationItem(
            label = "Profile", icon = Icons.Outlined.AccountCircle, route = Screen.Profile.route
        ),
    )
}


internal val LocalRailStatus = compositionLocalOf { RailStatus() }

internal class RailStatus {
    var hideRail: Boolean by mutableStateOf(false)
    var showMenu: Boolean by mutableStateOf(false)
}

@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.sharedViewModel(navController: NavController): T {
    val navGraphRoute = destination.parent?.route ?: return hiltViewModel()
    val parentEntry = remember(this) {
        navController.getBackStackEntry(navGraphRoute)
    }

    return hiltViewModel(parentEntry)
}