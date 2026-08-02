package com.uibuilder.app.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Template(
    val id: String,
    val name: String,
    val category: TemplateCategory,
    val description: String,
    val previewDrawableRes: Int = 0,
    val componentsJson: String
) : Parcelable

enum class TemplateCategory(val displayName: String) {
    LOGIN("Login"),
    SIGNUP("Signup"),
    DASHBOARD("Dashboard"),
    PROFILE("Profile"),
    SETTINGS("Settings"),
    CHAT("Chat"),
    ECOMMERCE("E-Commerce"),
    NEWS("News"),
    ONBOARDING("Onboarding"),
    FORMS("Forms"),
    NAV_DRAWER("Navigation Drawer"),
    BOTTOM_NAV("Bottom Navigation")
}
