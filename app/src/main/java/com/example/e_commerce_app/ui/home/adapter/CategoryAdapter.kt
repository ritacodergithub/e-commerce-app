package com.example.e_commerce_app.ui.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.e_commerce_app.R
import com.example.e_commerce_app.domain.model.Category

class CategoryAdapter(
    private val onSelected: (Category) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.VH>() {

    private val items: MutableList<Category> = mutableListOf()
    private var selectedIndex = 0

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val chip: View = view.findViewById(R.id.categoryChip)
        val label: TextView = view.findViewById(R.id.categoryLabel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        val params = ViewGroup.MarginLayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            val m = (4 * parent.resources.displayMetrics.density).toInt()
            marginStart = m
            marginEnd = m
        }
        view.layoutParams = params
        return VH(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val category = items[position]
        holder.label.text = category.name
        val selected = position == selectedIndex
        holder.chip.isSelected = selected
        holder.label.isSelected = selected
        holder.chip.setOnClickListener {
            if (selectedIndex != position) {
                val previous = selectedIndex
                selectedIndex = position
                notifyItemChanged(previous)
                notifyItemChanged(position)
                onSelected(category)
            }
        }
    }

    fun submit(list: List<Category>) {
        items.clear()
        items.addAll(list)
        if (selectedIndex >= items.size) selectedIndex = 0
        notifyDataSetChanged()
    }
}