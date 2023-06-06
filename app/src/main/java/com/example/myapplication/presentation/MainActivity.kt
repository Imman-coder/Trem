package com.example.myapplication.presentation

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.domain.model.AppPreferences
import com.example.myapplication.network.exceptions.FetchException
import com.example.myapplication.network.exceptions.LoginException
import com.example.myapplication.presentation.components.NavBarItem
import com.example.myapplication.presentation.components.notification.NotificationContentBuilder
import com.example.myapplication.presentation.components.notification.TimetableNotificationService
import com.example.myapplication.presentation.navigation.main.HomeScreen
import com.example.myapplication.presentation.navigation.main.MainViewModel
import com.example.myapplication.presentation.navigation.main.ProfileScreen
import com.example.myapplication.presentation.navigation.main.Tts
import com.example.myapplication.presentation.navigation.settings.SettingsMain
import com.example.myapplication.presentation.navigation.settings.SettingsViewModel
import com.example.myapplication.presentation.ui.theme.MyApplicationTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@OptIn(ExperimentalFoundationApi::class)
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val TAG = generateLogTag(this::class.java.simpleName)

    private val settingsViewModel: SettingsViewModel by viewModels()
    private val viewModel: MainViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Main()

                    val notificationService = TimetableNotificationService(applicationContext)
                    val settings = settingsViewModel.getAppPreference().data.collectAsState(
                        initial = AppPreferences(
                            false,
                            false,
                            false
                        )
                    ).value

                    val timetableState = viewModel.timetableState.collectAsState().value
                    val isLoggedIn by viewModel.hasCredentials.collectAsState()



                    if (!isLoggedIn) {
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    }

                    fun updateNotification() {
                        if (timetableState.data != null) {
                            val w = NotificationContentBuilder.buildContent(timetableState.data)
                            notificationService.showNotification(w)
                        }

                    }

                    DisposableEffect(key1 = settings) {
                        if (settings.showNotifications) {
                            when {
                                ContextCompat.checkSelfPermission(
                                    this@MainActivity,
                                    Manifest.permission.POST_NOTIFICATIONS
                                ) ==
                                        PackageManager.PERMISSION_GRANTED -> {
                                    Log.e("TAG", "User accepted the notifications!")
                                    updateNotification()
                                }

                                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {

                                }

                                else -> {
                                    ActivityCompat.requestPermissions(
                                        this@MainActivity,
                                        arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                                        1
                                    )
                                }
                            }
                        }
                        onDispose {
                            if (settings.showNotifications)
                                notificationService.unregisterAManager()
                        }
                    }

                    LaunchedEffect(Unit) {
                        if (isOnline(this@MainActivity)) {

//                            mainVM.login()
//                            mainVM.fetchTimetable()
//                            mainVM.fetchResult()
//                            mainVM.fetchAttendance()

                        } else {
                            Toast.makeText(this@MainActivity, "Offline", Toast.LENGTH_SHORT).show()
                        }
                    }

                }
            }
        }
    }


    @Composable
    fun Main() {
        var openSettings by remember { mutableStateOf(false) }
        val navController = rememberNavController()

        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Navigation(navController = navController)

            if (openSettings) {
                SettingsMain(settingsViewModel, { finish() }) { openSettings = false }
            }
        }
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun Navigation(navController: NavHostController) {
        val cs = rememberCoroutineScope()
        val pagerState = rememberPagerState()
        val backStackEntry = navController.currentBackStackEntryAsState()

        val profileState by viewModel.profileState.collectAsState()
        val scorecardState by viewModel.scorecardState.collectAsState()
        val timetableState by viewModel.timetableState.collectAsState()
        val attendanceState by viewModel.attendanceState.collectAsState()

        var isAttendanceLoading by remember { mutableStateOf(false) }
        var isTimetableLoading by remember { mutableStateOf(false) }
        var isScorecardLoading by remember { mutableStateOf(false) }


        val attendancePullRefreshState = rememberPullRefreshState(
            refreshing = isAttendanceLoading,
            onRefresh = {
                CoroutineScope(Dispatchers.IO).launch {
                    viewModel.fetchAttendance()
                    showToast("Attendance Refreshed!")
                }
            }
        )

        val timetablePullRefreshState = rememberPullRefreshState(
            refreshing = isTimetableLoading,
            onRefresh = {
                CoroutineScope(Dispatchers.IO).launch {
                    viewModel.fetchTimetable()
                    showToast("Timetable Refreshed!")
                }

            }
        )

        val scorecardPullRefreshState = rememberPullRefreshState(
            refreshing = isScorecardLoading,
            onRefresh = {
                CoroutineScope(Dispatchers.IO).launch {
                    viewModel.fetchResult()
                    showToast("Result Refreshed!")
                }
            }
        )

        LaunchedEffect(
            key1 = attendanceState,
            block = {
                isAttendanceLoading =
                    attendanceState.dataState == MainViewModel.DataState.DataState.Fetching
            })

        LaunchedEffect(
            key1 = timetableState,
            block = {
                isTimetableLoading =
                    timetableState.dataState == MainViewModel.DataState.DataState.Fetching
            })

        LaunchedEffect(
            key1 = scorecardState,
            block = {
                isScorecardLoading =
                    scorecardState.dataState == MainViewModel.DataState.DataState.Fetching
            })



        NoScrollEffect {
            HorizontalPager(
                pageCount = 2,
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                beyondBoundsPageCount = 1,
                userScrollEnabled = (backStackEntry.value?.destination?.route == "Home")
            ) {
                when (it) {
                    0 -> {
                        Column(
                            Modifier
                                .fillMaxSize()
                        ) {
                            NavHost(
                                navController = navController,
                                startDestination = "Home",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight()
                                    .weight(1f, fill = true)
                            ) {
                                composable("Home") {
                                    val backDispatcher =
                                        LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
                                    DisposableEffect(key1 = Unit) {
                                        val callback = object :
                                            OnBackPressedCallback(enabled = pagerState.currentPage == 1) {
                                            override fun handleOnBackPressed() {
                                                cs.launch { pagerState.animateScrollToPage(0) }
                                            }
                                        }

                                        backDispatcher?.addCallback(callback)
                                        onDispose {
                                            callback.remove()
                                        }
                                    }
                                    Box(Modifier.pullRefresh(attendancePullRefreshState)) {
                                        HomeScreen(attendanceState, profileState)
                                        PullRefreshIndicator(
                                            isAttendanceLoading,
                                            attendancePullRefreshState,
                                            Modifier.align(Alignment.TopCenter)
                                        )
                                    }


                                }


                                composable("Profile") {
                                    Box(Modifier.pullRefresh(scorecardPullRefreshState)) {
                                        ProfileScreen(
                                            profileState.data,
                                            scorecard = scorecardState.data
                                        ) {
                                            startActivity(
                                                Intent(
                                                    this@MainActivity,
                                                    SettingsActivity::class.java
                                                )
                                            )
                                        }
                                        PullRefreshIndicator(
                                            isScorecardLoading,
                                            scorecardPullRefreshState,
                                            Modifier.align(Alignment.TopCenter)
                                        )
                                    }

                                }
                            }

                            BottomNavBar(
                                items = listOf(
                                    NavBarItem("Home", Icons.Default.Home),
                                    NavBarItem("Profile", Icons.Default.Person)
                                ),
                                modifier = Modifier
                                    .fillMaxWidth(),
                                navController = navController,
                                onItemClick = { item ->
                                    navController.navigate(item.route)
                                }
                            )
                        }

                    }

                    1 -> {
                        Box(modifier = Modifier.pullRefresh(timetablePullRefreshState)) {
                            Tts(
                                timetableState
                            ) {
                                cs.launch { pagerState.animateScrollToPage(0) }
                            }
                            PullRefreshIndicator(
                                isTimetableLoading,
                                timetablePullRefreshState,
                                Modifier.align(Alignment.TopCenter)
                            )
                        }
                    }
                }
            }


        }


    }


    @Composable
    fun BottomNavBar(
        items: List<NavBarItem>,
        navController: NavController,
        modifier: Modifier = Modifier,
        onItemClick: (NavBarItem) -> Unit
    ) {
        val backStackEntry = navController.currentBackStackEntryAsState()

        Row(
            modifier = modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(MaterialTheme.colorScheme.primaryContainer),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,

            ) {
            items.forEach {
                val isSelected = it.route == backStackEntry.value?.destination?.route
                val bg =
                    if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.primaryContainer
                val tbg =
                    if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.inversePrimary
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(bg.copy(alpha = .2f))
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                        .clickable { onItemClick(it) }
                        .animateContentSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(it.icon, it.route, tint = tbg)
                    if (isSelected)
                        Text(
                            text = it.route,
                            color = tbg,
                            style = MaterialTheme.typography.labelSmall
                        )
                }

            }

        }

    }

    private fun showToast(s: String) {
        Looper.prepare()
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show()
        Looper.loop()
    }

    @Preview
    @Composable
    fun Preview() {
        MyApplicationTheme {
            Surface {
            }
        }
    }
}