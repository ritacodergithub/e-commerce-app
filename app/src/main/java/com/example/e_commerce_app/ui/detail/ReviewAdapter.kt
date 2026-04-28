package com.example.e_commerce_app.ui.detail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.e_commerce_app.R
import com.example.e_commerce_app.domain.model.Review

class ReviewAdapter : RecyclerView.Adapter<ReviewAdapter.VH>() {

    private val items: MutableList<Review> = mutableListOf()

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.reviewerName)
        val rating: TextView = view.findViewById(R.id.reviewRating)
        val comment: TextView = view.findViewById(R.id.reviewComment)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_review, parent, false)
        val params = ViewGroup.MarginLayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            val m = (8 * parent.resources.displayMetrics.density).toInt()
            topMargin = m; bottomMargin = m
        }
        view.layoutParams = params
        return VH(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val review = items[position]
        holder.name.text = review.reviewer
        holder.rating.text = review.rating.toString()
        holder.comment.text = review.comment
    }

    fun submit(list: List<Review>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }
}