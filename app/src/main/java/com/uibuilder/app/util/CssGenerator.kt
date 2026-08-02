package com.uibuilder.app.util

import com.uibuilder.app.domain.model.BackgroundType
import com.uibuilder.app.domain.model.ComponentProperties
import com.uibuilder.app.domain.model.ComponentType
import com.uibuilder.app.domain.model.GradientOrientation
import com.uibuilder.app.domain.model.Insets
import com.uibuilder.app.domain.model.UiComponent
import com.uibuilder.app.domain.model.VisibilityMode

class CssGenerator {

    fun generate(
        components: List<UiComponent>,
        themeColor: String = "#6750A4"
    ): String {
        val sb = StringBuilder()
        sb.append(":root {\n")
        sb.append("    --primary: $themeColor;\n")
        sb.append("    --primary-light: ${lighten(themeColor)};\n")
        sb.append("    --primary-dark: ${darken(themeColor)};\n")
        sb.append("    --on-primary: #FFFFFF;\n")
        sb.append("    --background: #FFFBFE;\n")
        sb.append("    --surface: #FFFFFF;\n")
        sb.append("    --on-surface: #1C1B1F;\n")
        sb.append("    --on-surface-variant: #49454F;\n")
        sb.append("    --outline: #79747E;\n")
        sb.append("    --error: #B3261E;\n")
        sb.append("    --font-family: 'Inter', 'Roboto', sans-serif;\n")
        sb.append("    --border-radius: 8px;\n")
        sb.append("    --spacing-unit: 8px;\n")
        sb.append("}\n\n")

        sb.append("* {\n")
        sb.append("    box-sizing: border-box;\n")
        sb.append("    margin: 0;\n")
        sb.append("    padding: 0;\n")
        sb.append("}\n\n")

        sb.append("body {\n")
        sb.append("    font-family: var(--font-family);\n")
        sb.append("    background-color: var(--background);\n")
        sb.append("    color: var(--on-surface);\n")
        sb.append("    line-height: 1.5;\n")
        sb.append("    -webkit-font-smoothing: antialiased;\n")
        sb.append("}\n\n")

        sb.append(".root-container {\n")
        sb.append("    width: 100%;\n")
        sb.append("    min-height: 100vh;\n")
        sb.append("    position: relative;\n")
        sb.append("}\n\n")

        for (component in components) {
            sb.append(generateRule(component))
        }

        sb.append(generateResponsiveRules())
        return sb.toString()
    }

