package com.uibuilder.app.presentation.common

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.skydoves.colorpickerview.ColorPickerView
import com.skydoves.colorpickerview.listeners.ColorListener

class ColorPickerDialog : DialogFragment() {

    private var listener: OnColorSelectedListener? = null

    fun setOnColorSelectedListener(listener: OnColorSelectedListener) {
        this.listener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val initialColor = arguments?.getString(ARG_INITIAL_COLOR) ?: "#FF000000"
        val initialArgb = runCatching { android.graphics.Color.parseColor(initialColor) }.getOrDefault(android.graphics.Color.BLACK)

        val picker = ColorPickerView(requireContext()).apply {
            setInitialColor(initialArgb)
        }

        picker.setColorListener { color: Int, _ ->
            val hex = String.format("#%08X", color)
            listener?.onColorSelected(hex)
        }

        return MaterialAlertDialogBuilder(requireContext())
            .setTitle("Pick a color")
            .setView(picker)
            .setPositiveButton("OK") { _, _ ->
                val hex = String.format("#%08X", picker.color)
                listener?.onColorSelected(hex)
            }
            .setNegativeButton("Cancel", null)
            .create()
    }

    interface OnColorSelectedListener {
        fun onColorSelected(hexColor: String)
    }

    companion object {
        private const val ARG_INITIAL_COLOR = "initial_color"

        fun newInstance(initialColor: String): ColorPickerDialog =
            ColorPickerDialog().apply {
                arguments = Bundle().apply {
                    putString(ARG_INITIAL_COLOR, initialColor)
                }
            }
    }
}
