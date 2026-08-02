package com.uibuilder.app.data.repository

import com.uibuilder.app.domain.model.ColorTheme
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ColorThemeProvider @Inject constructor() {

    fun allThemes(): List<ColorTheme> = listOf(

        ColorTheme("purple_light", "Purple (Light)", "#FF6750A4", "#FFEADDFF", "#FF625B71", "#FF7D5260", "#FFFBFE", "#FFFBFE"),
        ColorTheme("blue_light", "Blue (Light)", "#FF1976D2", "#FFD1E4FF", "#FF039BE5", "#FF0288D1", "#FFFAFAFA", "#FFFFFFFF"),
        ColorTheme("teal_light", "Teal (Light)", "#FF00897B", "#FFB2DFDB", "#FF4DB6AC", "#FF009688", "#FFFAFAFA", "#FFFFFFFF"),
        ColorTheme("orange_light", "Orange (Light)", "#FFFF5722", "#FFFFCCBC", "#FFFF9800", "#FFFF7043", "#FFFAFAFA", "#FFFFFFFF"),
        ColorTheme("pink_light", "Pink (Light)", "#FFE91E63", "#FFF8BBD0", "#FFEC407A", "#FFD81B60", "#FFFAFAFA", "#FFFFFFFF"),
        ColorTheme("indigo_light", "Indigo (Light)", "#FF3F51B5", "#FFC5CAE9", "#FF5C6BC0", "#FF3949AB", "#FFFAFAFA", "#FFFFFFFF"),
        ColorTheme("red_light", "Red (Light)", "#FFD32F2F", "#FFFFCDD2", "#FFE53935", "#FFC62828", "#FFFAFAFA", "#FFFFFFFF"),
        ColorTheme("green_light", "Green (Light)", "#FF388E3C", "#FFC8E6C9", "#FF43A047", "#FF2E7D32", "#FFFAFAFA", "#FFFFFFFF"),
        ColorTheme("amber_light", "Amber (Light)", "#FFFFA000", "#FFFFECB3", "#FFFFB300", "#FFFF8F00", "#FFFAFAFA", "#FFFFFFFF"),
        ColorTheme("cyan_light", "Cyan (Light)", "#FF0097A7", "#FFB2EBF2", "#FF00BCD4", "#FF0097A7", "#FFFAFAFA", "#FFFFFFFF"),
        ColorTheme("deep_purple_light", "Deep Purple (Light)", "#FF7B1FA2", "#FFE1BEE7", "#FF8E24AA", "#FF6A1B9A", "#FFFAFAFA", "#FFFFFFFF"),
        ColorTheme("lime_light", "Lime (Light)", "#FFAFB42B", "#FFF0F4C3", "#FFC0CA33", "#FF9E9D24", "#FFFAFAFA", "#FFFFFFFF"),
        ColorTheme("brown_light", "Brown (Light)", "#FF5D4037", "#FFD7CCC8", "#FF6D4C41", "#FF4E342E", "#FFFAFAFA", "#FFFFFFFF"),
        ColorTheme("grey_light", "Grey (Light)", "#FF455A64", "#FFCFD8DC", "#FF546E7A", "#FF37474F", "#FFFAFAFA", "#FFFFFFFF"),
        ColorTheme("rose_light", "Rose (Light)", "#FFE91E63", "#FFFCE4EC", "#FFF06292", "#FFAD1457", "#FFFAFAFA", "#FFFFFFFF"),

        ColorTheme("purple_dark", "Purple (Dark)", "#FFD0BCFF", "#FF4F378B", "#FFCCC2DC", "#FFEFB8C8", "#FF1C1B1F", "#FF1C1B1F", "#FFFFFFFF", "#FFE6E1E5", true),
        ColorTheme("blue_dark", "Blue (Dark)", "#FF82B1FF", "#FF003C8F", "#FF80D8FF", "#FF82B1FF", "#FF101418", "#FF101418", "#FFFFFFFF", "#FFE6E1E5", true),
        ColorTheme("teal_dark", "Teal (Dark)", "#FF80CBC4", "#FF004D40", "#FF80CBC4", "#FF4DB6AC", "#FF101418", "#FF101418", "#FFFFFFFF", "#FFE6E1E5", true),
        ColorTheme("orange_dark", "Orange (Dark)", "#FFFFAB40", "#FFE65100", "#FFFFAB40", "#FFFF8A65", "#FF101418", "#FF101418", "#FFFFFFFF", "#FFE6E1E5", true),
        ColorTheme("pink_dark", "Pink (Dark)", "#FFFF80AB", "#FF880E4F", "#FFFF80AB", "#FFFF80AB", "#FF101418", "#FF101418", "#FFFFFFFF", "#FFE6E1E5", true),
        ColorTheme("indigo_dark", "Indigo (Dark)", "#FF8C9EFF", "#FF1A237E", "#FF9FA8DA", "#FF8C9EFF", "#FF101418", "#FF101418", "#FFFFFFFF", "#FFE6E1E5", true),
        ColorTheme("red_dark", "Red (Dark)", "#FFFF8A80", "#FFB71C1C", "#FFFF8A80", "#FFFF8A80", "#FF101418", "#FF101418", "#FFFFFFFF", "#FFE6E1E5", true),
        ColorTheme("green_dark", "Green (Dark)", "#FFB9F6CA", "#FF1B5E20", "#FFB9F6CA", "#FF69F0AE", "#FF101418", "#FF101418", "#FFFFFFFF", "#FFE6E1E5", true),
        ColorTheme("amber_dark", "Amber (Dark)", "#FFFFD54F", "#FFFF6F00", "#FFFFD54F", "#FFFFE082", "#FF101418", "#FF101418", "#FFFFFFFF", "#FFE6E1E5", true),
        ColorTheme("cyan_dark", "Cyan (Dark)", "#FF84FFFF", "#FF006064", "#FF84FFFF", "#FF84FFFF", "#FF101418", "#FF101418", "#FFFFFFFF", "#FFE6E1E5", true),
        ColorTheme("deep_purple_dark", "Deep Purple (Dark)", "#FFB388FF", "#FF311B92", "#FFB388FF", "#FFB388FF", "#FF101418", "#FF101418", "#FFFFFFFF", "#FFE6E1E5", true),
        ColorTheme("lime_dark", "Lime (Dark)", "#FFCCFF90", "#FF827717", "#FFCCFF90", "#FFCCFF90", "#FF101418", "#FF101418", "#FFFFFFFF", "#FFE6E1E5", true),
        ColorTheme("brown_dark", "Brown (Dark)", "#FFBCAAA4", "#FF3E2723", "#FFBCAAA4", "#FFBCAAA4", "#FF101418", "#FF101418", "#FFFFFFFF", "#FFE6E1E5", true),
        ColorTheme("grey_dark", "Grey (Dark)", "#FFB0BEC5", "#FF263238", "#FFB0BEC5", "#FFB0BEC5", "#FF101418", "#FF101418", "#FFFFFFFF", "#FFE6E1E5", true),
        ColorTheme("rose_dark", "Rose (Dark)", "#FFFFB6C1", "#FF880E4F", "#FFFFB6C1", "#FFF06292", "#FF101418", "#FF101418", "#FFFFFFFF", "#FFE6E1E5", true)
    )

    fun getById(id: String): ColorTheme? = allThemes().find { it.id == id }
}
