package com.uibuilder.app.data.repository

import com.uibuilder.app.domain.model.ComponentProperties
import com.uibuilder.app.domain.model.ComponentType
import com.uibuilder.app.domain.model.CornerRadius
import com.uibuilder.app.domain.model.Insets
import com.uibuilder.app.domain.model.Template
import com.uibuilder.app.domain.model.TemplateCategory
import com.uibuilder.app.domain.model.UiComponent
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TemplateProvider @Inject constructor() {

    fun allTemplates(): List<Template> = buildList {
        repeat(10) { i ->
            add(
                Template(
                    id = "login_$i",
                    name = "Login ${i + 1}",
                    category = TemplateCategory.LOGIN,
                    description = "Login page variant ${i + 1}",
                    componentsJson = ""
                )
            )
        }
        repeat(5) { i ->
            add(
                Template(
                    id = "signup_$i",
                    name = "Signup ${i + 1}",
                    category = TemplateCategory.SIGNUP,
                    description = "Signup page variant ${i + 1}",
                    componentsJson = ""
                )
            )
        }
        repeat(5) { i ->
            add(
                Template(
                    id = "dashboard_$i",
                    name = "Dashboard ${i + 1}",
                    category = TemplateCategory.DASHBOARD,
                    description = "Dashboard layout variant ${i + 1}",
                    componentsJson = ""
                )
            )
        }
        repeat(5) { i ->
            add(
                Template(
                    id = "profile_$i",
                    name = "Profile ${i + 1}",
                    category = TemplateCategory.PROFILE,
                    description = "Profile page variant ${i + 1}",
                    componentsJson = ""
                )
            )
        }
        repeat(3) { i ->
            add(
                Template(
                    id = "settings_$i",
                    name = "Settings ${i + 1}",
                    category = TemplateCategory.SETTINGS,
                    description = "Settings page variant ${i + 1}",
                    componentsJson = ""
                )
            )
        }
        repeat(3) { i ->
            add(
                Template(
                    id = "chat_$i",
                    name = "Chat ${i + 1}",
                    category = TemplateCategory.CHAT,
                    description = "Chat UI variant ${i + 1}",
                    componentsJson = ""
                )
            )
        }
        repeat(5) { i ->
            add(
                Template(
                    id = "ecommerce_$i",
                    name = "Product Card ${i + 1}",
                    category = TemplateCategory.ECOMMERCE,
                    description = "E-commerce product card variant ${i + 1}",
                    componentsJson = ""
                )
            )
        }
        repeat(3) { i ->
            add(
                Template(
                    id = "news_$i",
                    name = "News ${i + 1}",
                    category = TemplateCategory.NEWS,
                    description = "News article layout variant ${i + 1}",
                    componentsJson = ""
                )
            )
        }
        repeat(4) { i ->
            add(
                Template(
                    id = "onboarding_$i",
                    name = "Onboarding ${i + 1}",
                    category = TemplateCategory.ONBOARDING,
                    description = "Onboarding page variant ${i + 1}",
                    componentsJson = ""
                )
            )
        }
        repeat(3) { i ->
            add(
                Template(
                    id = "form_$i",
                    name = "Form ${i + 1}",
                    category = TemplateCategory.FORMS,
                    description = "Form layout variant ${i + 1}",
                    componentsJson = ""
                )
            )
        }
        repeat(2) { i ->
            add(
                Template(
                    id = "nav_drawer_$i",
                    name = "Sidebar Nav ${i + 1}",
                    category = TemplateCategory.NAV_DRAWER,
                    description = "Sidebar navigation variant ${i + 1}",
                    componentsJson = ""
                )
            )
        }
        repeat(2) { i ->
            add(
                Template(
                    id = "bottom_nav_$i",
                    name = "Top Nav ${i + 1}",
                    category = TemplateCategory.BOTTOM_NAV,
                    description = "Top navigation variant ${i + 1}",
                    componentsJson = ""
                )
            )
        }
    }

    fun buildComponents(templateId: String, projectId: String): List<UiComponent> {
        val parts = templateId.split("_")
        val category = parts.firstOrNull() ?: return emptyList()
        val variant = parts.getOrNull(1)?.toIntOrNull() ?: 0

        return when (category) {
            "login" -> buildLogin(variant, projectId)
            "signup" -> buildSignup(variant, projectId)
            "dashboard" -> buildDashboard(variant, projectId)
            "profile" -> buildProfile(variant, projectId)
            "settings" -> buildSettings(variant, projectId)
            "chat" -> buildChat(variant, projectId)
            "ecommerce" -> buildEcommerce(variant, projectId)
            "news" -> buildNews(variant, projectId)
            "onboarding" -> buildOnboarding(variant, projectId)
            "form" -> buildForm(variant, projectId)
            "navdrawer" -> buildNavDrawer(variant, projectId)
            "bottomnav" -> buildBottomNav(variant, projectId)
            else -> emptyList()
        }
    }

    private fun newId() = UUID.randomUUID().toString()

    private fun comp(
        projectId: String,
        type: ComponentType,
        x: Float,
        y: Float,
        zIndex: Int,
        block: (ComponentProperties.ComponentPropertiesBuilder.() -> Unit)? = null
    ): UiComponent {
        val props = if (block != null) {
            ComponentProperties.forType(type).copy(block)
        } else {
            ComponentProperties.forType(type)
        }
        return UiComponent(
            id = newId(),
            projectId = projectId,
            type = type,
            properties = props,
            x = x,
            y = y,
            zIndex = zIndex
        )
    }

    private fun buildLogin(variant: Int, projectId: String): List<UiComponent> = listOf(
        comp(projectId, ComponentType.HEADING, 32f, 80f, 0) {
            textContent = "Welcome Back"
            fontSize = 32
            textColor = "#6750A4"
        },
        comp(projectId, ComponentType.PARAGRAPH, 32f, 160f, 1) {
            textContent = "Sign in to continue"
            fontSize = 16
            textColor = "#79747E"
        },
        comp(projectId, ComponentType.INPUT, 32f, 220f, 2) {
            placeholder = "Email"
            width = "100%"
            margin = Insets(16, 0, 0, 0)
        },
        comp(projectId, ComponentType.INPUT, 32f, 300f, 3) {
            placeholder = "Password"
            inputType = "password"
            width = "100%"
            margin = Insets(16, 0, 0, 0)
        },
        comp(projectId, ComponentType.BUTTON, 32f, 400f, 4) {
            textContent = "Sign In"
            width = "100%"
            padding = Insets(14, 24, 14, 24)
        }
    )

    private fun buildSignup(variant: Int, projectId: String): List<UiComponent> = listOf(
        comp(projectId, ComponentType.HEADING, 32f, 80f, 0) {
            textContent = "Create Account"
            fontSize = 32
            textColor = "#6750A4"
        },
        comp(projectId, ComponentType.INPUT, 32f, 160f, 1) {
            placeholder = "Full Name"
            width = "100%"
        },
        comp(projectId, ComponentType.INPUT, 32f, 240f, 2) {
            placeholder = "Email"
            width = "100%"
        },
        comp(projectId, ComponentType.INPUT, 32f, 320f, 3) {
            placeholder = "Password"
            inputType = "password"
            width = "100%"
        },
        comp(projectId, ComponentType.BUTTON, 32f, 420f, 4) {
            textContent = "Sign Up"
            width = "100%"
        }
    )

    private fun buildDashboard(variant: Int, projectId: String): List<UiComponent> = listOf(
        comp(projectId, ComponentType.HEADER, 0f, 0f, 0) {
            textContent = "Dashboard"
            fontSize = 24
            width = "100%"
            padding = Insets(16, 16, 16, 16)
        },
        comp(projectId, ComponentType.CARD, 32f, 100f, 1) {
            width = "100%"
            height = "120px"
            padding = Insets(16, 16, 16, 16)
        },
        comp(projectId, ComponentType.CARD, 32f, 240f, 2) {
            width = "100%"
            height = "120px"
            padding = Insets(16, 16, 16, 16)
        },
        comp(projectId, ComponentType.LIST, 32f, 380f, 3) {
            width = "100%"
            height = "300px"
        }
    )

    private fun buildProfile(variant: Int, projectId: String): List<UiComponent> = listOf(
        comp(projectId, ComponentType.IMAGE, 130f, 60f, 0) {
            width = "120px"
            height = "120px"
            corners = CornerRadius(topLeft = 60, roundAll = true)
        },
        comp(projectId, ComponentType.HEADING, 32f, 200f, 1) {
            textContent = "John Doe"
            fontSize = 24
        },
        comp(projectId, ComponentType.PARAGRAPH, 32f, 240f, 2) {
            textContent = "johndoe@example.com"
            fontSize = 14
            textColor = "#79747E"
        },
        comp(projectId, ComponentType.BUTTON, 32f, 320f, 3) {
            textContent = "Edit Profile"
        }
    )

    private fun buildSettings(variant: Int, projectId: String): List<UiComponent> = listOf(
        comp(projectId, ComponentType.HEADING, 32f, 32f, 0) {
            textContent = "Settings"
            fontSize = 24
        },
        comp(projectId, ComponentType.TOGGLE, 32f, 100f, 1) {
            textContent = "Notifications"
            width = "100%"
        },
        comp(projectId, ComponentType.TOGGLE, 32f, 160f, 2) {
            textContent = "Dark Mode"
            width = "100%"
        },
        comp(projectId, ComponentType.TOGGLE, 32f, 220f, 3) {
            textContent = "Auto-Save"
            width = "100%"
        },
        comp(projectId, ComponentType.BUTTON, 32f, 320f, 4) {
            textContent = "Logout"
            width = "100%"
        }
    )

    private fun buildChat(variant: Int, projectId: String): List<UiComponent> = listOf(
        comp(projectId, ComponentType.HEADER, 0f, 0f, 0) {
            textContent = "Chat"
            fontSize = 20
            width = "100%"
            padding = Insets(16, 16, 16, 16)
        },
        comp(projectId, ComponentType.LIST, 32f, 100f, 1) {
            width = "100%"
            height = "500px"
        },
        comp(projectId, ComponentType.INPUT, 32f, 700f, 2) {
            placeholder = "Type a message"
            width = "70%"
        },
        comp(projectId, ComponentType.BUTTON, 280f, 700f, 3) {
            textContent = "Send"
        }
    )

    private fun buildEcommerce(variant: Int, projectId: String): List<UiComponent> = listOf(
        comp(projectId, ComponentType.CARD, 32f, 32f, 0) {
            width = "100%"
            height = "400px"
            padding = Insets(16, 16, 16, 16)
        },
        comp(projectId, ComponentType.IMAGE, 48f, 60f, 1) {
            width = "200px"
            height = "200px"
        },
        comp(projectId, ComponentType.HEADING, 48f, 280f, 2) {
            textContent = "Product Name"
            fontSize = 20
        },
        comp(projectId, ComponentType.PARAGRAPH, 48f, 320f, 3) {
            textContent = "$99.99"
            fontSize = 22
            textColor = "#6750A4"
        },
        comp(projectId, ComponentType.BUTTON, 32f, 380f, 4) {
            textContent = "Add to Cart"
            width = "100%"
        }
    )

    private fun buildNews(variant: Int, projectId: String): List<UiComponent> = listOf(
        comp(projectId, ComponentType.IMAGE, 32f, 32f, 0) {
            width = "100%"
            height = "200px"
        },
        comp(projectId, ComponentType.HEADING, 32f, 250f, 1) {
            textContent = "Breaking News Headline"
            fontSize = 24
        },
        comp(projectId, ComponentType.PARAGRAPH, 32f, 300f, 2) {
            textContent = "Article body content goes here..."
            fontSize = 16
            textColor = "#49454F"
        }
    )

    private fun buildOnboarding(variant: Int, projectId: String): List<UiComponent> = listOf(
        comp(projectId, ComponentType.IMAGE, 100f, 80f, 0) {
            width = "200px"
            height = "200px"
        },
        comp(projectId, ComponentType.HEADING, 32f, 320f, 1) {
            textContent = "Welcome to App"
            fontSize = 28
        },
        comp(projectId, ComponentType.PARAGRAPH, 32f, 380f, 2) {
            textContent = "Get started in seconds"
            fontSize = 16
            textColor = "#79747E"
        },
        comp(projectId, ComponentType.BUTTON, 32f, 480f, 3) {
            textContent = "Next"
            width = "100%"
        }
    )

    private fun buildForm(variant: Int, projectId: String): List<UiComponent> = listOf(
        comp(projectId, ComponentType.HEADING, 32f, 32f, 0) {
            textContent = "Form"
            fontSize = 24
        },
        comp(projectId, ComponentType.INPUT, 32f, 100f, 1) {
            placeholder = "Field 1"
            width = "100%"
        },
        comp(projectId, ComponentType.INPUT, 32f, 180f, 2) {
            placeholder = "Field 2"
            width = "100%"
        },
        comp(projectId, ComponentType.CHECKBOX, 32f, 260f, 3) {
            textContent = "I agree to terms"
        },
        comp(projectId, ComponentType.BUTTON, 32f, 340f, 4) {
            textContent = "Submit"
            width = "100%"
        }
    )

    private fun buildNavDrawer(variant: Int, projectId: String): List<UiComponent> = listOf(
        comp(projectId, ComponentType.IMAGE, 32f, 32f, 0) {
            width = "80px"
            height = "80px"
            corners = CornerRadius(topLeft = 40, roundAll = true)
        },
        comp(projectId, ComponentType.LINK, 32f, 130f, 1) {
            textContent = "Menu Item 1"
            fontSize = 16
        },
        comp(projectId, ComponentType.LINK, 32f, 180f, 2) {
            textContent = "Menu Item 2"
            fontSize = 16
        },
        comp(projectId, ComponentType.LINK, 32f, 230f, 3) {
            textContent = "Menu Item 3"
            fontSize = 16
        },
        comp(projectId, ComponentType.LINK, 32f, 280f, 4) {
            textContent = "Menu Item 4"
            fontSize = 16
        }
    )

    private fun buildBottomNav(variant: Int, projectId: String): List<UiComponent> = listOf(
        comp(projectId, ComponentType.NAV, 0f, 0f, 0) {
            width = "100%"
            padding = Insets(16, 16, 16, 16)
        },
        comp(projectId, ComponentType.LIST, 32f, 100f, 1) {
            width = "100%"
            height = "500px"
        },
        comp(projectId, ComponentType.FOOTER, 0f, 700f, 2) {
            width = "100%"
            height = "60px"
            padding = Insets(16, 16, 16, 16)
        }
    )
}
