package com.immanlv.trem.presentation.screens.settings

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AdminPanelSettings
import androidx.compose.material.icons.rounded.Air
import androidx.compose.material.icons.rounded.DisplaySettings
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.immanlv.trem.domain.model.ColorMode
import com.immanlv.trem.domain.util.DataErrorType
import com.immanlv.trem.network.util.ImageUtils
import com.immanlv.trem.presentation.screens.login.util.noRippleClickable
import com.immanlv.trem.presentation.screens.settings.components.SettingGroup
import com.immanlv.trem.presentation.screens.settings.components.SettingItemClickable
import com.immanlv.trem.presentation.screens.settings.components.SettingItemClickablePreview
import com.immanlv.trem.presentation.screens.settings.components.SettingItemSelectable
import com.immanlv.trem.presentation.screens.settings.components.SettingItemToggleable
import com.immanlv.trem.presentation.theme.TremTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingScreenViewModel = hiltViewModel()
) {

    var clickedTimesDeveloper by remember { mutableIntStateOf(0) }
    var initDeveloperClicks by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val preference = viewModel.appPreference.value
    val profile = viewModel.profile.value

    var settingModalConfig by remember { mutableStateOf<SettingsModal?>(null) }

    if (clickedTimesDeveloper > 9) {
        Toast.makeText(LocalContext.current, "Developer Mode Enabled", Toast.LENGTH_SHORT).show()
        viewModel.onEvent(SettingScreenEvent.SetPreference(preference.copy(developerMode = true)))
        initDeveloperClicks = false
        clickedTimesDeveloper = 0
    }

    val color = MaterialTheme.colorScheme.onSecondaryContainer
    Surface(
        modifier = Modifier.background(MaterialTheme.colorScheme.secondaryContainer),
        contentColor = color
    ) {
        if (settingModalConfig != null) {
            BasicAlertDialog(onDismissRequest = {
                settingModalConfig!!.onDismiss(); settingModalConfig = null;
            }) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth(.9f)
                        .wrapContentHeight(),
                    shape = MaterialTheme.shapes.large
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {

                        var v by remember {
                            mutableStateOf(settingModalConfig!!.initValue)
                        }

                        Text(text = settingModalConfig!!.title)
                        OutlinedTextField(value = v, onValueChange = { v = it }, maxLines = 7)
                        Button(onClick = {
                            settingModalConfig!!.onSuccess(v)
                            settingModalConfig = null
                        }) {
                            Text(text = "Save")
                        }
                    }
                }
            }
        }

        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Text(text = "Done", Modifier.noRippleClickable { navController.popBackStack() })
                }
                Text(
                    text = "Setting",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.noRippleClickable {
                        if (!initDeveloperClicks) {
                            coroutineScope.launch {
                                initDeveloperClicks = true
                                delay(5000)
                                Log.d("TAG", "SettingsScreen: Cleared $clickedTimesDeveloper")
                                clickedTimesDeveloper = 0
                                initDeveloperClicks = false
                            }
                        }
                        clickedTimesDeveloper++
                        Log.d("TAG", "SettingsScreen: $clickedTimesDeveloper")
                    }
                )
            }
            HorizontalDivider()
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (profile.propicError == DataErrorType.NoDataFound || profile.propic.isNullOrBlank())
                        Column(
                            Modifier
                                .clip(CircleShape)
                                .size(100.dp)
                                .background(color),
                        ) {}
                    else
                        Image(
                            bitmap = ImageUtils.base64ToBitmap(profile.propic).asImageBitmap(),
                            contentDescription = null
                        )
                    Column(Modifier.padding(start = 12.dp)) {
                        Text(text = profile.name)
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "${profile.regdno}")
                            Text(
                                text = "Logout",
                                modifier = Modifier.noRippleClickable {
                                    viewModel.onEvent(
                                        SettingScreenEvent.Logout
                                    )
                                },
                                color = MaterialTheme.colorScheme.tertiary
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                SettingGroup(title = "General", icon = {
                    Icon(
                        imageVector = Icons.Rounded.Air,
                        contentDescription = ""
                    )
                }) {
                    var enabled by remember { mutableStateOf(false) }
                    SettingItemToggleable("Location", enabled) { enabled = it }
                }
                SettingGroup(title = "Notification", icon = {
                    Icon(
                        imageVector = Icons.Rounded.Notifications,
                        contentDescription = ""
                    )
                }) {
                    SettingItemToggleable(
                        "Permission",
                        preference.showNotification
                    ) {
                        viewModel.onEvent(
                            SettingScreenEvent.SetPreference(
                                preference.copy(
                                    showNotification = !preference.showNotification
                                )
                            )
                        )
                    }
                    SettingItemClickable("Advance") {}
                }
                SettingGroup(title = "Display", icon = {
                    Icon(
                        imageVector = Icons.Rounded.DisplaySettings,
                        contentDescription = ""
                    )
                }) {
                    val s = listOf(ColorMode.Dark, ColorMode.Light, ColorMode.Unspecified)
                    val c = listOf("Dark", "Light", "Follow System")
                    SettingItemSelectable(
                        title = "Color Mode",
                        selected = c[s.indexOf(preference.colorMode)],
                        values = c
                    ) {
                        viewModel.onEvent(SettingScreenEvent.SetPreference(preference.copy(colorMode = s[it])))
                    }
                }
                if (preference.developerMode) {
                    SettingGroup(title = "Extras", icon = {
                        Icon(
                            imageVector = Icons.Rounded.AdminPanelSettings,
                            contentDescription = ""
                        )
                    }) {
                        SettingItemToggleable(title = "Developer Option", enabled = true) {
                            viewModel.onEvent(
                                SettingScreenEvent.SetPreference(
                                    preference.copy(
                                        developerMode = it
                                    )
                                )
                            )
                        }
                        SettingItemClickablePreview(
                            title = "Custom Timetable Fetcher",
                            value = preference.loadCustomTimetable
                        ) {
                            settingModalConfig = SettingsModal(
                                onDismiss = {},
                                onSuccess = {
                                    viewModel.onEvent(
                                        SettingScreenEvent.SetPreference(
                                            preference.copy(
                                                loadCustomTimetable = it
                                            )
                                        )
                                    )
                                },
                                title = "Custom Timetable Config",
                                initValue = preference.loadCustomTimetable
                            )
                        }
                        SettingItemToggleable(
                            title = "Auto Fetch Last Updated",
                            enabled = preference.autoFetchLastUpdated,
                            onToggle ={
                                viewModel.onEvent(
                                    SettingScreenEvent.SetPreference(
                                        preference.copy(
                                            autoFetchLastUpdated = it
                                        )
                                    )
                                )
                            }
                        )
                    }
                }
                SettingGroup(title = "Help", icon = {
                    Icon(
                        imageVector = Icons.Rounded.Info,
                        contentDescription = ""
                    )
                }, onClick = {}) {
                }
            }


        }
    }
}

data class SettingsModal(
    val onDismiss: () -> Unit,
    val onSuccess: (String) -> Unit,
    val title: String,
    val initValue: String,
)


@Preview
@Composable
fun SettingsScreenPreview() {
    TremTheme(darkTheme = true) {
        SettingsScreen(navController = rememberNavController())
    }

}