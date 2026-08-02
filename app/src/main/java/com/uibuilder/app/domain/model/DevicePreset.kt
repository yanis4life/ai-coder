package com.uibuilder.app.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DevicePreset(
    val id: String,
    val name: String,
    val widthDp: Int,
    val heightDp: Int,
    val density: Float
) : Parcelable {
    val aspectRatio: Float get() = widthDp.toFloat() / heightDp.toFloat()
}

object DevicePresets {
    val PHONE_SMALL = DevicePreset("phone_small", "Phone Small", 320, 480, 1.5f)
    val PHONE_MEDIUM = DevicePreset("phone_medium", "Phone Medium", 360, 640, 2.0f)
    val PHONE_LARGE = DevicePreset("phone_large", "Phone Large", 411, 731, 2.625f)
    val TABLET_7 = DevicePreset("tablet_7", "Tablet 7\"", 600, 960, 1.5f)
    val TABLET_10 = DevicePreset("tablet_10", "Tablet 10\"", 800, 1280, 1.5f)

    val ALL: List<DevicePreset> = listOf(
        PHONE_SMALL, PHONE_MEDIUM, PHONE_LARGE, TABLET_7, TABLET_10
    )
}
