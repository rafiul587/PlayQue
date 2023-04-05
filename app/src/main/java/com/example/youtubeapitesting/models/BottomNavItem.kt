package com.example.youtubeapitesting.models

import androidx.annotation.DrawableRes

data class BottomNavItem(
    val name: String,
    val route: String,
    @DrawableRes
    val icon: Int
)
