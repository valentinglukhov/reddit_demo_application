package com.example.redditdemoapplication.ui

import androidx.compose.ui.graphics.Color
import com.example.redditdemoapplication.R

sealed class BottomNavigationRoutes(
    val route: String,
    val title: String,
    val icon: Int,
    val contentDescription: String,
    val tint: Color
) {
    object Feed : BottomNavigationRoutes(
        "feed",
        "Feed",
        R.drawable.icon_feed,
        "Button to switch to the section with news feed",
        Color.Black
    )

    object Favorites : BottomNavigationRoutes(
        "favorites",
        "Favorites",
        R.drawable.icon_favorite,
        "button to switch to the section with favorites",
        Color.Black
    )

    object Profile : BottomNavigationRoutes(
        "Profile",
        "Profile",
        R.drawable.icon_account,
        "Button to switch to the user profile section",
        Color.Black
    )
}
