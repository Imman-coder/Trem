package com.example.myapplication.presentation

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.myapplication.domain.model.Credentials
import com.example.myapplication.presentation.navigation.main.Tts
import com.example.myapplication.presentation.navigation.main.MainViewModel
import com.example.myapplication.presentation.navigation.settings.SettingsMain
import com.example.myapplication.presentation.navigation.settings.SettingsViewModel
import com.example.myapplication.presentation.ui.theme.MyApplicationTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FakeLoggedInActivity : AppCompatActivity() {

    private val model: MainViewModel by viewModels()
    private val settingsViewModel: SettingsViewModel by viewModels()


    @RequiresApi(Build.VERSION_CODES.O)
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
                        if (isOnline(this@FakeLoggedInActivity)) {
                            if (k.uid != "nothing") {
                                try {
                                    model.updateTimetable()
                                    Log.d("MainScreen", "Timetable updated")
                                } catch (e: Exception) {
                                    Toast.makeText(
                                        this@FakeLoggedInActivity,
                                        "failed to get Timetable",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                        } else {
                            Toast.makeText(this@FakeLoggedInActivity, "Offline", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun Main() {
        var openSettings by remember { mutableStateOf(false) }


        Box(modifier  = Modifier
            .fillMaxSize()){

            Tts(model = model,true){openSettings=true}
            if(openSettings){
                SettingsMain(settingsViewModel,{finish()}){openSettings=false}
            }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if(openSettings) openSettings=false
                else {
                    finish()
                }
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
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