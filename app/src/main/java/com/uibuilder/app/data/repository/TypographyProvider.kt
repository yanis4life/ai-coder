package com.uibuilder.app.data.repository

import com.uibuilder.app.domain.model.TypographyPreset
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TypographyProvider @Inject constructor() {

    fun allPresets(): List<TypographyPreset> = listOf(
        TypographyPreset("roboto", "Roboto", "sans-serif", "sans-serif", 28, 18, 14, 12),
        TypographyPreset("roboto_mono", "Roboto Mono", "sans-serif", "monospace", 28, 18, 14, 12),
        TypographyPreset("open_sans", "Open Sans", "sans-serif-medium", "sans-serif", 28, 18, 14, 12),
        TypographyPreset("lato", "Lato", "sans-serif-medium", "sans-serif-light", 28, 18, 14, 12),
        TypographyPreset("montserrat", "Montserrat", "sans-serif-medium", "sans-serif", 30, 18, 14, 12),
        TypographyPreset("raleway", "Raleway", "sans-serif-medium", "sans-serif", 28, 18, 14, 12),
        TypographyPreset("poppins", "Poppins", "sans-serif-medium", "sans-serif-light", 28, 18, 14, 12),
        TypographyPreset("nunito", "Nunito", "sans-serif-medium", "sans-serif", 28, 18, 14, 12),
        TypographyPreset("playfair", "Playfair Display", "serif", "serif", 32, 18, 14, 12),
        TypographyPreset("merriweather", "Merriweather", "serif", "serif", 28, 18, 14, 12),
        TypographyPreset("lora", "Lora", "serif", "serif", 28, 18, 14, 12),
        TypographyPreset("source_sans", "Source Sans Pro", "sans-serif-medium", "sans-serif", 28, 18, 14, 12),
        TypographyPreset("inter", "Inter", "sans-serif-medium", "sans-serif", 28, 18, 14, 12),
        TypographyPreset("manrope", "Manrope", "sans-serif-medium", "sans-serif", 28, 18, 14, 12),
        TypographyPreset("work_sans", "Work Sans", "sans-serif-medium", "sans-serif-light", 28, 18, 14, 12),
        TypographyPreset("mulish", "Mulish", "sans-serif-medium", "sans-serif", 28, 18, 14, 12),
        TypographyPreset("rubik", "Rubik", "sans-serif-medium", "sans-serif", 28, 18, 14, 12),
        TypographyPreset("quicksand", "Quicksand", "sans-serif-medium", "sans-serif-light", 28, 18, 14, 12),
        TypographyPreset("cabin", "Cabin", "sans-serif-medium", "sans-serif", 28, 18, 14, 12),
        TypographyPreset("oswald", "Oswald", "sans-serif-medium", "sans-serif", 30, 18, 14, 12)
    )

    fun getById(id: String): TypographyPreset? = allPresets().find { it.id == id }
}
