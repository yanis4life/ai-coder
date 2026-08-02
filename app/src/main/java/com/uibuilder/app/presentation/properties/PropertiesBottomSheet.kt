package com.uibuilder.app.presentation.properties

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import com.uibuilder.app.databinding.BottomSheetPropertiesBinding
import com.uibuilder.app.presentation.canvas.CanvasViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PropertiesBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetPropertiesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PropertiesViewModel by viewModels()
    private val canvasViewModel: CanvasViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetPropertiesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val componentId = arguments?.getString(ARG_COMPONENT_ID) ?: return dismiss()
        viewModel.loadComponent(canvasViewModel.uiState.value, componentId)

        setupPager()
        observeState()
    }

    private fun setupPager() {
        binding.pagerProperties.adapter = PropertiesPagerAdapter(this)
        TabLayoutMediator(binding.tabLayout, binding.pagerProperties) { tab, position ->
            tab.text = PropertiesPagerAdapter.TAB_TITLES[position]
        }.attach()
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    val updated = state.component
                    if (updated != null) {
                        canvasViewModel.updateComponent(updated)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_COMPONENT_ID = "component_id"

        fun newInstance(componentId: String): PropertiesBottomSheet =
            PropertiesBottomSheet().apply {
                arguments = Bundle().apply {
                    putString(ARG_COMPONENT_ID, componentId)
                }
            }
    }
}
