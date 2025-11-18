package com.example.tott.data

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color

data class User(
    val id: Int,
    val email: String,
    val password: String,
    val color: Color,
    val name: String,
    @DrawableRes val avatarRes: Int
)