    private fun generateRule(component: UiComponent): String {
        val props = component.properties
        val elementId = props.elementId.ifBlank { defaultIdFor(component) }
        val selector = "#" + elementId

        val sb = StringBuilder()
        sb.append("$selector {\n")

        if (props.width != "auto" && props.width.isNotBlank()) {
            sb.append("    width: ${dimensionToCss(props.width)};\n")
        }
        if (props.height != "auto" && props.height.isNotBlank()) {
            sb.append("    height: ${dimensionToCss(props.height)};\n")
        }
        if (props.minWidth > 0) {
            sb.append("    min-width: ${props.minWidth}px;\n")
        }
        if (props.minHeight > 0) {
            sb.append("    min-height: ${props.minHeight}px;\n")
        }

        if (props.padding != Insets()) {
            sb.append("    padding: ${props.padding.top}px ${props.padding.right}px ${props.padding.bottom}px ${props.padding.left}px;\n")
        }
        if (props.margin != Insets()) {
            sb.append("    margin: ${props.margin.top}px ${props.margin.right}px ${props.margin.bottom}px ${props.margin.left}px;\n")
        }

        when (props.backgroundType) {
            BackgroundType.SOLID_COLOR -> {
                sb.append("    background-color: ${props.backgroundColor};\n")
            }
            BackgroundType.GRADIENT -> {
                sb.append("    background: linear-gradient(${props.gradient.orientation.cssValue}, ${props.gradient.startColor}, ${props.gradient.endColor});\n")
            }
            BackgroundType.IMAGE -> {
                props.backgroundImageUri?.let {
                    sb.append("    background-image: url('$it');\n")
                    sb.append("    background-size: cover;\n")
                    sb.append("    background-position: center;\n")
                }
            }
        }

        if (props.corners.effectiveTopLeft() > 0 || props.corners.effectiveTopRight() > 0 ||
            props.corners.effectiveBottomLeft() > 0 || props.corners.effectiveBottomRight() > 0
        ) {
            sb.append("    border-radius: ${props.corners.effectiveTopLeft()}px ${props.corners.effectiveTopRight()}px ${props.corners.effectiveBottomRight()}px ${props.corners.effectiveBottomLeft()}px;\n")
        }

        if (props.elevation > 0) {
            sb.append("    box-shadow: 0 ${props.elevation}px ${props.elevation * 2}px rgba(0,0,0,0.15);\n")
        }

        if (props.rotation != 0) {
            sb.append("    transform: rotate(${props.rotation}deg);\n")
        }
        if (props.scaleX != 1.0f || props.scaleY != 1.0f) {
            val existingTransform = if (props.rotation != 0) " rotate(${props.rotation}deg)" else ""
            sb.append("    transform: scale(${props.scaleX}, ${props.scaleY})$existingTransform;\n")
        }

        if (props.alpha < 1.0f) {
            sb.append("    opacity: ${props.alpha};\n")
        }

        when (props.visibility) {
            VisibilityMode.HIDDEN -> sb.append("    visibility: hidden;\n")
            VisibilityMode.DISPLAY_NONE -> sb.append("    display: none;\n")
            VisibilityMode.VISIBLE -> {}
        }

        if (component.type in TEXT_COMPONENTS) {
            if (props.textContent.isNotBlank() || component.type == ComponentType.INPUT) {
                sb.append("    font-family: ${props.fontFamily};\n")
                sb.append("    font-size: ${props.fontSize}px;\n")
                sb.append("    color: ${props.textColor};\n")
                if (props.textStyles.isNotEmpty()) {
                    val fontStyles = mutableListOf<String>()
                    if (props.textStyles.any { it.name == "BOLD" }) fontStyles.add("bold")
                    if (props.textStyles.any { it.name == "ITALIC" }) fontStyles.add("italic")
                    if (fontStyles.isNotEmpty()) {
                        sb.append("    font-weight: ${if (fontStyles.contains("bold")) "bold" else "normal"};\n")
                        sb.append("    font-style: ${if (fontStyles.contains("italic")) "italic" else "normal"};\n")
                    }
                    if (props.textStyles.any { it.name == "UNDERLINE" }) {
                        sb.append("    text-decoration: underline;\n")
                    }
                }
            }
        }

        when (component.type) {
            ComponentType.FLEX_ROW, ComponentType.FLEX_COLUMN, ComponentType.FORM -> {
                sb.append("    display: flex;\n")
                sb.append("    flex-direction: ${props.flexDirection};\n")
                sb.append("    justify-content: ${props.justifyContent};\n")
                sb.append("    align-items: ${props.alignItems};\n")
                if (props.gap > 0) {
                    sb.append("    gap: ${props.gap}px;\n")
                }
            }
            ComponentType.GRID -> {
                sb.append("    display: grid;\n")
                sb.append("    grid-template-columns: repeat(${props.gridColumns}, 1fr);\n")
                if (props.gap > 0) {
                    sb.append("    gap: ${props.gap}px;\n")
                }
            }
            ComponentType.SCROLL_CONTAINER -> {
                sb.append("    overflow: auto;\n")
            }
            ComponentType.CAROUSEL -> {
                sb.append("    overflow-x: auto;\n")
                sb.append("    scroll-snap-type: x mandatory;\n")
            }
            ComponentType.IMAGE -> {
                sb.append("    object-fit: cover;\n")
            }
            ComponentType.BUTTON -> {
                sb.append("    cursor: pointer;\n")
                sb.append("    border: none;\n")
                sb.append("    transition: background-color 0.2s ease, transform 0.1s ease;\n")
            }
            ComponentType.INPUT, ComponentType.TEXTAREA, ComponentType.SELECT -> {
                sb.append("    border: 1px solid var(--outline);\n")
                sb.append("    outline: none;\n")
                sb.append("    transition: border-color 0.2s ease;\n")
            }
            ComponentType.LINK -> {
                sb.append("    text-decoration: none;\n")
                sb.append("    cursor: pointer;\n")
            }
            else -> {}
        }

        sb.append("}\n\n")

        if (component.type == ComponentType.BUTTON) {
            sb.append("$selector:hover {\n")
            sb.append("    filter: brightness(0.92);\n")
            sb.append("}\n\n")
            sb.append("$selector:active {\n")
            sb.append("    transform: scale(0.97);\n")
            sb.append("}\n\n")
        }
        if (component.type == ComponentType.INPUT || component.type == ComponentType.TEXTAREA) {
            sb.append("$selector:focus {\n")
            sb.append("    border-color: var(--primary);\n")
            sb.append("    box-shadow: 0 0 0 3px rgba(103, 80, 164, 0.15);\n")
            sb.append("}\n\n")
        }

        return sb.toString()
    }

    private fun generateResponsiveRules(): String {
        val sb = StringBuilder()
        sb.append("@media (max-width: 768px) {\n")
        sb.append("    body { font-size: 14px; }\n")
        sb.append("    .grid { grid-template-columns: 1fr !important; }\n")
        sb.append("    .flex-row { flex-direction: column !important; }\n")
        sb.append("}\n")
        return sb.toString()
    }

    private fun dimensionToCss(value: String): String = when {
        value == "match_parent" -> "100%"
        value == "wrap_content" -> "auto"
        value.endsWith("dp") -> value.replace("dp", "px")
        value.endsWith("px") -> value
        value.matches(Regex("\\d+")) -> "${value}px"
        else -> value
    }

    private fun defaultIdFor(component: UiComponent): String {
        val base = component.type.name.lowercase().replace("_", "-")
        return "$base-${component.id.take(6)}"
    }

    private fun lighten(hex: String): String = shiftHex(hex, 32)
    private fun darken(hex: String): String = shiftHex(hex, -32)

    private fun shiftHex(hex: String, amount: Int): String {
        val cleaned = hex.removePrefix("#")
        if (cleaned.length != 6) return hex
        return try {
            val r = (cleaned.substring(0, 2).toInt(16) + amount).coerceIn(0, 255)
            val g = (cleaned.substring(2, 4).toInt(16) + amount).coerceIn(0, 255)
            val b = (cleaned.substring(4, 6).toInt(16) + amount).coerceIn(0, 255)
            String.format("#%02X%02X%02X", r, g, b)
        } catch (_: Exception) {
            hex
        }
    }

    companion object {
        private val TEXT_COMPONENTS = setOf(
            ComponentType.HEADING,
            ComponentType.PARAGRAPH,
            ComponentType.SPAN,
            ComponentType.BUTTON,
            ComponentType.INPUT,
            ComponentType.TEXTAREA,
            ComponentType.SELECT,
            ComponentType.LINK,
            ComponentType.CHECKBOX,
            ComponentType.RADIO,
            ComponentType.TOGGLE
        )
    }
}
