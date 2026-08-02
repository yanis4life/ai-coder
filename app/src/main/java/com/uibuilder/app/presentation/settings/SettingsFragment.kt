package com.uibuilder.app.presentation.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.snackbar.Snackbar
import com.uibuilder.app.databinding.FragmentSettingsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
        observeState()
    }

    private fun setupListeners() {
        binding.switchDarkMode.setOnCheckedChangeListener { _, checked ->
            val mode = if (checked) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
            AppCompatDelegate.setDefaultNightMode(mode)
            viewModel.setDarkMode(checked)
        }

        binding.switchRtl.setOnCheckedChangeListener { _, checked ->
            viewModel.setRtl(checked)
        }

        binding.switchGrid.setOnCheckedChangeListener { _, checked ->
            viewModel.setShowGrid(checked)
        }

        binding.switchSnap.setOnCheckedChangeListener { _, checked ->
            viewModel.setSnapToGrid(checked)
        }

        binding.switchAutosave.setOnCheckedChangeListener { _, checked ->
            viewModel.setAutoSave(checked)
            Snackbar.make(binding.root, "Auto-save ${if (checked) "enabled" else "disabled"}", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.settings.collect { settings ->
                    binding.switchDarkMode.isChecked = settings.darkMode
                    binding.switchRtl.isChecked = settings.rtl
                    binding.switchGrid.isChecked = settings.showGrid
                    binding.switchSnap.isChecked = settings.snapToGrid
                    binding.switchAutosave.isChecked = settings.autoSave
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
