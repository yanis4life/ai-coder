package com.uibuilder.app.presentation.properties.tabs

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.chip.Chip
import com.uibuilder.app.databinding.TabPropertiesTextBinding
import com.uibuilder.app.domain.model.TextStyle
import com.uibuilder.app.presentation.properties.PropertiesViewModel
import kotlinx.coroutines.launch

class TextTabFragment : androidx.fragment.app.Fragment() {

    private var _binding: TabPropertiesTextBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PropertiesViewModel by activityViewModels()

    private var textWatcher: TextWatcher? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = TabPropertiesTextBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupFontFamilySpinner()
        setupStyleChips()
        setupListeners()
        observeState()
    }

    private fun setupFontFamilySpinner() {
        val fonts = arrayOf(
            "sans-serif", "sans-serif-light", "sans-serif-medium", "sans-serif-black",
            "sans-serif-condensed", "sans-serif-thin", "serif", "monospace"
        )
        binding.spinnerFontFamily.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            fonts
        )
    }

    private fun setupStyleChips() {
        binding.chipBold.setOnCheckedChangeListener { _, checked ->
            updateStyle(TextStyle.BOLD, checked)
        }
        binding.chipItalic.setOnCheckedChangeListener { _, checked ->
            updateStyle(TextStyle.ITALIC, checked)
        }
        binding.chipUnderline.setOnCheckedChangeListener { _, checked ->
            updateStyle(TextStyle.UNDERLINE, checked)
        }
    }

    private fun updateStyle(style: TextStyle, enabled: Boolean) {
        viewModel.update {
            val current = textStyles.toMutableList()
            if (enabled && style !in current) current.add(style)
            if (!enabled) current.remove(style)
            textStyles = current
        }
    }

    private fun setupListeners() {
        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                viewModel.update { textContent = s?.toString() ?: "" }
            }
        }
        binding.editContent.addTextChangedListener(textWatcher)

        binding.sliderFontSize.addOnChangeListener { _, value, fromUser ->
            if (fromUser) viewModel.update { fontSize = value.toInt() }
        }
        binding.editTextColor.setOnClickListener {

            com.uibuilder.app.presentation.common.ColorPickerDialog
                .newInstance(binding.editTextColor.text.toString())
                .show(parentFragmentManager, "color_picker_text")
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    val component = state.component ?: return@collect
                    val props = component.properties

                    if (binding.editContent.text?.toString() != props.textContent) {
                        binding.editContent.removeTextChangedListener(textWatcher)
                        binding.editContent.setText(props.textContent)
                        binding.editContent.addTextChangedListener(textWatcher)
                    }

                    if (binding.sliderFontSize.value.toInt() != props.fontSize) {
                        binding.sliderFontSize.value = props.fontSize.toFloat()
                    }

                    val fontIndex = (binding.spinnerFontFamily.adapter as? ArrayAdapter<String>)
                        ?.getPosition(props.fontFamily) ?: -1
                    if (fontIndex >= 0 && binding.spinnerFontFamily.selectedItemPosition != fontIndex) {
                        binding.spinnerFontFamily.setSelection(fontIndex)
                    }

                    binding.editTextColor.text = props.textColor

                    binding.chipBold.isChecked = TextStyle.BOLD in props.textStyles
                    binding.chipItalic.isChecked = TextStyle.ITALIC in props.textStyles
                    binding.chipUnderline.isChecked = TextStyle.UNDERLINE in props.textStyles
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
