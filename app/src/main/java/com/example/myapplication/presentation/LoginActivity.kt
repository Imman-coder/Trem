package com.example.myapplication.presentation

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.datastore.core.DataStore
import com.example.myapplication.domain.model.Credentials
import com.example.myapplication.domain.model.Profile
import com.example.myapplication.network.exceptions.LoginException
import com.example.myapplication.presentation.navigation.login.LoginScreen
import com.example.myapplication.presentation.navigation.login.SplashScreen
import com.example.myapplication.presentation.ui.theme.MyApplicationTheme
import com.example.myapplication.repository.ProfileRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.InetAddress
import javax.inject.Inject


@AndroidEntryPoint
class LoginActivity : ComponentActivity() {

    @Inject
    lateinit var profileRepository: ProfileRepository

    @Inject
    lateinit var profileStore: DataStore<Profile>

    @Inject
    lateinit var credentialsStore: DataStore<Credentials>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MyApplicationTheme(dynamicColor = false) {
                Surface(
                    Modifier.fillMaxSize(),
                    color = Color.Black
                ) {
                    if(isOnline(this@LoginActivity)){
                        Main()
                    }
                    else{
                        LaunchedEffect(key1 = Unit){
                            val i = Intent(this@LoginActivity, MainActivity::class.java)
                            startActivity(i)
                            finish()
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun Main() {
        val creds = credentialsStore.data.collectAsState(initial = Credentials()).value
        val loginStatus = remember {
            mutableStateOf(true)
        }
        LaunchedEffect(key1 = creds) {
            if (!(creds.uid.isEmpty() || creds.pass.isEmpty()))
                loginStatus.value = creds.hasCredentials
        }
        if (loginStatus.value) {
            SplashScreen()
            if (!(creds.uid.isEmpty() || creds.pass.isEmpty())) {
                LaunchedEffect(key1 = Unit) {
                    CoroutineScope(IO).launch {
                        try {
                            setProfile(profileRepository.Login(creds.uid, creds.pass))
                            val i = Intent(this@LoginActivity, MainActivity::class.java)
                            startActivity(i)
                            finish()
                        } catch (e: LoginException) {
                            if(e.toastMessage=="No Intenet")
                                loginStatus.value = false
                        }

                    }
                }
            }
        } else {
            LoginScreen(onLoginClicked = { uid, pass, b ->
                CoroutineScope(IO).launch {
                    try {
                        makeToast("Logging in..")
                        setProfile(profileRepository.Login(uid, pass))
                        if (b) setCredentials(uid, pass)
                        makeToast("Logged In")
                        val i = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(i)
                        finish()
                    } catch (e: LoginException) {
                        makeToast(e.toastMessage)
                    }
                }
            }, credentialsStore)
        }
    }

    private suspend fun makeToast(message: String) {
        withContext(Dispatchers.Main) {
            Toast.makeText(this@LoginActivity, message, Toast.LENGTH_SHORT).show()
        }
    }

    @Composable
    @Preview
    fun Preview() {
        Main()
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

    suspend fun setCredentials(uid: String, pass: String) {
        credentialsStore.updateData {
            it.copy(
                true, uid, pass
            )
        }
    }





}
fun isInternetAvailable(): Boolean {
    return try {
        val ipAddr = InetAddress.getByName("www.google.com")
        !ipAddr.equals("")
    } catch (e: Exception) {
        false
    }
}

fun isOnline(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val capabilities =
        connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
    if (capabilities != null) {
        if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
            Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
            return true
        } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
            Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
            return true
        } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
            Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
            return true
        }
    }
    return false
}