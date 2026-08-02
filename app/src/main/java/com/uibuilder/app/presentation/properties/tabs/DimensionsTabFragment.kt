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
import com.uibuilder.app.databinding.TabPropertiesDimensionsBinding
import com.uibuilder.app.presentation.properties.PropertiesViewModel
import kotlinx.coroutines.launch

class DimensionsTabFragment : androidx.fragment.app.Fragment() {

    private var _binding: TabPropertiesDimensionsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PropertiesViewModel by activityViewModels()

    private val dimensionOptions = arrayOf("100%", "auto", "custom")

    private var widthTextWatcher: TextWatcher? = null
    private var heightTextWatcher: TextWatcher? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = TabPropertiesDimensionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSpinners()
        setupListeners()
        observeState()
    }

    private fun setupSpinners() {
        binding.spinnerWidth.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            dimensionOptions
        )
        binding.spinnerHeight.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            dimensionOptions
        )
    }

    private fun setupListeners() {
        binding.spinnerWidth.onItemSelectedListener = simpleListener { selected ->
            val current = viewModel.uiState.value.component?.properties?.width ?: return@simpleListener
            val newValue = when (selected) {
                0 -> "100%"
                1 -> "auto"
                else -> if (current != "100%" && current != "auto") current else "100px"
            }
            viewModel.update { width = newValue }
            binding.editCustomWidth.visibility = if (selected == 2) View.VISIBLE else View.GONE
        }

        binding.spinnerHeight.onItemSelectedListener = simpleListener { selected ->
            val current = viewModel.uiState.value.component?.properties?.height ?: return@simpleListener
            val newValue = when (selected) {
                0 -> "100%"
                1 -> "auto"
                else -> if (current != "100%" && current != "auto") current else "100px"
            }
            viewModel.update { height = newValue }
            binding.editCustomHeight.visibility = if (selected == 2) View.VISIBLE else View.GONE
        }

        widthTextWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val value = s?.toString() ?: ""
                if (value.isNotBlank()) viewModel.update { width = "${value}px" }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }
        binding.editCustomWidth.addTextChangedListener(widthTextWatcher)

        heightTextWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val value = s?.toString() ?: ""
                if (value.isNotBlank()) viewModel.update { height = "${value}px" }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }
        binding.editCustomHeight.addTextChangedListener(heightTextWatcher)

        binding.sliderMinWidth.addOnChangeListener { _, value, fromUser ->
            if (fromUser) viewModel.update { minWidth = value.toInt() }
        }
        binding.sliderMinHeight.addOnChangeListener { _, value, fromUser ->
            if (fromUser) viewModel.update { minHeight = value.toInt() }
        }
    }

    private fun simpleListener(onSelected: (Int) -> Unit) =
        object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: android.widget.AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) { onSelected(position) }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    val component = state.component ?: return@collect
                    val props = component.properties

                    val widthSelection = when {
                        props.width == "100%" -> 0
                        props.width == "auto" -> 1
                        else -> 2
                    }
                    if (binding.spinnerWidth.selectedItemPosition != widthSelection) {
                        binding.spinnerWidth.setSelection(widthSelection)
                    }
                    binding.editCustomWidth.visibility =
                        if (widthSelection == 2) View.VISIBLE else View.GONE
                    val widthNum = props.width.removeSuffix("px")
                    if (widthSelection == 2 && binding.editCustomWidth.text?.toString() != widthNum) {
                        binding.editCustomWidth.removeTextChangedListener(widthTextWatcher)
                        binding.editCustomWidth.setText(widthNum)
                        binding.editCustomWidth.addTextChangedListener(widthTextWatcher)
                    }

                    val heightSelection = when {
                        props.height == "100%" -> 0
                        props.height == "auto" -> 1
                        else -> 2
                    }
                    if (binding.spinnerHeight.selectedItemPosition != heightSelection) {
                        binding.spinnerHeight.setSelection(heightSelection)
                    }
                    binding.editCustomHeight.visibility =
                        if (heightSelection == 2) View.VISIBLE else View.GONE
                    val heightNum = props.height.removeSuffix("px")
                    if (heightSelection == 2 && binding.editCustomHeight.text?.toString() != heightNum) {
                        binding.editCustomHeight.removeTextChangedListener(heightTextWatcher)
                        binding.editCustomHeight.setText(heightNum)
                        binding.editCustomHeight.addTextChangedListener(heightTextWatcher)
                    }

                    if (binding.sliderMinWidth.value.toInt() != props.minWidth) {
                        binding.sliderMinWidth.value = props.minWidth.toFloat()
                    }
                    if (binding.sliderMinHeight.value.toInt() != props.minHeight) {
                        binding.sliderMinHeight.value = props.minHeight.toFloat()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
