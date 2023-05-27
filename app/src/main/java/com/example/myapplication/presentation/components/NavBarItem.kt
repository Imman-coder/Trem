package com.example.myapplication.presentation.components

import android.graphics.drawable.Drawable
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.fragment.app.Fragment

data class NavBarItem(val name:String, val route:Fragment , val icon:ImageVector )
