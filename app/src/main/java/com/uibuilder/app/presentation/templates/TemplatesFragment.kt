package com.uibuilder.app.presentation.templates

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.uibuilder.app.databinding.FragmentTemplatesBinding
import com.uibuilder.app.domain.model.TemplateCategory
import com.uibuilder.app.presentation.main.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TemplatesFragment : Fragment() {

    private var _binding: FragmentTemplatesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TemplatesViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    private val adapter = TemplatesAdapter { template ->
        val projectId = mainViewModel.currentProjectId.value
        if (projectId == null) {
            Snackbar.make(binding.root, "Create a project first", Snackbar.LENGTH_SHORT).show()
        } else {
            viewModel.applyTemplate(template.id, projectId)
            Snackbar.make(binding.root, "Applied template: ${template.name}", Snackbar.LENGTH_SHORT).show()
            findNavController().navigateUp()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTemplatesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerTemplates.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerTemplates.adapter = adapter
        viewModel.loadTemplates()
        observeState()
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.templates.collect { templates ->
                    adapter.submitList(templates)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
