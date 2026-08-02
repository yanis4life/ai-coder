package com.uibuilder.app.presentation.canvas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.uibuilder.app.R
import com.uibuilder.app.databinding.FragmentCanvasBinding
import com.uibuilder.app.domain.model.ComponentType
import com.uibuilder.app.presentation.main.MainViewModel
import com.uibuilder.app.presentation.palette.ComponentPaletteBottomSheet
import com.uibuilder.app.presentation.properties.PropertiesBottomSheet
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CanvasFragment : Fragment() {

    private var _binding: FragmentCanvasBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CanvasViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCanvasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCanvas()
        setupToolbar()
        setupFabs()
        observeState()
        observeEvents()

        val currentProjectId = mainViewModel.currentProjectId.value
        if (currentProjectId != null) {
            viewModel.loadProject(currentProjectId)
        } else {
            mainViewModel.createAndOpenProject("Untitled Project")
        }
    }

    private fun setupCanvas() {
        binding.canvasView.listener = object : CanvasView.CanvasListener {
            override fun onComponentDropped(type: ComponentType, x: Float, y: Float) {
                viewModel.addComponent(type, x, y)
            }

            override fun onSelectionChanged(selectedIds: Set<String>) {
                if (selectedIds.isEmpty()) {
                    viewModel.clearSelection()
                } else if (selectedIds.size == 1) {
                    viewModel.selectComponent(selectedIds.first())
                }
            }

            override fun onComponentResized(
                componentId: String,
                x: Float,
                y: Float,
                width: Float,
                height: Float
            ) {
                viewModel.updateComponentGeometry(componentId, x, y, width, height)
            }
        }
    }

    private fun setupToolbar() {
        binding.topAppBar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_undo -> { viewModel.undo(); true }
                R.id.action_redo -> { viewModel.redo(); true }
                R.id.action_delete -> { viewModel.deleteSelected(); true }
                R.id.action_duplicate -> { viewModel.duplicateSelected(); true }
                R.id.action_cut -> { viewModel.cutSelected(); true }
                R.id.action_copy -> { viewModel.copySelected(); true }
                R.id.action_paste -> { viewModel.paste(); true }
                R.id.action_bring_to_front -> { viewModel.bringToFront(); true }
                R.id.action_send_to_back -> { viewModel.sendToBack(); true }
                R.id.action_save -> {  true }
                R.id.action_grid -> { viewModel.toggleGrid(); true }
                R.id.action_rtl -> { viewModel.toggleRtl(); true }
                else -> false
            }
        }
    }

    private fun setupFabs() {
        binding.fabAddComponent.setOnClickListener {
            ComponentPaletteBottomSheet.newInstance()
                .show(parentFragmentManager, "palette")
        }
        binding.fabProperties.setOnClickListener {
            val selectedId = viewModel.uiState.value.selectedIds.firstOrNull()
            if (selectedId != null) {
                PropertiesBottomSheet.newInstance(selectedId)
                    .show(parentFragmentManager, "properties")
            } else {
                Snackbar.make(binding.root, getString(R.string.msg_no_selection), Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    binding.canvasView.setComponents(state.components)
                    binding.canvasView.setSelected(state.selectedIds)
                    binding.canvasView.showGrid = state.showGrid
                    binding.canvasView.snapToGrid = state.snapToGrid
                    binding.canvasView.rtlMode = state.rtlMode
                    binding.topAppBar.menu.findItem(R.id.action_undo)?.isEnabled = state.canUndo
                    binding.topAppBar.menu.findItem(R.id.action_redo)?.isEnabled = state.canRedo
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainViewModel.currentProjectId.collect { pid ->
                    if (pid != null && pid != viewModel.uiState.value.projectId) {
                        viewModel.loadProject(pid)
                    }
                }
            }
        }
    }

    private fun observeEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.events.collect { event ->
                    when (event) {
                        is CanvasEvent.ShowMessage -> {
                            Snackbar.make(binding.root, event.message, Snackbar.LENGTH_SHORT).show()
                        }
                        is CanvasEvent.ShowDeleteConfirmation -> {
                            showDeleteConfirmation(event.componentId)
                        }
                        is CanvasEvent.OpenProperties -> {
                            PropertiesBottomSheet.newInstance(event.componentId)
                                .show(parentFragmentManager, "properties")
                        }
                    }
                }
            }
        }
    }

    private fun showDeleteConfirmation(componentId: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.dialog_delete_title)
            .setMessage(R.string.dialog_delete_message)
            .setNegativeButton(R.string.action_cancel) { _, _ -> }
            .setPositiveButton(R.string.action_confirm) { _, _ ->
                viewModel.performDelete(setOf(componentId))
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
