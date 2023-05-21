package com.example.myapplication.presentation

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.domain.model.Attendance
import com.example.myapplication.domain.model.Profile
import com.example.myapplication.domain.model.Result
import com.example.myapplication.presentation.navigation.main.Dashboard
import com.example.myapplication.presentation.navigation.main.HomeScreen
import com.example.myapplication.presentation.navigation.main.NavBarItem
import com.example.myapplication.presentation.navigation.main.ProfileScreen
import com.example.myapplication.presentation.ui.theme.MyApplicationTheme
import com.example.myapplication.repository.ProfileRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var profileRepository: ProfileRepository

    @Inject
    lateinit var profileStore: DataStore<Profile>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme(dynamicColor = false) {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val profile = profileStore.data.collectAsState(initial = Profile()).value
                    LaunchedEffect(key1 = Unit) {
                        CoroutineScope(IO).launch {
                            if(isInternetAvailable()) {
                                setAttendance(profileRepository.getAttendance(4))
                                setResult(profileRepository.getResults())
                            }
                            else{
                                Looper.prepare()
                                Toast.makeText(this@MainActivity,"offline",Toast.LENGTH_SHORT).show()
                                Looper.loop()
                            }
                        }
                    }
                    LaunchedEffect(key1 = profile) {
                        Log.d("TAG", profile.toString())
                    }


                    Main()

                }
            }
        }
    }

    suspend fun setProfile(profile: Profile) {
        profileStore.updateData {
            it.copy(
                name = profile.name,
                redgno = profile.redgno,
                phoneno = profile.phoneno,
                sem = profile.sem,
                program = profile.program,
            )
        }
    }

    suspend fun setAttendance(attendance: Attendance) {

        profileStore.updateData {
            it.copy(
                attendance = attendance.subs
            )
        }
    }

    suspend fun setResult(result: Result) {
        profileStore.updateData {
            it.copy(
                result = result
            )
        }
    }

    @Composable
    fun Navigation(navController: NavHostController) {
        NavHost(navController = navController, startDestination = "home") {
            composable("home") {
                HomeScreen(profileStore)
            }
            composable("table") {
                Text(text = "Table")
            }
            composable("setting") {
                ProfileScreen(profileStore)
            }

        }
    }

    @Composable
    fun BottomNavBar(
        items: List<NavBarItem>,
        navController: NavController,
        onItemClick: (NavBarItem) -> Unit
    ) {
        val backStackEntry = navController.currentBackStackEntryAsState()
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .fillMaxWidth()
                .padding(vertical = 22.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(top = 12.dp, bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,

            ) {
            items.forEach {
                val selected = it.route == (backStackEntry.value?.destination?.route ?: "")
                NavigationBarItem(selected = false, onClick = { onItemClick(it) }, icon = {

                    val color =
                        if (selected) MaterialTheme.colorScheme.onPrimaryContainer
                        else MaterialTheme.colorScheme.inversePrimary
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {

                        Icon(it.icon, it.name, tint = color)
                        Text(text = it.name, color = color)
                    }

                }, Modifier.height(50.dp))

            }

        }

    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun Main() {
        val navController = rememberNavController()
        Scaffold(
            bottomBar = {
                BottomNavBar(
                    items = listOf(
                        NavBarItem("Home", "home", Icons.Default.Home),
                        NavBarItem("Timetable", "table", Icons.Default.Edit),
                        NavBarItem("Setting", "setting", Icons.Default.Settings),
                    ),
                    navController = navController,
                    onItemClick = {
                        if (it.route != navController.currentDestination?.route)
                            navController.navigate(it.route)
                    }
                )
            }
        ) {
            Navigation(navController = navController)


        }

    }


    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        MyApplicationTheme(darkTheme = true) {
            Main()
        }
    }
}

