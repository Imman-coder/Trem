package com.immanlv.trem.presentation.screens.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.immanlv.trem.R
import com.immanlv.trem.domain.model.Grade
import com.immanlv.trem.domain.model.Profile
import com.immanlv.trem.domain.model.ResultSubject
import com.immanlv.trem.domain.model.Scorecard
import com.immanlv.trem.domain.model.Sem
import com.immanlv.trem.domain.util.DataErrorType
import com.immanlv.trem.network.util.ImageUtils
import com.immanlv.trem.presentation.screens.login.util.noRippleClickable
import com.immanlv.trem.presentation.theme.TremTheme
import com.immanlv.trem.presentation.util.Screen

@Composable
fun ProfileScreen(
    profile: Profile,
    scorecard: Scorecard,
    openSettingsPage: () -> Unit,
    onEvent:(ProfileScreenEvent)->Unit
) {
    Column(
        Modifier.verticalScroll(rememberScrollState())
    ) {
        ProfileCard(
            profile = profile,
            openSettingsPage = openSettingsPage,
            refreshProfilePicture = { onEvent(ProfileScreenEvent.RefreshProfile) })
        ScorecardTable(scorecard = scorecard)

    }

}

@Composable
fun ScorecardTable(scorecard: Scorecard) {
    var expandNum by remember {
        mutableIntStateOf(-1)
    }
    scorecard.sems.forEachIndexed { id, item ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Card {
                Row(
                    Modifier
                        .clickable { expandNum = if (id == expandNum) -1 else id }
                        .fillMaxWidth()
                        .padding(12.dp)
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = item.sem.toString())
                    Text(text = item.sgpa.toString())
                }
            }
            AnimatedVisibility(visible = expandNum == id) {
                Box {
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 12.dp)
                            .clip(RoundedCornerShape(bottomEnd = 12.dp, bottomStart = 12.dp))
                            .background(CardDefaults.cardColors().containerColor)
                    ) {
                        item.subjects.forEach {
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp)
                                    .padding(horizontal = 24.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = it.name)
                                Text(text = it.credit.toString())
                                Text(text = it.grade.toString())
                            }
                        }
                    }
                    Row(
                        Modifier
                            .padding(horizontal = 12.dp)
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        Color.Black.copy(alpha = .5f), Color.Transparent
                                    )
                                )
                            )
                            .height(12.dp)
                            .fillMaxWidth()
                    ) {}
                }
            }


        }

    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProfileCard(profile: Profile, openSettingsPage:() -> Unit = {}, refreshProfilePicture: () -> Unit = {}) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(0.dp, 0.dp, 32.dp, 32.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        Row(
            Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            Image(
                imageVector = Icons.Outlined.Settings,
                contentDescription = "Settings",
                modifier = Modifier.noRippleClickable { openSettingsPage() },
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground),
            )
        }
        Column(Modifier.padding(12.dp)) {
            Box(
                Modifier

                    .fillMaxWidth()
                    .combinedClickable(onClick = {}, onLongClick = { refreshProfilePicture() }),
                contentAlignment = Alignment.Center,
            ) {
                if (profile.propicError == DataErrorType.NoDataFound) {
                    Column(
                        Modifier
                            .clip(CircleShape)
                            .size(100.dp)
                            .background(MaterialTheme.colorScheme.background),
                    ) {}
                    Column(
                        Modifier
                            .clip(CircleShape)
                            .size(100.dp)
                            .background(MaterialTheme.colorScheme.onBackground.copy(.3f)),
                    ) {}
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painterResource(id = R.drawable.baseline_refresh_24),
                            "refresh",

                            )
                        Text(text = "Refresh")
                    }

                } else {
                    Image(bitmap = profile.propic?.let {
                        ImageUtils.base64ToBitmap(it).asImageBitmap()
                    } ?: ImageBitmap(256, 256),
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(100.dp)
                            .background(MaterialTheme.colorScheme.background))
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = profile.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(text = profile.regdno.toString())
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Program")
                    Text(text = profile.program)
                }
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Sem")
                    Text(text = profile.sem.toString())
                }
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Branch")
                    Text(text = profile.branch)

                }
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Section")
                    Text(text = profile.section.toString())

                }
            }
        }
    }
}


@Preview
@Composable
fun ProfileCardPreview() {
    TremTheme(darkTheme = true) {
        ProfileCard(
            Profile(
                name = "Immanuel",
                regdno = 2101229079,
                sem = 5,
                program = "Btech",
                batch = "2021-2025",
                branch = "CSE",
                section = 'A',
                propicError = DataErrorType.NoDataFound
            )
        )
    }


}

@Preview
@Composable
fun ScorecardTablePreview() {
    ScorecardTable(
        scorecard = Scorecard(
            sems = listOf(
                Sem(
                    1, 9.9f, subjects = listOf(
                        ResultSubject(
                            name = "Subject 1", code = "", credit = 2, grade = Grade.A
                        ),
                        ResultSubject(
                            name = "Subject 2", code = "", credit = 2, grade = Grade.A
                        ),
                        ResultSubject(
                            name = "Subject 3", code = "", credit = 2, grade = Grade.A
                        ),
                    )
                ), Sem(
                    1, 9.9f, subjects = listOf(
                        ResultSubject(
                            name = "Subject 1", code = "", credit = 2, grade = Grade.A
                        ),
                        ResultSubject(
                            name = "Subject 2", code = "", credit = 2, grade = Grade.A
                        ),
                        ResultSubject(
                            name = "Subject 3", code = "", credit = 2, grade = Grade.A
                        ),
                    )
                )
            ),
        )
    )
}