package com.uibuilder.app.presentation.properties.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.uibuilder.app.databinding.TabPropertiesPaddingMarginBinding
import com.uibuilder.app.domain.model.Insets
import com.uibuilder.app.presentation.properties.PropertiesViewModel
import kotlinx.coroutines.launch

class PaddingMarginTabFragment : androidx.fragment.app.Fragment() {

    private var _binding: TabPropertiesPaddingMarginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PropertiesViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = TabPropertiesPaddingMarginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
        observeState()
    }

    private fun setupListeners() {
        binding.sliderPaddingTop.addOnChangeListener { _, v, u ->
            if (u) viewModel.update { padding = padding.copy(top = v.toInt()) }
        }
        binding.sliderPaddingRight.addOnChangeListener { _, v, u ->
            if (u) viewModel.update { padding = padding.copy(right = v.toInt()) }
        }
        binding.sliderPaddingBottom.addOnChangeListener { _, v, u ->
            if (u) viewModel.update { padding = padding.copy(bottom = v.toInt()) }
        }
        binding.sliderPaddingLeft.addOnChangeListener { _, v, u ->
            if (u) viewModel.update { padding = padding.copy(left = v.toInt()) }
        }

        binding.sliderMarginTop.addOnChangeListener { _, v, u ->
            if (u) viewModel.update { margin = margin.copy(top = v.toInt()) }
        }
        binding.sliderMarginRight.addOnChangeListener { _, v, u ->
            if (u) viewModel.update { margin = margin.copy(right = v.toInt()) }
        }
        binding.sliderMarginBottom.addOnChangeListener { _, v, u ->
            if (u) viewModel.update { margin = margin.copy(bottom = v.toInt()) }
        }
        binding.sliderMarginLeft.addOnChangeListener { _, v, u ->
            if (u) viewModel.update { margin = margin.copy(left = v.toInt()) }
        }

        binding.switchRoundAllPadding.setOnCheckedChangeListener { _, checked ->
            if (checked) {
                val top = viewModel.uiState.value.component?.properties?.padding?.top ?: 0
                viewModel.update { padding = Insets(top, top, top, top) }
            }
        }
        binding.switchRoundAllMargin.setOnCheckedChangeListener { _, checked ->
            if (checked) {
                val top = viewModel.uiState.value.component?.properties?.margin?.top ?: 0
                viewModel.update { margin = Insets(top, top, top, top) }
            }
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    val component = state.component ?: return@collect
                    val props = component.properties
                    val pad = props.padding
                    val mar = props.margin
                    if (binding.sliderPaddingTop.value.toInt() != pad.top) {
                        binding.sliderPaddingTop.value = pad.top.toFloat()
                    }
                    if (binding.sliderPaddingRight.value.toInt() != pad.right) {
                        binding.sliderPaddingRight.value = pad.right.toFloat()
                    }
                    if (binding.sliderPaddingBottom.value.toInt() != pad.bottom) {
                        binding.sliderPaddingBottom.value = pad.bottom.toFloat()
                    }
                    if (binding.sliderPaddingLeft.value.toInt() != pad.left) {
                        binding.sliderPaddingLeft.value = pad.left.toFloat()
                    }
                    if (binding.sliderMarginTop.value.toInt() != mar.top) {
                        binding.sliderMarginTop.value = mar.top.toFloat()
                    }
                    if (binding.sliderMarginRight.value.toInt() != mar.right) {
                        binding.sliderMarginRight.value = mar.right.toFloat()
                    }
                    if (binding.sliderMarginBottom.value.toInt() != mar.bottom) {
                        binding.sliderMarginBottom.value = mar.bottom.toFloat()
                    }
                    if (binding.sliderMarginLeft.value.toInt() != mar.left) {
                        binding.sliderMarginLeft.value = mar.left.toFloat()
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
