package com.uibuilder.app.util

class Memento<T>(private val maxHistory: Int = DEFAULT_MAX_HISTORY) {

    private val undoStack: ArrayDeque<T> = ArrayDeque()
    private val redoStack: ArrayDeque<T> = ArrayDeque()

    val canUndo: Boolean get() = undoStack.size > 1

    val canRedo: Boolean get() = redoStack.isNotEmpty()

    val current: T? get() = undoStack.lastOrNull()

    fun save(state: T) {
        undoStack.addLast(state)
        if (undoStack.size > maxHistory) {
            undoStack.removeFirst()
        }
        redoStack.clear()
    }

    fun undo(): T? {
        if (!canUndo) return null
        val current = undoStack.removeLast()
        redoStack.addLast(current)
        return undoStack.lastOrNull()
    }

    fun redo(): T? {
        if (!canRedo) return null
        val state = redoStack.removeLast()
        undoStack.addLast(state)
        return state
    }

    fun clear() {
        undoStack.clear()
        redoStack.clear()
    }

    companion object {
        const val DEFAULT_MAX_HISTORY = 100
    }
}
