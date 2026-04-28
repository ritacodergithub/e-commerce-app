package com.example.e_commerce_app.ui.cart

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.e_commerce_app.R
import com.example.e_commerce_app.domain.model.CartItem
import com.example.e_commerce_app.util.ImageLoader
import com.example.e_commerce_app.util.PriceFormat

class CartAdapter(
    private val onIncrement: (CartItem) -> Unit,
    private val onDecrement: (CartItem) -> Unit,
    private val onRemove: (CartItem) -> Unit
) : ListAdapter<CartItem, CartAdapter.VH>(DIFF) {

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.cartItemImage)
        val name: TextView = view.findViewById(R.id.cartItemName)
        val brand: TextView = view.findViewById(R.id.cartItemBrand)
        val price: TextView = view.findViewById(R.id.cartItemPrice)
        val qty: TextView = view.findViewById(R.id.cartItemQty)
        val increment: ImageButton = view.findViewById(R.id.cartItemIncrement)
        val decrement: ImageButton = view.findViewById(R.id.cartItemDecrement)
        val remove: ImageButton = view.findViewById(R.id.cartItemRemove)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = getItem(position)
        val product = item.product
        ImageLoader.load(holder.image, product.thumbnail)
        holder.name.text = product.title
        holder.brand.text = product.brand.ifEmpty { product.displayCategory }
        holder.price.text = PriceFormat.format(item.lineTotal)
        holder.qty.text = item.quantity.toString()
        holder.increment.setOnClickListener { onIncrement(item) }
        holder.decrement.setOnClickListener { onDecrement(item) }
        holder.remove.setOnClickListener { onRemove(item) }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<CartItem>() {
            override fun areItemsTheSame(old: CartItem, new: CartItem): Boolean =
                old.product.id == new.product.id
            override fun areContentsTheSame(old: CartItem, new: CartItem): Boolean = old == new
        }
    }
}