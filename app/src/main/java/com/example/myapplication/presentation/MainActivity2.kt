package com.example.myapplication.presentation

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.gestures.stopScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
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
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.example.myapplication.R
import com.example.myapplication.domain.model.Credentials
import com.example.myapplication.network.exceptions.LoginException
import com.example.myapplication.presentation.components.NavBarItem
import com.example.myapplication.presentation.navigation.main.Dashboard
import com.example.myapplication.presentation.navigation.main.Tts
import com.example.myapplication.presentation.navigation.main.mainViewModel
import com.example.myapplication.presentation.ui.theme.MyApplicationTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

@AndroidEntryPoint
class MainActivity2 : AppCompatActivity() {

    private val model: mainViewModel by viewModels()


    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme(
//                darkTheme = true,
//                dynamicColor = false
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Main()

                    val k = model.credentialStore.data.collectAsState(initial = Credentials(uid = "nothing")).value
                    LaunchedEffect(key1 = k) {
                        if (isOnline(this@MainActivity2)) {
                            if (k.uid != "nothing") {
                                try {
                                    model.updateProfile(k.uid, k.pass)
                                    Log.d("MainScreen", "Login succeed")
                                } catch (e: LoginException) {
                                    Toast.makeText(
                                        this@MainActivity2,
                                        e.toastMessage,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    e.printStackTrace()
                                    Log.d("MainScreen", "Login failed")
                                } catch (e: java.lang.Exception) {
                                    e.printStackTrace()
                                    Log.d("MainScreen", "Login failed")
                                }

                                try {
                                    Log.d("MainScreen", "Attendence..")
                                    model.updateAttendence()
                                    Log.d("MainScreen", "Attendence updated")
                                } catch (e: Exception) {
                                    Toast.makeText(
                                        this@MainActivity2,
                                        "failed to get Attendence",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                try {
                                    model.updateResult()
                                    Log.d("MainScreen", "Result updated")
                                } catch (e: Exception) {
                                    Toast.makeText(
                                        this@MainActivity2,
                                        "failed to get Result",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                try {
                                    model.updateTimetable()
                                    Log.d("MainScreen", "Timetable updated")
                                } catch (e: Exception) {
                                    Toast.makeText(
                                        this@MainActivity2,
                                        "failed to get Timetable",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                        } else {
                            Toast.makeText(this@MainActivity2, "Offline", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }


    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.commit()
    }


    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun Main() {
        var selected by remember { mutableStateOf("Home") }
        val pagerState = rememberPagerState()
        val k = rememberCoroutineScope()
        
        HorizontalPager(
            pageCount = if (selected=="Home") 2 else 1, state = pagerState, modifier = Modifier
                .fillMaxSize()

        ) { page ->
            when (page) {
                0 -> {
                    Column {
                        androidx.compose.ui.viewinterop.AndroidView(modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            ,
                            factory = { context ->
                                val view =
                                    android.view.LayoutInflater.from(context)
                                        .inflate(R.layout.activity_main, null, false)
                                replaceFragment(Dashboard())
                                view
                            },
                            update = { }
                        )
                        BottomNavBar(
                            items = listOf(
                                NavBarItem(
                                    "Home",
                                    Dashboard(),
                                    androidx.compose.material.icons.Icons.Outlined.Home
                                ),
                                NavBarItem(
                                    "Timetable",
                                    com.example.myapplication.presentation.navigation.main.TimetableScreen(),
                                    androidx.compose.material.icons.Icons.Outlined.DateRange
                                ),
                                NavBarItem(
                                    "Profile",
                                    com.example.myapplication.presentation.navigation.main.Profile(),
                                    androidx.compose.material.icons.Icons.Outlined.Person
                                ),
                            ),
                            selected = selected,
                            onItemClick = {
                                selected = it.name
                                replaceFragment(it.route)
                            }
                        )
                    }
                }
                1 -> {

                    Column(Modifier.fillMaxSize()) {
                        Tts(model)
                    }
                }
            }

        }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (pagerState.currentPage == 0) {
                    if (selected == "Home")
//                        finish()
                        exitProcess(-1)
                    else {
                        selected = "Home"
                        replaceFragment(Dashboard())
                    }
                } else {
                    k.launch {
                        pagerState.animateScrollToPage(0)
                    }
                }
            }
        })
    }


    @Composable
    fun BottomNavBar(
        items: List<NavBarItem>,
        selected: String,
        onItemClick: (NavBarItem) -> Unit
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(MaterialTheme.colorScheme.primaryContainer),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,

            ) {
            items.forEach {
                val isSelected = it.name == selected
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
                    Icon(it.icon, it.name, tint = tbg)
                    if (isSelected)
                        Text(
                            text = it.name,
                            color = tbg,
                            style = MaterialTheme.typography.labelSmall
                        )
                }

            }

        }

    }

    @Preview
    @Composable
    fun Preview() {
        MyApplicationTheme {
            Surface {
                Main()
            }
        }
    }

}