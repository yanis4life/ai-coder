package com.uibuilder.app.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ColorTheme(
    val id: String,
    val name: String,
    val primaryColor: String,
    val primaryContainerColor: String,
    val secondaryColor: String,
    val tertiaryColor: String,
    val backgroundColor: String,
    val surfaceColor: String,
    val onPrimaryColor: String = "#FFFFFFFF",
    val onBackgroundColor: String = "#FF1C1B1F",
    val isDark: Boolean = false
) : Parcelable

@Parcelize
data class TypographyPreset(
    val id: String,
    val name: String,
    val headingFont: String,
    val bodyFont: String,
    val headlineSize: Int,
    val subtitleSize: Int,
    val bodySize: Int,
    val captionSize: Int
) : Parcelable
