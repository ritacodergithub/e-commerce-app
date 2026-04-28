package com.example.e_commerce_app.ui.home.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.e_commerce_app.R
import com.example.e_commerce_app.domain.model.Product
import com.example.e_commerce_app.util.ImageLoader
import com.example.e_commerce_app.util.PriceFormat

class ProductGridAdapter(
    private val onClick: (Product) -> Unit,
    private val onWishlistToggle: (Product) -> Unit,
    private val isWishlisted: (Int) -> Boolean
) : ListAdapter<Product, ProductGridAdapter.VH>(DIFF) {

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val card: CardView = view.findViewById(R.id.productCard)
        val image: ImageView = view.findViewById(R.id.productImage)
        val discount: TextView = view.findViewById(R.id.productDiscount)
        val brand: TextView = view.findViewById(R.id.productBrand)
        val name: TextView = view.findViewById(R.id.productName)
        val rating: TextView = view.findViewById(R.id.productRating)
        val price: TextView = view.findViewById(R.id.productPrice)
        val priceOriginal: TextView = view.findViewById(R.id.productPriceOriginal)
        val wishlist: ImageButton = view.findViewById(R.id.productWishlist)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val product = getItem(position)
        ImageLoader.load(holder.image, product.thumbnail)
        holder.brand.text = product.brand.ifEmpty { product.displayCategory }
        holder.name.text = product.title
        holder.rating.text = "%.1f".format(product.rating)
        holder.price.text = PriceFormat.format(product.price)

        if (product.discountPercentage > 0) {
            holder.priceOriginal.text = PriceFormat.format(product.originalPrice)
            holder.priceOriginal.paintFlags =
                holder.priceOriginal.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            holder.priceOriginal.visibility = View.VISIBLE
            holder.discount.text = "-${product.discountPercentInt}%"
            holder.discount.visibility = View.VISIBLE
        } else {
            holder.priceOriginal.visibility = View.GONE
            holder.discount.visibility = View.GONE
        }

        holder.wishlist.setImageResource(
            if (isWishlisted(product.id)) R.drawable.ic_heart_filled
            else R.drawable.ic_heart
        )
        holder.wishlist.setOnClickListener { onWishlistToggle(product) }
        holder.card.setOnClickListener { onClick(product) }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Product>() {
            override fun areItemsTheSame(old: Product, new: Product): Boolean = old.id == new.id
            override fun areContentsTheSame(old: Product, new: Product): Boolean = old == new
        }
    }
}