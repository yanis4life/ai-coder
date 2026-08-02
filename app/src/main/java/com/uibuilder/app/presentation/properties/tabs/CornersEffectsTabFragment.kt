package com.uibuilder.app.presentation.properties.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.uibuilder.app.databinding.TabPropertiesCornersEffectsBinding
import com.uibuilder.app.domain.model.CornerRadius
import com.uibuilder.app.domain.model.VisibilityMode
import com.uibuilder.app.presentation.properties.PropertiesViewModel
import kotlinx.coroutines.launch

class CornersEffectsTabFragment : androidx.fragment.app.Fragment() {

    private var _binding: TabPropertiesCornersEffectsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PropertiesViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = TabPropertiesCornersEffectsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
        observeState()
    }

    private fun setupListeners() {

        binding.switchRoundAllCorners.setOnCheckedChangeListener { _, checked ->
            viewModel.update { corners = corners.copy(roundAll = checked) }
            updateCornersVisibility(checked)
        }

        binding.sliderCornerTopLeft.addOnChangeListener { _, v, u ->
            if (u) viewModel.update {
                corners = if (corners.roundAll) corners.copy(topLeft = v.toInt())
                else corners.copy(topLeft = v.toInt())
            }
        }
        binding.sliderCornerTopRight.addOnChangeListener { _, v, u ->
            if (u) viewModel.update { corners = corners.copy(topRight = v.toInt()) }
        }
        binding.sliderCornerBottomLeft.addOnChangeListener { _, v, u ->
            if (u) viewModel.update { corners = corners.copy(bottomLeft = v.toInt()) }
        }
        binding.sliderCornerBottomRight.addOnChangeListener { _, v, u ->
            if (u) viewModel.update { corners = corners.copy(bottomRight = v.toInt()) }
        }

        binding.sliderElevation.addOnChangeListener { _, v, u ->
            if (u) viewModel.update { elevation = v.toInt() }
        }

        binding.sliderRotation.addOnChangeListener { _, v, u ->
            if (u) viewModel.update { rotation = v.toInt() }
        }

        binding.sliderAlpha.addOnChangeListener { _, v, u ->
            if (u) viewModel.update { alpha = v }
        }

        binding.sliderScaleX.addOnChangeListener { _, v, u ->
            if (u) viewModel.update { scaleX = v }
        }
        binding.sliderScaleY.addOnChangeListener { _, v, u ->
            if (u) viewModel.update { scaleY = v }
        }

        binding.toggleGroupVisibility.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (!isChecked) return@addOnButtonCheckedListener
            val visibility = when (checkedId) {
                binding.btnVisible.id -> VisibilityMode.VISIBLE
                binding.btnInvisible.id -> VisibilityMode.INVISIBLE
                binding.btnGone.id -> VisibilityMode.GONE
                else -> VisibilityMode.VISIBLE
            }
            viewModel.update { visibility = visibility }
        }
    }

    private fun updateCornersVisibility(roundAll: Boolean) {
        binding.layoutIndividualCorners.visibility =
            if (roundAll) View.GONE else View.VISIBLE
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    val component = state.component ?: return@collect
                    val props = component.properties
                    val c = props.corners

                    if (binding.switchRoundAllCorners.isChecked != c.roundAll) {
                        binding.switchRoundAllCorners.isChecked = c.roundAll
                    }
                    updateCornersVisibility(c.roundAll)

                    updateSlider(binding.sliderCornerTopLeft, c.topLeft.toFloat())
                    updateSlider(binding.sliderCornerTopRight, c.topRight.toFloat())
                    updateSlider(binding.sliderCornerBottomLeft, c.bottomLeft.toFloat())
                    updateSlider(binding.sliderCornerBottomRight, c.bottomRight.toFloat())
                    updateSlider(binding.sliderElevation, props.elevation.toFloat())
                    updateSlider(binding.sliderRotation, props.rotation.toFloat())
                    updateSlider(binding.sliderAlpha, props.alpha)
                    updateSlider(binding.sliderScaleX, props.scaleX)
                    updateSlider(binding.sliderScaleY, props.scaleY)

                    when (props.visibility) {
                        VisibilityMode.VISIBLE -> binding.btnVisible.isChecked = true
                        VisibilityMode.INVISIBLE -> binding.btnInvisible.isChecked = true
                        VisibilityMode.GONE -> binding.btnGone.isChecked = true
                    }
                }
            }
        }
    }

    private fun updateSlider(slider: com.google.android.material.slider.Slider, value: Float) {
        if (slider.value != value) slider.value = value
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
