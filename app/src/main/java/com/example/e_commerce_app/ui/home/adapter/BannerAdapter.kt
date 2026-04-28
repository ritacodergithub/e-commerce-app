package com.example.e_commerce_app.ui.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.e_commerce_app.R

class BannerAdapter(
    private val banners: List<Banner>,
    private val onClick: () -> Unit
) : RecyclerView.Adapter<BannerAdapter.VH>() {

    data class Banner(val titleRes: Int, val subtitleRes: Int, val backgroundRes: Int)

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val card: LinearLayout = view.findViewById(R.id.bannerCard)
        val title: TextView = view.findViewById(R.id.bannerTitle)
        val subtitle: TextView = view.findViewById(R.id.bannerSubtitle)
        val cta: TextView = view.findViewById(R.id.bannerCta)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_banner, parent, false)
        return VH(view)
    }

    override fun getItemCount(): Int = banners.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val banner = banners[position]
        holder.card.setBackgroundResource(banner.backgroundRes)
        holder.title.setText(banner.titleRes)
        holder.subtitle.setText(banner.subtitleRes)
        holder.cta.setOnClickListener { onClick() }
        holder.card.setOnClickListener { onClick() }
    }
}