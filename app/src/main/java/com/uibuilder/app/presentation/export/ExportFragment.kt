package com.uibuilder.app.presentation.export

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
import com.google.android.material.snackbar.Snackbar
import com.uibuilder.app.databinding.FragmentExportBinding
import com.uibuilder.app.presentation.main.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ExportFragment : Fragment() {

    private var _binding: FragmentExportBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ExportViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
        observeState()
    }

    private fun setupListeners() {
        binding.btnExportXml.setOnClickListener {
            val projectId = mainViewModel.currentProjectId.value ?: return@setOnClickListener
            val pageTitle = binding.editPackageName.text?.toString()?.ifBlank { "index" }
                ?: "index"
            val cssFileName = binding.editActivityName.text?.toString()?.ifBlank { "styles" }
                ?: "styles"
            val jsFileName = binding.editLayoutName.text?.toString()?.ifBlank { "script" }
                ?: "script"
            val themeColor = binding.editThemeColor.text?.toString()?.ifBlank { "#6750A4" }
                ?: "#6750A4"
            viewModel.exportHtmlCssJs(projectId, pageTitle, cssFileName, jsFileName, themeColor)
        }

        binding.btnExportAar.setOnClickListener {
            Snackbar.make(binding.root, "Standalone bundle: see filesDir/exports/", Snackbar.LENGTH_LONG).show()
        }

        binding.btnExportFigma.setOnClickListener {
            val projectId = mainViewModel.currentProjectId.value ?: return@setOnClickListener
            viewModel.exportFigmaJson(projectId)
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.exportResult.collect { result ->
                    result?.let {
                        Snackbar.make(
                            binding.root,
                            "Exported: ${it.totalBytes} bytes",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.error.collect { msg ->
                    msg?.let {
                        Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
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
