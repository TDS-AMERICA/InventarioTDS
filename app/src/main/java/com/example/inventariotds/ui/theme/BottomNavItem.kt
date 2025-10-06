package com.example.inventariotds.ui.theme

import androidx.annotation.DrawableRes

data class BottomNavItem(
    val route: String,
    val label: String,
    @DrawableRes val icon: Int,
    val selectedIcon: Int? = null
)
