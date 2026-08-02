package com.uibuilder.app.presentation.palette

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.uibuilder.app.databinding.BottomSheetPaletteBinding
import com.uibuilder.app.domain.model.ComponentType
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ComponentPaletteBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetPaletteBinding? = null
    private val binding get() = _binding!!

    private val paletteAdapter = PaletteAdapter { type ->

        val canvas = requireActivity().findViewById<com.uibuilder.app.presentation.canvas.CanvasView>(R.id.canvasView)
        val cx = (canvas?.width ?: 0) / 2f
        val cy = (canvas?.height ?: 0) / 2f
        val listener = canvas?.listener
        listener?.onComponentDropped(type, cx, cy)
        dismiss()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetPaletteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerPalette.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.recyclerPalette.adapter = paletteAdapter
        paletteAdapter.submitList(ComponentType.paletteItems())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(): ComponentPaletteBottomSheet = ComponentPaletteBottomSheet()
    }
}
