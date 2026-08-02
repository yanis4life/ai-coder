package com.uibuilder.app.presentation.canvas

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.uibuilder.app.R
import com.uibuilder.app.domain.model.BackgroundType
import com.uibuilder.app.domain.model.ComponentProperties
import com.uibuilder.app.domain.model.ComponentType
import com.uibuilder.app.domain.model.UiComponent
import com.uibuilder.app.domain.model.VisibilityMode

class ComponentViewWrapper @JvmOverloads constructor(
    context: Context,
    initialComponent: UiComponent
) : FrameLayout(context) {

    val componentId: String get() = currentComponent.id
    private var currentComponent: UiComponent = initialComponent
    private var previewView: View? = null

    private val selectionBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.canvas_selection_border)
        strokeWidth = 3f
        style = Paint.Style.STROKE
    }

    private val resizeHandlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.canvas_resize_handle)
        style = Paint.Style.FILL
    }

    private val resizeHandleBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.canvas_resize_handle_border)
        strokeWidth = 2f
        style = Paint.Style.STROKE
    }

    var isSelected: Boolean = false
        set(value) {
            if (field != value) {
                field = value
                invalidate()
            }
        }

    init {
        layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        setWillNotDraw(false)
        rebuildPreview()
    }

    fun update(component: UiComponent) {
        val typeChanged = currentComponent.type != component.type
        currentComponent = component
        if (typeChanged) {
            rebuildPreview()
        } else {
            applyProperties(component.properties)
        }
        invalidate()
    }

    private fun rebuildPreview() {
        removeAllViews()
        previewView = createPreviewView(currentComponent.type).also { view ->
            applyProperties(currentComponent.properties)
            addView(view)
        }
    }

    private fun createPreviewView(type: ComponentType): View {
        return when (type) {
            ComponentType.HEADING, ComponentType.PARAGRAPH, ComponentType.SPAN,
            ComponentType.BUTTON, ComponentType.LINK, ComponentType.NAV,
            ComponentType.HEADER, ComponentType.FOOTER, ComponentType.SECTION ->
                TextView(context).apply {
                    when (type) {
                        ComponentType.BUTTON -> {
                            isAllCaps = false
                            isClickable = true
                        }
                        ComponentType.LINK -> {
                            paintFlags = paintFlags or android.graphics.Paint.UNDERLINE_TEXT_FLAG
                        }
                        else -> {}
                    }
                }
            ComponentType.INPUT, ComponentType.TEXTAREA, ComponentType.SELECT,
            ComponentType.CHECKBOX, ComponentType.RADIO, ComponentType.TOGGLE ->
                TextView(context).apply {
                    when (type) {
                        ComponentType.INPUT, ComponentType.TEXTAREA -> {
                            hint = currentComponent.properties.placeholder
                        }
                        ComponentType.CHECKBOX -> text = "[ ] " + currentComponent.properties.textContent
                        ComponentType.RADIO -> text = "( ) " + currentComponent.properties.textContent
                        ComponentType.TOGGLE -> text = currentComponent.properties.textContent + "  [ON]"
                        else -> {}
                    }
                }
            ComponentType.IMAGE -> View(context).apply {
                setBackgroundColor(ContextCompat.getColor(context, R.color.palette_imageview))
            }
            ComponentType.CARD -> CardView(context).apply {
                radius = 12f * resources.displayMetrics.density
                useCompatPadding = true
            }
            ComponentType.FLEX_ROW, ComponentType.FLEX_COLUMN,
            ComponentType.GRID, ComponentType.SCROLL_CONTAINER, ComponentType.FORM,
            ComponentType.LIST, ComponentType.CAROUSEL ->
                View(context).apply {
                    setBackgroundColor(0x22FF9C27B0.toInt())
                }
            ComponentType.PROGRESS -> View(context).apply {
                setBackgroundColor(0x22FF03DAC5.toInt())
            }
        }
    }

    private fun applyProperties(props: ComponentProperties) {
        val view = previewView ?: return

        if (view is TextView) {
            view.text = props.textContent
            view.textSize = props.fontSize.toFloat()
            try {
                view.setTextColor(android.graphics.Color.parseColor(props.textColor))
            } catch (_: Exception) { }
            view.setPadding(
                dp(props.padding.left),
                dp(props.padding.top),
                dp(props.padding.right),
                dp(props.padding.bottom)
            )
            val typefaceStyle = when {
                props.textStyles.any { it.name == "BOLD" } && props.textStyles.any { it.name == "ITALIC" } ->
                    Typeface.BOLD_ITALIC
                props.textStyles.any { it.name == "BOLD" } -> Typeface.BOLD
                props.textStyles.any { it.name == "ITALIC" } -> Typeface.ITALIC
                else -> Typeface.NORMAL
            }
            view.setTypeface(Typeface.create(props.fontFamily, typefaceStyle))
        }

        when (props.backgroundType) {
            BackgroundType.SOLID_COLOR -> {
                try {
                    view.setBackgroundColor(android.graphics.Color.parseColor(props.backgroundColor))
                } catch (_: Exception) { }
            }
            else -> {}
        }

        view.visibility = when (props.visibility) {
            VisibilityMode.VISIBLE -> VISIBLE
            VisibilityMode.HIDDEN -> INVISIBLE
            VisibilityMode.DISPLAY_NONE -> GONE
        }

        view.rotation = props.rotation.toFloat()
        view.alpha = props.alpha
        view.scaleX = props.scaleX
        view.scaleY = props.scaleY
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            view.elevation = props.elevation * resources.displayMetrics.density
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (isSelected) drawSelectionOverlay(canvas)
    }

    private fun drawSelectionOverlay(canvas: Canvas) {
        val rect = RectF(
            1f, 1f,
            (width - 1).toFloat(), (height - 1).toFloat()
        )
        canvas.drawRect(rect, selectionBorderPaint)
        val handleRadius = 8f * resources.displayMetrics.density
        val positions = listOf(
            0f to 0f,
            width / 2f to 0f,
            width.toFloat() to 0f,
            0f to height / 2f,
            width.toFloat() to height / 2f,
            0f to height.toFloat(),
            width / 2f to height.toFloat(),
            width.toFloat() to height.toFloat()
        )
        for ((x, y) in positions) {
            canvas.drawCircle(x, y, handleRadius, resizeHandlePaint)
            canvas.drawCircle(x, y, handleRadius, resizeHandleBorderPaint)
        }
    }

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()
}
