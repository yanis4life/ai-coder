package com.uibuilder.app.presentation.palette

import android.content.ClipData
import android.content.ClipDescription
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.uibuilder.app.R
import com.uibuilder.app.domain.model.ComponentType

class PaletteAdapter(
    private val onTap: (ComponentType) -> Unit
) : ListAdapter<ComponentType, PaletteAdapter.PaletteViewHolder>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaletteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_palette, parent, false) as ViewGroup
        return PaletteViewHolder(view)
    }

    override fun onBindViewHolder(holder: PaletteViewHolder, position: Int) {
        holder.bind(getItem(position), onTap)
    }

    class PaletteViewHolder(itemView: ViewGroup) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.paletteItemTitle)

        fun bind(type: ComponentType, onTap: (ComponentType) -> Unit) {
            title.text = type.displayName
            itemView.setOnClickListener { onTap(type) }
            itemView.setOnLongClickListener { view ->
                val clipText = type.name
                val item = ClipData.Item(clipText)
                val dragData = ClipData(
                    clipText,
                    arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN),
                    item
                )
                val shadow = View.DragShadowBuilder(view)
                if (ViewCompat.startDragAndDrop(view, dragData, shadow, null, 0)) {
                    return@setOnLongClickListener true
                }
                false
            }
        }
    }

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<ComponentType>() {
            override fun areItemsTheSame(old: ComponentType, new: ComponentType) = old == new
            override fun areContentsTheSame(old: ComponentType, new: ComponentType) = old == new
        }
    }
}
