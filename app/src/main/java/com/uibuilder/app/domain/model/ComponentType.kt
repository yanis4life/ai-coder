package com.uibuilder.app.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class ComponentType(
    val displayName: String,
    val htmlTag: String,
    val isContainer: Boolean,
    val defaultWidth: String = "auto",
    val defaultHeight: String = "auto"
) : Parcelable {
    HEADING("Heading", "h1", false, "auto", "auto"),
    PARAGRAPH("Paragraph", "p", false, "100%", "auto"),
    SPAN("Span", "span", false, "auto", "auto"),
    BUTTON("Button", "button", false, "auto", "auto"),
    INPUT("Input", "input", false, "100%", "auto"),
    TEXTAREA("Textarea", "textarea", false, "100%", "auto"),
    IMAGE("Image", "img", false, "auto", "auto"),
    CARD("Card", "div", true, "100%", "auto"),
    FLEX_ROW("Flex Row", "div", true, "100%", "auto"),
    FLEX_COLUMN("Flex Column", "div", true, "100%", "auto"),
    GRID("Grid", "div", true, "100%", "auto"),
    SCROLL_CONTAINER("Scroll Container", "div", true, "100%", "auto"),
    LIST("List", "ul", true, "100%", "auto"),
    CAROUSEL("Carousel", "div", true, "100%", "auto"),
    SELECT("Select", "select", false, "auto", "auto"),
    CHECKBOX("Checkbox", "input", false, "auto", "auto"),
    RADIO("Radio", "input", false, "auto", "auto"),
    TOGGLE("Toggle", "label", false, "auto", "auto"),
    PROGRESS("Progress", "progress", false, "100%", "auto"),
    LINK("Link", "a", false, "auto", "auto"),
    NAV("Nav", "nav", true, "100%", "auto"),
    HEADER("Header", "header", true, "100%", "auto"),
    FOOTER("Footer", "footer", true, "100%", "auto"),
    SECTION("Section", "section", true, "100%", "auto"),
    FORM("Form", "form", true, "100%", "auto");

    companion object {
        fun paletteItems(): List<ComponentType> = values().toList()
    }
}
