package com.example.e_commerce_app.ui.wishlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.e_commerce_app.R
import com.example.e_commerce_app.domain.model.Product
import com.example.e_commerce_app.util.ImageLoader
import com.example.e_commerce_app.util.PriceFormat

class WishlistAdapter(
    private val onClick: (Product) -> Unit,
    private val onRemove: (Product) -> Unit,
    private val onMoveToCart: (Product) -> Unit
) : ListAdapter<Product, WishlistAdapter.VH>(DIFF) {

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.wishlistImage)
        val name: TextView = view.findViewById(R.id.wishlistName)
        val brand: TextView = view.findViewById(R.id.wishlistBrand)
        val price: TextView = view.findViewById(R.id.wishlistPrice)
        val remove: ImageButton = view.findViewById(R.id.wishlistRemove)
        val moveToCart: Button = view.findViewById(R.id.wishlistMoveToCart)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_wishlist, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val product = getItem(position)
        ImageLoader.load(holder.image, product.thumbnail)
        holder.name.text = product.title
        holder.brand.text = product.brand.ifEmpty { product.displayCategory }
        holder.price.text = PriceFormat.format(product.price)
        holder.itemView.setOnClickListener { onClick(product) }
        holder.remove.setOnClickListener { onRemove(product) }
        holder.moveToCart.setOnClickListener { onMoveToCart(product) }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Product>() {
            override fun areItemsTheSame(old: Product, new: Product): Boolean = old.id == new.id
            override fun areContentsTheSame(old: Product, new: Product): Boolean = old == new
        }
    }
}