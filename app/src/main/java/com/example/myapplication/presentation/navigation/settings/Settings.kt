package com.example.myapplication.presentation.navigation.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.domain.model.AppPreferences
import com.example.myapplication.presentation.LoginActivity
import com.example.myapplication.presentation.deleteCache
import com.example.myapplication.presentation.navigation.main.noRippleClickable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch


@Composable
fun SettingsMain(viewModel: SettingsViewModel, killMe: () -> Unit = {}, onBackClick: () -> Unit) {
    val params = viewModel.getAppPreference().data.collectAsState(
        initial = AppPreferences(
            false,
            false,
            false
        )
    ).value
    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        val context = LocalContext.current
        Column {

            Row(
                Modifier
                    .height(50.dp)
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 10.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.clickable { onBackClick() })
                Text(
                    text = "Settings",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.size(24.dp))
            }
            Divider()

            SettingSection(title = "Join us on Discord", onClick = {
                context.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://discord.gg/b86VhhCwKy")
                    )
                )
            }) {}

            SettingSection(title = "Settings") {
                SettingItem {
                    Text(text = "App Preferences")
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = "Show More"
                    )
                }
                ExpandableSettingItem({
                    Row(Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "Notification Settings")
                        Icon(
                            imageVector = if(it) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowRight,
                            contentDescription = "Show More"
                        )
                    }
                }
                ) {
                    SettingItem {
                        Text(text = "Show Timetable tracker")
                        Switch(
                            checked = params.showNotifications, onCheckedChange = {
                                CoroutineScope(IO).launch {
                                    viewModel.setNotificationSetting(it)
                                }
                            }, modifier = Modifier
                                .scale(.7f)
                                .height(22.dp)
                        )
                    }
                }
                SettingItem {
                    Text(text = "Show download option")
                    Switch(
                        checked = params.showDownloadOption, onCheckedChange = {
                            CoroutineScope(IO).launch {
                                viewModel.setDownloadSetting(
                                    it
                                )
                            }
                        }, modifier = Modifier
                            .scale(.7f)
                            .height(22.dp)
                    )
                }
                SettingItem(onClick = { deleteCache(context = context) }) {
                    Text(text = "Clear local cache")
                }
                SettingItem(onClick = {
                    CoroutineScope(IO).launch {
                        viewModel.logOut()
                    }
                    context.startActivity(Intent(context, LoginActivity::class.java))
                    killMe()
                }) {
                    Text(text = "Logout")
                }
            }
            SettingSection(title = "About") {
                SettingItem {
                    Text(text = "version : 0.72.2 beta")
                }
                SettingItem {
                    Text(text = "Thanks for using the app")
                }
                SettingItem(onClick = {
                    context.startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://github.com/Imman-coder/Trem")
                        )
                    )
                }) {
                    Text(text = "This project is open to be contributed! Feel free to checkout this github repository")
                }
            }
        }
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Made with Love ❤ ️")
        }
    }
}

@Composable
fun SettingSection(title: String, onClick: () -> Unit = {}, content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .noRippleClickable { onClick() }
            .padding(start = 12.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.W500
        )
        Column(Modifier.padding(start = 0.dp, bottom = 15.dp)) { content() }
    }
}

@Composable
fun SettingItem(onClick: () -> Unit = {}, content: @Composable () -> Unit) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .noRippleClickable { onClick() }
        .padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        content()
    }
}

@Composable
fun ExpandableSettingItem(
    content: @Composable (Boolean) -> Unit,
    expandableContent: @Composable () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Column(modifier = Modifier
        .fillMaxWidth()
        .noRippleClickable { expanded = !expanded }
        .padding(12.dp)) {
        content(expanded)
        if (expanded)
            expandableContent()
    }

}


@Preview
@Composable
fun PSM() {
    Surface(Modifier.fillMaxSize()) {
        MaterialTheme {
//            SettingsMain(){}

        }
    }
}