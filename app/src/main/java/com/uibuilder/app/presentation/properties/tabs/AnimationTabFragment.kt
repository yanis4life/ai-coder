package com.uibuilder.app.presentation.properties.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.snackbar.Snackbar
import com.uibuilder.app.R
import com.uibuilder.app.databinding.TabPropertiesAnimationBinding
import com.uibuilder.app.domain.model.AnimationConfig
import com.uibuilder.app.domain.model.AnimationInterpolator
import com.uibuilder.app.domain.model.AnimationType
import com.uibuilder.app.domain.model.RepeatMode
import com.uibuilder.app.domain.model.SequenceMode
import com.uibuilder.app.presentation.canvas.CanvasViewModel
import com.uibuilder.app.presentation.properties.PropertiesViewModel
import kotlinx.coroutines.launch

class AnimationTabFragment : androidx.fragment.app.Fragment() {

    private var _binding: TabPropertiesAnimationBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PropertiesViewModel by activityViewModels()
    private val canvasViewModel: CanvasViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = TabPropertiesAnimationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSpinners()
        setupListeners()
        observeState()
    }

    private fun setupSpinners() {
        binding.spinnerAnimType.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            AnimationType.values().map { it.displayName }
        )
        binding.spinnerInterpolator.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            AnimationInterpolator.values().map { it.displayName }
        )
        binding.spinnerRepeatMode.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            arrayOf("Restart", "Reverse")
        )
    }

    private fun setupListeners() {
        binding.spinnerAnimType.onItemSelectedListener = simpleListener { pos ->
            val type = AnimationType.values()[pos]
            updateCurrentAnimation { it.copy(type = type) }
        }

        binding.spinnerInterpolator.onItemSelectedListener = simpleListener { pos ->
            val interp = AnimationInterpolator.values()[pos]
            updateCurrentAnimation { it.copy(interpolator = interp) }
        }

        binding.spinnerRepeatMode.onItemSelectedListener = simpleListener { pos ->
            val mode = if (pos == 0) RepeatMode.RESTART else RepeatMode.REVERSE
            updateCurrentAnimation { it.copy(repeatMode = mode) }
        }

        binding.sliderDuration.addOnChangeListener { _, v, u ->
            if (u) updateCurrentAnimation { it.copy(durationMs = v.toInt()) }
        }

        binding.sliderDelay.addOnChangeListener { _, v, u ->
            if (u) updateCurrentAnimation { it.copy(delayMs = v.toInt()) }
        }

        binding.sliderRepeatCount.addOnChangeListener { _, v, u ->
            if (u) updateCurrentAnimation { it.copy(repeatCount = v.toInt()) }
        }

        binding.toggleSequenceMode.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (!isChecked) return@addOnButtonCheckedListener
            val mode = when (checkedId) {
                binding.btnSequential.id -> SequenceMode.SEQUENTIAL
                binding.btnParallel.id -> SequenceMode.PARALLEL
                else -> SequenceMode.SEQUENTIAL
            }
            updateCurrentAnimation { it.copy(sequenceMode = mode) }
        }

        binding.btnAddAnimation.setOnClickListener {
            val current = viewModel.uiState.value.component ?: return@setOnClickListener
            val newAnim = AnimationConfig(
                type = AnimationType.FADE_IN,
                durationMs = 500,
                sequenceOrder = current.animations.size
            )
            viewModel.update {  }

            val updated = current.copy(animations = current.animations + newAnim)

            canvasViewModel.updateComponent(updated)
        }

        binding.btnPlay.setOnClickListener {
            previewAnimation()
        }

        binding.btnExportAnimXml.setOnClickListener {

            Snackbar.make(binding.root, "Animation XML available in Export screen", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun updateCurrentAnimation(block: (AnimationConfig) -> AnimationConfig) {
        val component = viewModel.uiState.value.component ?: return
        val animations = component.animations.toMutableList()
        if (animations.isEmpty()) {
            animations.add(AnimationConfig(sequenceOrder = 0))
        }
        val currentIndex = binding.spinnerAnimType.selectedItemPosition.coerceAtMost(animations.lastIndex)
        animations[currentIndex] = block(animations[currentIndex])
        val updated = component.copy(animations = animations)

        canvasViewModel.updateComponent(updated)
    }

    private fun previewAnimation() {
        val component = viewModel.uiState.value.component ?: return
        if (component.animations.isEmpty()) {
            Snackbar.make(binding.root, "Add an animation first", Snackbar.LENGTH_SHORT).show()
            return
        }
        val anim = component.animations.first()
        val resId = when (anim.type) {
            AnimationType.FADE_IN -> R.anim.fade_in
            AnimationType.FADE_OUT -> R.anim.fade_out
            AnimationType.SLIDE_UP -> R.anim.slide_up
            AnimationType.SLIDE_DOWN -> R.anim.slide_down
            AnimationType.SLIDE_LEFT -> R.anim.slide_left
            AnimationType.SLIDE_RIGHT -> R.anim.slide_right
            AnimationType.BOUNCE -> R.anim.bounce
            AnimationType.PULSE -> R.anim.pulse
            AnimationType.SHAKE -> R.anim.shake
            AnimationType.ROTATE -> R.anim.rotate
            AnimationType.ZOOM_IN -> R.anim.zoom_in
            AnimationType.ZOOM_OUT -> R.anim.zoom_out
            AnimationType.FLIP_X -> R.anim.flip_x
            AnimationType.FLIP_Y -> R.anim.flip_y
        }
        val androidAnim = AnimationUtils.loadAnimation(requireContext(), resId)
        androidAnim.duration = anim.durationMs.toLong()
        androidAnim.startOffset = anim.delayMs.toLong()

        val canvas = requireActivity().findViewById<com.uibuilder.app.presentation.canvas.CanvasView>(R.id.canvasView)

        Snackbar.make(binding.root, "Playing ${anim.type.displayName}", Snackbar.LENGTH_SHORT).show()
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
                    val anim = component.animations.firstOrNull() ?: AnimationConfig()
                    if (binding.spinnerAnimType.selectedItemPosition != anim.type.ordinal) {
                        binding.spinnerAnimType.setSelection(anim.type.ordinal)
                    }
                    if (binding.spinnerInterpolator.selectedItemPosition != anim.interpolator.ordinal) {
                        binding.spinnerInterpolator.setSelection(anim.interpolator.ordinal)
                    }
                    val repModeIdx = if (anim.repeatMode == RepeatMode.RESTART) 0 else 1
                    if (binding.spinnerRepeatMode.selectedItemPosition != repModeIdx) {
                        binding.spinnerRepeatMode.setSelection(repModeIdx)
                    }
                    if (binding.sliderDuration.value.toInt() != anim.durationMs) {
                        binding.sliderDuration.value = anim.durationMs.toFloat()
                    }
                    if (binding.sliderDelay.value.toInt() != anim.delayMs) {
                        binding.sliderDelay.value = anim.delayMs.toFloat()
                    }
                    if (binding.sliderRepeatCount.value.toInt() != anim.repeatCount) {
                        binding.sliderRepeatCount.value = anim.repeatCount.toFloat()
                    }
                    when (anim.sequenceMode) {
                        SequenceMode.SEQUENTIAL -> binding.btnSequential.isChecked = true
                        SequenceMode.PARALLEL -> binding.btnParallel.isChecked = true
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
