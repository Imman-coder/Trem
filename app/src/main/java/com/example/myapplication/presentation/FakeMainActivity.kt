package com.example.myapplication.presentation

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.myapplication.presentation.navigation.main.FakeMainViewModel
import com.example.myapplication.presentation.navigation.main.MainViewModel
import com.example.myapplication.presentation.navigation.main.Tts
import com.example.myapplication.presentation.ui.theme.MyApplicationTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FakeMainActivity : AppCompatActivity() {

    private val model: FakeMainViewModel by viewModels()


    @OptIn(ExperimentalMaterialApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    val timetableState = model.timetableState.collectAsState().value
                    val isFakeLogged by model.hasCredentials.collectAsState()
                    var isLoading by remember { mutableStateOf(false) }

                    LaunchedEffect(key1 = timetableState) {
                        isLoading =
                            timetableState.dataState == MainViewModel.DataState.DataState.Fetching
                    }


                    val pullRefreshState = rememberPullRefreshState(
                        refreshing = isLoading,
                        onRefresh = {
                            CoroutineScope(Dispatchers.IO).launch {
                                model.fetchTimetable()
                            }
                        })

                    if (!isFakeLogged) {
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    }

                    Box(
                        modifier = Modifier
                            .pullRefresh(pullRefreshState)
                            .fillMaxSize()
                    ) {
                        Tts(timetableState,true) {
                            startActivity(
                                Intent(
                                    this@FakeMainActivity,
                                    SettingsActivity::class.java
                                )
                            )
                        }
                        PullRefreshIndicator(
                            isLoading,
                            pullRefreshState,
                            Modifier.align(Alignment.TopCenter)
                        )

                    }
                }
            }
        }
    }


    @Composable
    fun Main() {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
//            Tts(timetable = model.timetableState.value.data?: Timetable(),model.timetableState.value.data==null){
//                startActivity(Intent(this@FakeLoggedInActivity,SettingsActivity::class.java))
//            }
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