package com.uibuilder.app.util

import com.uibuilder.app.domain.model.BackgroundType
import com.uibuilder.app.domain.model.ComponentProperties
import com.uibuilder.app.domain.model.ComponentType
import com.uibuilder.app.domain.model.GradientOrientation
import com.uibuilder.app.domain.model.Insets
import com.uibuilder.app.domain.model.UiComponent
import com.uibuilder.app.domain.model.VisibilityMode

class HtmlGenerator {

    fun generate(
        pageTitle: String,
        cssFileName: String,
        jsFileName: String,
        components: List<UiComponent>,
        allComponents: List<UiComponent>
    ): String {
        val sb = StringBuilder()
        sb.append("<!DOCTYPE html>\n")
        sb.append("<html lang=\"en\">\n")
        sb.append("<head>\n")
        sb.append("    <meta charset=\"UTF-8\">\n")
        sb.append("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n")
        sb.append("    <title>").append(escapeHtml(pageTitle)).append("</title>\n")
        sb.append("    <link rel=\"stylesheet\" href=\"$cssFileName.css\">\n")
        sb.append("</head>\n")
        sb.append("<body>\n")
        sb.append("    <div class=\"root-container\">\n")

        val sorted = components.filter { it.parentId == null }.sortedBy { it.zIndex }
        for (component in sorted) {
            sb.append(generateComponent(component, allComponents, indent = 2))
        }

        sb.append("    </div>\n")
        sb.append("    <script src=\"$jsFileName.js\"></script>\n")
        sb.append("</body>\n")
        sb.append("</html>\n")
        return sb.toString()
    }

    private fun generateComponent(
        component: UiComponent,
        allComponents: List<UiComponent>,
        indent: Int
    ): String {
        val pad = "    ".repeat(indent)
        val props = component.properties
        val tag = component.type.htmlTag
        val elementId = props.elementId.ifBlank { defaultIdFor(component) }
        val cssClass = if (props.cssClass.isNotBlank()) props.cssClass else elementId

        val sb = StringBuilder()
        sb.append("$pad<$tag")
        sb.append(" id=\"$elementId\"")
        sb.append(" class=\"$cssClass\"")

        when (component.type) {
            ComponentType.IMAGE -> {
                sb.append(" src=\"${escapeAttr(props.src)}\"")
                sb.append(" alt=\"${escapeAttr(props.alt)}\"")
            }
            ComponentType.LINK -> {
                sb.append(" href=\"${escapeAttr(props.href)}\"")
            }
            ComponentType.INPUT -> {
                sb.append(" type=\"${escapeAttr(props.inputType)}\"")
                if (props.placeholder.isNotBlank()) {
                    sb.append(" placeholder=\"${escapeAttr(props.placeholder)}\"")
                }
            }
            ComponentType.TEXTAREA -> {
                if (props.placeholder.isNotBlank()) {
                    sb.append(" placeholder=\"${escapeAttr(props.placeholder)}\"")
                }
            }
            ComponentType.CHECKBOX, ComponentType.RADIO -> {
                sb.append(" type=\"${escapeAttr(props.inputType)}\"")
            }
            ComponentType.PROGRESS -> {
                sb.append(" value=\"70\" max=\"100\"")
            }
            else -> {}
        }

        if (props.visibility == VisibilityMode.HIDDEN) {
            sb.append(" hidden")
        }

        val children = allComponents.filter { it.parentId == component.id }.sortedBy { it.zIndex }
        val hasChildren = children.isNotEmpty() && component.type.isContainer
        val selfClosing = isSelfClosing(component.type)

        if (selfClosing) {
            sb.append(">\n")
            return sb.toString()
        }

        val textContent = textContentFor(component, props)
        if (!hasChildren && textContent.isEmpty()) {
            sb.append("></").append(tag).append(">\n")
            return sb.toString()
        }

        sb.append(">\n")
        if (textContent.isNotEmpty()) {
            sb.append("$pad    ").append(escapeHtml(textContent)).append("\n")
        }
        for (child in children) {
            sb.append(generateComponent(child, allComponents, indent + 1))
        }
        sb.append("$pad</").append(tag).append(">\n")
        return sb.toString()
    }

    private fun textContentFor(
        component: UiComponent,
        props: ComponentProperties
    ): String {
        return when (component.type) {
            ComponentType.SELECT -> {
                val options = props.textContent.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                options.joinToString("") { "        <option value=\"$it\">$it</option>\n" }.trimEnd()
            }
            ComponentType.INPUT, ComponentType.TEXTAREA, ComponentType.IMAGE,
            ComponentType.PROGRESS -> ""
            else -> props.textContent
        }
    }

    private fun isSelfClosing(type: ComponentType): Boolean = when (type) {
        ComponentType.IMAGE, ComponentType.INPUT, ComponentType.PROGRESS,
        ComponentType.CHECKBOX, ComponentType.RADIO -> true
        else -> false
    }

    private fun defaultIdFor(component: UiComponent): String {
        val base = component.type.name.lowercase().replace("_", "-")
        return "$base-${component.id.take(6)}"
    }

    private fun escapeHtml(text: String): String =
        text
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")

    private fun escapeAttr(text: String): String =
        text
            .replace("&", "&amp;")
            .replace("\"", "&quot;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
}
