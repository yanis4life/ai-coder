package com.uibuilder.app.presentation.canvas

import android.content.ClipData
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF
import android.os.Build
import android.util.AttributeSet
import android.view.DragEvent
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.uibuilder.app.R
import com.uibuilder.app.domain.model.ComponentType
import com.uibuilder.app.domain.model.UiComponent
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class CanvasView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    var gridSize: Int = dp(8)
        set(value) {
            field = value
            invalidate()
        }

    var showGrid: Boolean = true
        set(value) {
            field = value
            invalidate()
        }

    var snapToGrid: Boolean = false

    var deviceWidthPx: Int = 0
        set(value) {
            field = value
            requestLayout()
        }

    var deviceHeightPx: Int = 0
        set(value) {
            field = value
            requestLayout()
        }

    var rtlMode: Boolean = false
        set(value) {
            field = value
            layoutDirection = if (value) LAYOUT_DIRECTION_RTL else LAYOUT_DIRECTION_LOCALE
        }

    var listener: CanvasListener? = null

    private val componentViews = mutableMapOf<String, ComponentViewWrapper>()
    private val selectedIds = mutableSetOf<String>()
    private var activeResizeHandle: ResizeHandle? = null
    private var lastTouchPoint = PointF()
    private var initialPosition = PointF()
    private var initialSize = PointF()
    private var initialTouch = PointF()

    private val gridPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.canvas_grid_line)
        strokeWidth = 1f
        style = Paint.Style.STROKE
    }

    private val selectionPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
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

    init {
        setWillNotDraw(false)
        isClickable = true
        isFocusable = true
        layoutDirection = LAYOUT_DIRECTION_LOCALE
        setOnDragListener { _, event -> handleDragEvent(event) }
    }

    fun setComponents(components: List<UiComponent>) {

        val currentIds = components.map { it.id }.toSet()
        val toRemove = componentViews.keys - currentIds
        for (id in toRemove) {
            removeView(componentViews.remove(id))
            selectedIds.remove(id)
        }

        for (component in components.sortedBy { it.zIndex }) {
            val existing = componentViews[component.id]
            if (existing == null) {
                val wrapper = ComponentViewWrapper(context, component)
                wrapper.layoutParams = computeLayoutParams(component)
                addView(wrapper)
                componentViews[component.id] = wrapper
            } else {
                existing.update(component)
                existing.layoutParams = computeLayoutParams(component)
            }
        }

        invalidate()
    }

    fun getSelectedIds(): Set<String> = selectedIds.toSet()

    fun setSelected(ids: Set<String>) {
        selectedIds.clear()
        selectedIds.addAll(ids)
        for ((id, wrapper) in componentViews) {
            wrapper.isSelected = id in selectedIds
        }
        invalidate()
    }

    fun clearCanvas() {
        removeAllViews()
        componentViews.clear()
        selectedIds.clear()
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val targetWidth = if (deviceWidthPx > 0) deviceWidthPx else MeasureSpec.getSize(widthMeasureSpec)
        val targetHeight = if (deviceHeightPx > 0) deviceHeightPx else MeasureSpec.getSize(heightMeasureSpec)
        super.onMeasure(
            MeasureSpec.makeMeasureSpec(targetWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(targetHeight, MeasureSpec.EXACTLY)
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (showGrid) drawGrid(canvas)

    }

    private fun drawGrid(canvas: Canvas) {
        val w = width.toFloat()
        val h = height.toFloat()
        var x = 0f
        while (x <= w) {
            canvas.drawLine(x, 0f, x, h, gridPaint)
            x += gridSize
        }
        var y = 0f
        while (y <= h) {
            canvas.drawLine(0f, y, w, y, gridPaint)
            y += gridSize
        }
    }

    private fun handleDragEvent(event: DragEvent): Boolean {
        return when (event.action) {
            DragEvent.ACTION_DRAG_STARTED -> true
            DragEvent.ACTION_DRAG_ENTERED -> true
            DragEvent.ACTION_DRAG_LOCATION -> true
            DragEvent.ACTION_DROP -> {
                val type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    event.clipDescription?.label?.toString()
                } else {
                    event.clipDescription?.label?.toString()
                } ?: return false
                val componentType = runCatching { ComponentType.valueOf(type) }.getOrNull()
                    ?: return false

                var x = event.x
                var y = event.y
                if (snapToGrid) {
                    x = (x / gridSize * gridSize).toFloat()
                    y = (y / gridSize * gridSize).toFloat()
                }
                performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                listener?.onComponentDropped(componentType, x, y)
                true
            }
            DragEvent.ACTION_DRAG_ENDED -> true
            else -> false
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                lastTouchPoint.set(event.x, event.y)
                initialTouch.set(event.x, event.y)

                val handle = hitTestResizeHandle(event.x, event.y)
                if (handle != null) {
                    activeResizeHandle = handle
                    val wrapper = componentViews[handle.componentId]
                    wrapper?.let {
                        initialPosition.set(it.x, it.y)
                        initialSize.set(it.width.toFloat(), it.height.toFloat())
                    }
                    return true
                }

                val hitChild = hitTestChild(event.x, event.y)
                if (hitChild == null) {
                    if (selectedIds.isNotEmpty()) {
                        setSelected(emptySet())
                        listener?.onSelectionChanged(emptySet())
                    }
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (activeResizeHandle != null) {
                    handleResize(event.x, event.y)
                    return true
                }
            }
            MotionEvent.ACTION_UP -> {
                val moved = abs(event.x - initialTouch.x) > 5 || abs(event.y - initialTouch.y) > 5
                if (!moved) {
                    val hitChild = hitTestChild(event.x, event.y)
                    if (hitChild != null) {
                        val isMultiSelect = event.isCtrlPressed
                        val newSelection = if (isMultiSelect) {
                            selectedIds.toMutableSet().apply {
                                if (contains(hitChild)) remove(hitChild) else add(hitChild)
                            }
                        } else {
                            mutableSetOf(hitChild)
                        }
                        setSelected(newSelection)
                        listener?.onSelectionChanged(newSelection)
                    }
                }
                activeResizeHandle = null
            }
        }
        return super.onTouchEvent(event)
    }

    private fun handleResize(x: Float, y: Float) {
        val handle = activeResizeHandle ?: return
        val wrapper = componentViews[handle.componentId] ?: return
        val dx = x - initialTouch.x
        val dy = y - initialTouch.y
        val newLeft: Float
        val newTop: Float
        val newRight: Float
        val newBottom: Float
        val left = initialPosition.x
        val top = initialPosition.y
        val right = initialPosition.x + initialSize.x
        val bottom = initialPosition.y + initialSize.y

        when (handle.side) {
            ResizeSide.TOP_LEFT -> {
                newLeft = left + dx; newTop = top + dy
                newRight = right; newBottom = bottom
            }
            ResizeSide.TOP_RIGHT -> {
                newLeft = left; newTop = top + dy
                newRight = right + dx; newBottom = bottom
            }
            ResizeSide.BOTTOM_LEFT -> {
                newLeft = left + dx; newTop = top
                newRight = right; newBottom = bottom + dy
            }
            ResizeSide.BOTTOM_RIGHT -> {
                newLeft = left; newTop = top
                newRight = right + dx; newBottom = bottom + dy
            }
            ResizeSide.TOP -> {
                newLeft = left; newTop = top + dy
                newRight = right; newBottom = bottom
            }
            ResizeSide.BOTTOM -> {
                newLeft = left; newTop = top
                newRight = right; newBottom = bottom + dy
            }
            ResizeSide.LEFT -> {
                newLeft = left + dx; newTop = top
                newRight = right; newBottom = bottom
            }
            ResizeSide.RIGHT -> {
                newLeft = left; newTop = top
                newRight = right + dx; newBottom = bottom
            }
        }

        val newWidth = max(40f, newRight - newLeft)
        val newHeight = max(40f, newBottom - newTop)
        val newX = if (handle.side.affectsLeft) min(newLeft, newRight - 40f) else newLeft
        val newY = if (handle.side.affectsTop) min(newTop, newBottom - 40f) else newTop

        wrapper.x = newX
        wrapper.y = newY
        val lp = wrapper.layoutParams
        lp.width = newWidth.toInt()
        lp.height = newHeight.toInt()
        wrapper.layoutParams = lp

        listener?.onComponentResized(handle.componentId, newX, newY, newWidth, newHeight)
    }

    private fun hitTestResizeHandle(x: Float, y: Float): ResizeHandle? {
        for (id in selectedIds) {
            val wrapper = componentViews[id] ?: continue
            val handlePositions = computeHandlePositions(wrapper)
            for ((side, point) in handlePositions) {
                val dx = x - point.x
                val dy = y - point.y
                if (dx * dx + dy * dy <= 24f * 24f) {
                    return ResizeHandle(id, side)
                }
            }
        }
        return null
    }

    private fun hitTestChild(x: Float, y: Float): String? {

        for (i in childCount - 1 downTo 0) {
            val child = getChildAt(i) as? ComponentViewWrapper ?: continue
            val left = child.x
            val top = child.y
            val right = child.x + child.width
            val bottom = child.y + child.height
            if (x in left..right && y in top..bottom) {
                return child.componentId
            }
        }
        return null
    }

    private fun computeHandlePositions(wrapper: ComponentViewWrapper): Map<ResizeSide, PointF> {
        val left = wrapper.x
        val top = wrapper.y
        val right = wrapper.x + wrapper.width
        val bottom = wrapper.y + wrapper.height
        val cx = (left + right) / 2f
        val cy = (top + bottom) / 2f
        return mapOf(
            ResizeSide.TOP_LEFT to PointF(left, top),
            ResizeSide.TOP_RIGHT to PointF(right, top),
            ResizeSide.BOTTOM_LEFT to PointF(left, bottom),
            ResizeSide.BOTTOM_RIGHT to PointF(right, bottom),
            ResizeSide.TOP to PointF(cx, top),
            ResizeSide.BOTTOM to PointF(cx, bottom),
            ResizeSide.LEFT to PointF(left, cy),
            ResizeSide.RIGHT to PointF(right, cy)
        )
    }

    private fun computeLayoutParams(component: UiComponent): LayoutParams {
        val width = if (component.width > 0) component.width.toInt() else LayoutParams.WRAP_CONTENT
        val height = if (component.height > 0) component.height.toInt() else LayoutParams.WRAP_CONTENT
        return LayoutParams(width, height).apply {
            leftMargin = component.x.toInt()
            topMargin = component.y.toInt()
        }
    }

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()

    interface CanvasListener {
        fun onComponentDropped(type: ComponentType, x: Float, y: Float)
        fun onSelectionChanged(selectedIds: Set<String>)
        fun onComponentResized(componentId: String, x: Float, y: Float, width: Float, height: Float)
    }

    private data class ResizeHandle(
        val componentId: String,
        val side: ResizeSide
    )

    enum class ResizeSide(val affectsLeft: Boolean, val affectsTop: Boolean) {
        TOP_LEFT(true, true),
        TOP_RIGHT(false, true),
        BOTTOM_LEFT(true, false),
        BOTTOM_RIGHT(false, false),
        TOP(false, true),
        BOTTOM(false, false),
        LEFT(true, false),
        RIGHT(false, false)
    }
}
