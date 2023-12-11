package com.immanlv.trem.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.TableChart
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun FloatingBottomNavigation(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    Column( Modifier.fillMaxWidth(),horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Card(
            modifier = modifier
                .height(60.dp)
                .clip(RoundedCornerShape(30.dp)),
        ) {
            Row(
                Modifier
                    .padding(6.dp)
                    .fillMaxHeight(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                content()
            }
        }
    }

}

@Composable
fun FloatingNavigationBarItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    label: @Composable () -> Unit = {},
) {
    Row(
        modifier = modifier
            .clip(
                RoundedCornerShape(24.dp)
            )
            .clickable { onClick() }
            .background(if (selected) MaterialTheme.colorScheme.secondaryContainer else Color.Unspecified)
            .padding(horizontal = 24.dp)
            .height(48.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon()
        if(selected)
            Spacer(modifier = Modifier.width(6.dp))
        AnimatedVisibility(visible = selected) {
            label()
        }
    }
}

@Preview
@Composable
fun FloatingNavigationBarItemPreview() {
    Row {
        FloatingNavigationBarItem(
            selected = false,
            onClick = {},
            icon = {
                Icon(
                    Icons.Outlined.Home,
                    contentDescription = "Home",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        ) { Text(text = "Home", color = MaterialTheme.colorScheme.primary) }
    }

}

@Preview
@Composable
fun FloatingBottomNavigationPreview() {
    val navigationSelectedItem by remember {
        mutableIntStateOf(0)
    }
    val navigationItems = listOf(
        FloatingBottomNavigationItem(
            label = "Home", icon = Icons.Outlined.Home, route = ""
        ),
        FloatingBottomNavigationItem(
            label = "Timetable", icon = Icons.Outlined.TableChart, route = ""
        ),
        FloatingBottomNavigationItem(
            label = "Attendance", icon = Icons.Outlined.Badge, route = ""
        ),
        FloatingBottomNavigationItem(
            label = "Profile", icon = Icons.Outlined.AccountCircle, route = ""
        ),
    )

    FloatingBottomNavigation {
        navigationItems.forEachIndexed { id, item ->
            FloatingNavigationBarItem(
                selected = navigationSelectedItem == id,
                onClick = {  },
                icon = {
                    Icon(item.icon, contentDescription = item.label,tint = MaterialTheme.colorScheme.primary)
                }
            ) {
                Text(text = item.label, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}