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
import androidx.compose.animation.slideOutHorizontally
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
import androidx.compose.material.icons.automirrored.outlined.Redo
import androidx.compose.material.icons.automirrored.outlined.Undo
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.immanlv.trem.network.repository.getTimetable
import com.immanlv.trem.presentation.screens.attendance.AttendanceView
import com.immanlv.trem.presentation.screens.attendance.AttendanceViewModel
import com.immanlv.trem.presentation.screens.home.HomeScreen
import com.immanlv.trem.presentation.screens.home.HomeScreenViewModel
import com.immanlv.trem.presentation.screens.login.LoginScreen
import com.immanlv.trem.presentation.screens.login.LoginViewModel
import com.immanlv.trem.presentation.screens.profile.ProfileScreen
import com.immanlv.trem.presentation.screens.profile.ProfileScreenViewModel
import com.immanlv.trem.presentation.screens.settings.SettingScreenViewModel
import com.immanlv.trem.presentation.screens.settings.SettingsScreen
import com.immanlv.trem.presentation.screens.timetable.TimetableViewModel
import com.immanlv.trem.presentation.screens.timetable.Tts
import com.immanlv.trem.presentation.screens.timetableBuilder.TimetableBuilderScreen
import com.immanlv.trem.presentation.screens.timetableBuilder.TimetableBuilderViewModel
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
            var useDynamicColor by remember { mutableStateOf(false) }

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

            useDynamicColor = viewModel.appPreference.value.dynamicColor

            Box(
                Modifier
            ) {
                TremTheme(darkTheme = sysDarkTheme, dynamicColor = useDynamicColor) {
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
                            if (loggedIn) {
                                scope.launch {
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
                                                    )
                                                }) {
                                                Text(
                                                    text = item.label,
                                                )
                                            }
                                        }
                                    }
                                }
                            } else {
                                AnimatedVisibility(!state.hideRail) {
                                    NavRail(navController = navController)
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
                    val viewModel = it.sharedViewModel<LoginViewModel>(navController)
                    LoginScreen(
                        credentials = viewModel.credential,
                        eventFlow = viewModel.eventFlow,
                        onEvent = viewModel::onEvent
                    )
                }
            }
            navigation(
                startDestination = Screen.Home.route, route = Screen.MainNavGraph.route
            ) {
                composable(Screen.Home.route, enterTransition = { fadeIn(tween(0)) }) {
                    val viewModel = it.sharedViewModel<HomeScreenViewModel>(navController)
                    HomeScreen(
                        profile = viewModel.profile.value,
                        timetable = viewModel.timetable.value
                    )
                }
                composable(Screen.Timetable.route, enterTransition = { fadeIn(tween(0)) }) {
                    val viewModel = it.sharedViewModel<TimetableViewModel>(navController)
                    Tts(
                        timetable = viewModel.timetable.value,
                        timetableState = viewModel.timetableState.value,
                        onEvent = viewModel::onEvent
                    )
                }
                composable(Screen.Attendance.route, enterTransition = { fadeIn(tween(0)) }) {
                    val viewModel = it.sharedViewModel<AttendanceViewModel>(navController)
                    AttendanceView(
                        attendance = viewModel.attendance.value,
                        attendanceState = viewModel.attendanceState.value,
                        onEvent = viewModel::onEvent
                    )
                }
                composable(Screen.Profile.route, enterTransition = { fadeIn(tween(0)) }) {
                    val viewModel = it.sharedViewModel<ProfileScreenViewModel>(navController)
                    ProfileScreen(
                        profile = viewModel.profile.value,
                        scorecard = viewModel.scorecard.value,
                        openSettingsPage = { navController.navigate(Screen.SettingsMain.route) },
                        onEvent = viewModel::onEvent
                    )
                }

                /*
                * Experimental Feature only available for Large Screen devices.
                * Used for making timetable inside the app.
                */
                composable(Screen.TimetableBuilder.route, enterTransition = { fadeIn(tween(0)) }) {
                    val viewModel = it.sharedViewModel<TimetableBuilderViewModel>(navController)
                    TimetableBuilderScreen(
                        timetable = viewModel.timetable.value,
                        colorTable = viewModel.colorTable.value,
                        undoRedoStack = viewModel.undoRedoStack.value,
                        getTimetableAsBytes = viewModel::getTimetableAsBytes,
                        onEvent = viewModel::onEvent,
                    )
                }
            }
            navigation(
                startDestination = Screen.SettingsMain.route, route = Screen.SettingsNavGraph.route
            ) {
                composable(Screen.SettingsMain.route) {
                    val viewModel = it.sharedViewModel<SettingScreenViewModel>(navController)
                    SettingsScreen(
                        profile = viewModel.profile.value,
                        preference = viewModel.appPreference.value,
                        closeSettings = { navController.popBackStack() },
                        onEvent = viewModel::onEvent
                    )
                }
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

@Composable
fun NavRail(navController: NavController) {

    val state = LocalRailStatus.current

    val navigationItems = bottomNavigationItems()
    val currentBackstack = navController.currentBackStackEntryAsState()
    val currentDestination = currentBackstack.value?.destination

    NavigationRail {
        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(
                12.dp, Alignment.Top
            )
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
                        )
                    },
                    label = {
                        Text(
                            text = item.label,
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
                    )
                },
                label = {
                    Text(
                        text = "Builder",
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
                    Column(
                        verticalArrangement = Arrangement.spacedBy(
                            12.dp, Alignment.Top
                        )
                    ) {

                        NavigationRailItem(selected = false,
                            enabled = state.undoMenu.enabled,
                            onClick = {
                                state.undoMenu.onClick()
                            },
                            icon = {
                                Icon(
                                    Icons.AutoMirrored.Outlined.Undo,
                                    contentDescription = "Undo",
                                )
                            },
                            label = {
                                Text(
                                    text = "Undo",
                                )
                            })
                        NavigationRailItem(selected = false,
                            enabled = state.redoMenu.enabled,
                            onClick = {
                                state.redoMenu.onClick()
                            },
                            icon = {
                                Icon(
                                    Icons.AutoMirrored.Outlined.Redo,
                                    contentDescription = "Redo",
                                )
                            },
                            label = {
                                Text(
                                    text = "Redo",
                                )
                            })

                        NavigationRailItem(selected = state.showMenu,

                            onClick = {
                                state.showMenu = !state.showMenu
                            },
                            icon = {
                                Icon(
                                    Icons.Outlined.Menu,
                                    contentDescription = "Menu",
                                )
                            },
                            label = {
                                Text(
                                    text = "Menu",
                                )
                            })
                    }
                }
            }
        }
    }
}


internal val LocalRailStatus = compositionLocalOf { RailStatus() }

internal class RailStatus {
    var hideRail: Boolean by mutableStateOf(false)
    var showMenu: Boolean by mutableStateOf(false)
    var undoMenu: ItemState by mutableStateOf(ItemState())
    var redoMenu: ItemState by mutableStateOf(ItemState())
}

data class ItemState(
    val enabled: Boolean = false,
    val onClick: () -> Unit = {}
)

@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.sharedViewModel(navController: NavController): T {
    val navGraphRoute = destination.parent?.route ?: return hiltViewModel()
    val parentEntry = remember(this) {
        navController.getBackStackEntry(navGraphRoute)
    }

    return hiltViewModel(parentEntry)
}