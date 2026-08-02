package com.uibuilder.app.domain.model

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
enum class VisibilityMode(val cssValue: String) : Parcelable {
    VISIBLE("visible"),
    HIDDEN("hidden"),
    DISPLAY_NONE("none")
}

@Parcelize
enum class TextStyle(val cssValue: String) : Parcelable {
    BOLD("bold"),
    ITALIC("italic"),
    UNDERLINE("underline")
}

@Parcelize
enum class BackgroundType : Parcelable {
    SOLID_COLOR,
    GRADIENT,
    IMAGE
}

@Parcelize
enum class GradientOrientation(val cssValue: String) : Parcelable {
    TOP_BOTTOM("to bottom"),
    LEFT_RIGHT("to right"),
    TR_BL("to bottom left"),
    BR_TL("to top left"),
    BL_TR("to top right"),
    TL_BR("to bottom right")
}

@Parcelize
@JsonClass(generateAdapter = true)
data class Insets(
    val top: Int = 0,
    val right: Int = 0,
    val bottom: Int = 0,
    val left: Int = 0
) : Parcelable {
    val isUniform: Boolean get() = top == right && right == bottom && bottom == left
    val max: Int get() = maxOf(top, right, bottom, left)
}

@Parcelize
@JsonClass(generateAdapter = true)
data class GradientBackground(
    val startColor: String = "#6750A4",
    val endColor: String = "#03DAC5",
    val orientation: GradientOrientation = GradientOrientation.LEFT_RIGHT
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class CornerRadius(
    val topLeft: Int = 0,
    val topRight: Int = 0,
    val bottomLeft: Int = 0,
    val bottomRight: Int = 0,
    val roundAll: Boolean = true
) : Parcelable {
    fun effectiveTopLeft(): Int = if (roundAll) topLeft else topLeft
    fun effectiveTopRight(): Int = if (roundAll) topLeft else topRight
    fun effectiveBottomLeft(): Int = if (roundAll) topLeft else bottomLeft
    fun effectiveBottomRight(): Int = if (roundAll) topLeft else bottomRight
}

@Parcelize
@JsonClass(generateAdapter = true)
data class ComponentProperties(
    val textContent: String = "",
    val fontFamily: String = "Inter, sans-serif",
    val fontSize: Int = 16,
    val textColor: String = "#1C1B1F",
    val textStyles: List<TextStyle> = emptyList(),
    val backgroundType: BackgroundType = BackgroundType.SOLID_COLOR,
    val backgroundColor: String = "#FFFFFF",
    val gradient: GradientBackground = GradientBackground(),
    val backgroundImageUri: String? = null,
    val width: String = "auto",
    val height: String = "auto",
    val minWidth: Int = 0,
    val minHeight: Int = 0,
    val padding: Insets = Insets(0, 0, 0, 0),
    val margin: Insets = Insets(0, 0, 0, 0),
    val corners: CornerRadius = CornerRadius(),
    val elevation: Int = 0,
    val rotation: Int = 0,
    val alpha: Float = 1.0f,
    val scaleX: Float = 1.0f,
    val scaleY: Float = 1.0f,
    val visibility: VisibilityMode = VisibilityMode.VISIBLE,
    val flexDirection: String = "row",
    val justifyContent: String = "flex-start",
    val alignItems: String = "stretch",
    val gap: Int = 0,
    val gridColumns: Int = 2,
    val href: String = "#",
    val src: String = "",
    val alt: String = "",
    val placeholder: String = "",
    val inputType: String = "text",
    val cssClass: String = "",
    val elementId: String = ""
) : Parcelable {

    fun copy(block: ComponentPropertiesBuilder.() -> Unit): ComponentProperties {
        return ComponentPropertiesBuilder(this).apply(block).build()
    }

    companion object {
        fun forType(type: ComponentType): ComponentProperties {
            val base = ComponentProperties(
                width = type.defaultWidth,
                height = type.defaultHeight
            )
            return when (type) {
                ComponentType.HEADING -> base.copy(
                    textContent = "Heading",
                    fontSize = 32,
                    textStyles = listOf(TextStyle.BOLD)
                )
                ComponentType.PARAGRAPH -> base.copy(
                    textContent = "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
                    fontSize = 16,
                    textColor = "#49454F"
                )
                ComponentType.SPAN -> base.copy(
                    textContent = "Span",
                    fontSize = 16
                )
                ComponentType.BUTTON -> base.copy(
                    textContent = "Button",
                    fontSize = 14,
                    padding = Insets(12, 24, 12, 24),
                    corners = CornerRadius(topLeft = 8, roundAll = true),
                    backgroundColor = "#6750A4",
                    textColor = "#FFFFFF"
                )
                ComponentType.INPUT -> base.copy(
                    placeholder = "Enter text",
                    fontSize = 14,
                    padding = Insets(12, 12, 12, 12),
                    corners = CornerRadius(topLeft = 4, roundAll = true),
                    backgroundColor = "#F5F5F5",
                    inputType = "text"
                )
                ComponentType.TEXTAREA -> base.copy(
                    placeholder = "Enter message",
                    fontSize = 14,
                    padding = Insets(12, 12, 12, 12),
                    corners = CornerRadius(topLeft = 4, roundAll = true),
                    backgroundColor = "#F5F5F5"
                )
                ComponentType.IMAGE -> base.copy(
                    width = "120px",
                    height = "120px",
                    backgroundColor = "#EEEEEE",
                    alt = "Image",
                    src = ""
                )
                ComponentType.CARD -> base.copy(
                    padding = Insets(16, 16, 16, 16),
                    corners = CornerRadius(topLeft = 12, roundAll = true),
                    elevation = 4,
                    backgroundColor = "#FFFFFF"
                )
                ComponentType.FLEX_ROW -> base.copy(
                    flexDirection = "row",
                    padding = Insets(8, 8, 8, 8),
                    gap = 8
                )
                ComponentType.FLEX_COLUMN -> base.copy(
                    flexDirection = "column",
                    padding = Insets(8, 8, 8, 8),
                    gap = 8
                )
                ComponentType.GRID -> base.copy(
                    gridColumns = 2,
                    padding = Insets(8, 8, 8, 8),
                    gap = 8
                )
                ComponentType.SCROLL_CONTAINER -> base.copy(
                    width = "100%",
                    height = "300px"
                )
                ComponentType.LIST -> base.copy(
                    textContent = "",
                    padding = Insets(16, 16, 16, 16)
                )
                ComponentType.CAROUSEL -> base.copy(
                    width = "100%",
                    height = "300px"
                )
                ComponentType.SELECT -> base.copy(
                    textContent = "Option 1",
                    fontSize = 14,
                    padding = Insets(8, 12, 8, 12)
                )
                ComponentType.CHECKBOX -> base.copy(
                    textContent = "Checkbox",
                    inputType = "checkbox"
                )
                ComponentType.RADIO -> base.copy(
                    textContent = "Radio",
                    inputType = "radio"
                )
                ComponentType.TOGGLE -> base.copy(
                    textContent = "Toggle"
                )
                ComponentType.PROGRESS -> base.copy(
                    width = "100%",
                    height = "8px"
                )
                ComponentType.LINK -> base.copy(
                    textContent = "Link",
                    href = "#",
                    textColor = "#6750A4",
                    textStyles = listOf(TextStyle.UNDERLINE)
                )
                ComponentType.NAV -> base.copy(
                    padding = Insets(16, 16, 16, 16),
                    backgroundColor = "#FFFFFF"
                )
                ComponentType.HEADER -> base.copy(
                    padding = Insets(16, 16, 16, 16),
                    backgroundColor = "#FFFFFF"
                )
                ComponentType.FOOTER -> base.copy(
                    padding = Insets(16, 16, 16, 16),
                    backgroundColor = "#F5F5F5"
                )
                ComponentType.SECTION -> base.copy(
                    padding = Insets(24, 24, 24, 24)
                )
                ComponentType.FORM -> base.copy(
                    padding = Insets(16, 16, 16, 16),
                    gap = 12
                )
            }
        }
    }
}

@Parcelize
@JsonClass(generateAdapter = true)
data class ComponentConstraints(
    val topToTop: String? = null,
    val topToBottom: String? = null,
    val bottomToTop: String? = null,
    val bottomToBottom: String? = null,
    val startToStart: String? = null,
    val startToEnd: String? = null,
    val endToStart: String? = null,
    val endToEnd: String? = null
) : Parcelable

class ComponentPropertiesBuilder(initial: ComponentProperties) {
    var textContent: String = initial.textContent
    var fontFamily: String = initial.fontFamily
    var fontSize: Int = initial.fontSize
    var textColor: String = initial.textColor
    var textStyles: List<TextStyle> = initial.textStyles
    var backgroundType: BackgroundType = initial.backgroundType
    var backgroundColor: String = initial.backgroundColor
    var gradient: GradientBackground = initial.gradient
    var backgroundImageUri: String? = initial.backgroundImageUri
    var width: String = initial.width
    var height: String = initial.height
    var minWidth: Int = initial.minWidth
    var minHeight: Int = initial.minHeight
    var padding: Insets = initial.padding
    var margin: Insets = initial.margin
    var corners: CornerRadius = initial.corners
    var elevation: Int = initial.elevation
    var rotation: Int = initial.rotation
    var alpha: Float = initial.alpha
    var scaleX: Float = initial.scaleX
    var scaleY: Float = initial.scaleY
    var visibility: VisibilityMode = initial.visibility
    var flexDirection: String = initial.flexDirection
    var justifyContent: String = initial.justifyContent
    var alignItems: String = initial.alignItems
    var gap: Int = initial.gap
    var gridColumns: Int = initial.gridColumns
    var href: String = initial.href
    var src: String = initial.src
    var alt: String = initial.alt
    var placeholder: String = initial.placeholder
    var inputType: String = initial.inputType
    var cssClass: String = initial.cssClass
    var elementId: String = initial.elementId

    fun build(): ComponentProperties = ComponentProperties(
        textContent, fontFamily, fontSize, textColor, textStyles,
        backgroundType, backgroundColor, gradient, backgroundImageUri,
        width, height, minWidth, minHeight,
        padding, margin, corners,
        elevation, rotation, alpha, scaleX, scaleY,
        visibility, flexDirection, justifyContent, alignItems, gap, gridColumns,
        href, src, alt, placeholder, inputType, cssClass, elementId
    )
}
