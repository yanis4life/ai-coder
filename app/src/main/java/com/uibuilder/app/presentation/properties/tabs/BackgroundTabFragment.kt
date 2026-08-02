package com.uibuilder.app.presentation.properties.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.uibuilder.app.databinding.TabPropertiesBackgroundBinding
import com.uibuilder.app.domain.model.BackgroundType
import com.uibuilder.app.domain.model.GradientOrientation
import com.uibuilder.app.presentation.common.ColorPickerDialog
import com.uibuilder.app.presentation.properties.PropertiesViewModel
import kotlinx.coroutines.launch

class BackgroundTabFragment : androidx.fragment.app.Fragment() {

    private var _binding: TabPropertiesBackgroundBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PropertiesViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = TabPropertiesBackgroundBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupOrientationSpinner()
        setupListeners()
        observeState()
    }

    private fun setupOrientationSpinner() {
        val orientations = GradientOrientation.values().map { it.cssValue }
        binding.spinnerGradientOrientation.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            orientations
        )
    }

    private fun setupListeners() {
        binding.toggleGroupBackgroundType.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (!isChecked) return@addOnButtonCheckedListener
            val type = when (checkedId) {
                binding.btnSolidColor.id -> BackgroundType.SOLID_COLOR
                binding.btnGradient.id -> BackgroundType.GRADIENT
                binding.btnImage.id -> BackgroundType.IMAGE
                else -> BackgroundType.SOLID_COLOR
            }
            viewModel.update { backgroundType = type }
            updateVisibilityForType(type)
        }

        binding.btnSolidColorPick.setOnClickListener {
            ColorPickerDialog.newInstance(binding.btnSolidColorPick.text.toString())
                .show(parentFragmentManager, "color_picker_solid")
        }

        binding.btnGradientStartColor.setOnClickListener {
            ColorPickerDialog.newInstance(binding.btnGradientStartColor.text.toString())
                .show(parentFragmentManager, "color_picker_grad_start")
        }

        binding.btnGradientEndColor.setOnClickListener {
            ColorPickerDialog.newInstance(binding.btnGradientEndColor.text.toString())
                .show(parentFragmentManager, "color_picker_grad_end")
        }

        binding.spinnerGradientOrientation.onItemSelectedListener =
            object : android.widget.AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: android.widget.AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val orientations = GradientOrientation.values()
                    if (position in orientations.indices) {
                        viewModel.update { gradient = gradient.copy(orientation = orientations[position]) }
                    }
                }

                override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
            }
    }

    private fun updateVisibilityForType(type: BackgroundType) {
        binding.layoutSolidColor.visibility =
            if (type == BackgroundType.SOLID_COLOR) View.VISIBLE else View.GONE
        binding.layoutGradient.visibility =
            if (type == BackgroundType.GRADIENT) View.VISIBLE else View.GONE
        binding.layoutImage.visibility =
            if (type == BackgroundType.IMAGE) View.VISIBLE else View.GONE
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    val component = state.component ?: return@collect
                    val props = component.properties

                    when (props.backgroundType) {
                        BackgroundType.SOLID_COLOR -> binding.btnSolidColor.isChecked = true
                        BackgroundType.GRADIENT -> binding.btnGradient.isChecked = true
                        BackgroundType.IMAGE -> binding.btnImage.isChecked = true
                    }
                    updateVisibilityForType(props.backgroundType)

                    binding.btnSolidColorPick.text = props.backgroundColor
                    binding.btnGradientStartColor.text = props.gradient.startColor
                    binding.btnGradientEndColor.text = props.gradient.endColor

                    val orientIndex = GradientOrientation.values().indexOfFirst { it == props.gradient.orientation }
                    if (orientIndex >= 0 && binding.spinnerGradientOrientation.selectedItemPosition != orientIndex) {
                        binding.spinnerGradientOrientation.setSelection(orientIndex)
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
