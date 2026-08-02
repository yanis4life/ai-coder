package com.uibuilder.app.presentation.templates

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.uibuilder.app.R
import com.uibuilder.app.domain.model.Template

class TemplatesAdapter(
    private val onClick: (Template) -> Unit
) : ListAdapter<Template, TemplatesAdapter.TemplateViewHolder>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TemplateViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_template, parent, false)
        return TemplateViewHolder(view)
    }

    override fun onBindViewHolder(holder: TemplateViewHolder, position: Int) {
        holder.bind(getItem(position), onClick)
    }

    class TemplateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val categoryLabel: TextView = itemView.findViewById(R.id.textCategory)
        private val nameLabel: TextView = itemView.findViewById(R.id.textTemplateName)
        private val descriptionLabel: TextView = itemView.findViewById(R.id.textDescription)

        fun bind(template: Template, onClick: (Template) -> Unit) {
            categoryLabel.text = template.category.displayName.uppercase()
            nameLabel.text = template.name
            descriptionLabel.text = template.description
            itemView.setOnClickListener { onClick(template) }
        }
    }

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<Template>() {
            override fun areItemsTheSame(old: Template, new: Template) = old.id == new.id
            override fun areContentsTheSame(old: Template, new: Template) = old == new
        }
    }
}
