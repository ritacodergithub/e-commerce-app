package com.example.e_commerce_app.ui.orders

import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.e_commerce_app.R
import com.example.e_commerce_app.domain.model.Order
import com.example.e_commerce_app.util.PriceFormat

class OrderAdapter : ListAdapter<Order, OrderAdapter.VH>(DIFF) {

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val orderId: TextView = view.findViewById(R.id.orderId)
        val status: TextView = view.findViewById(R.id.orderStatus)
        val date: TextView = view.findViewById(R.id.orderDate)
        val total: TextView = view.findViewById(R.id.orderTotal)
        val itemCount: TextView = view.findViewById(R.id.orderItemCount)
        val payment: TextView = view.findViewById(R.id.orderPayment)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val order = getItem(position)
        holder.orderId.text = order.orderId
        holder.date.text = DateUtils.getRelativeTimeSpanString(
            order.placedAt,
            System.currentTimeMillis(),
            DateUtils.MINUTE_IN_MILLIS
        )
        holder.total.text = PriceFormat.format(order.total)
        holder.itemCount.text = "${order.itemCount} items"
        holder.payment.text = order.paymentMethod

        val ageDays = (System.currentTimeMillis() - order.placedAt) / DateUtils.DAY_IN_MILLIS
        val ctx = holder.itemView.context
        holder.status.text = when {
            ageDays >= 4 -> ctx.getString(R.string.status_delivered)
            ageDays >= 2 -> ctx.getString(R.string.status_shipped)
            else -> ctx.getString(R.string.status_processing)
        }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Order>() {
            override fun areItemsTheSame(old: Order, new: Order): Boolean = old.orderId == new.orderId
            override fun areContentsTheSame(old: Order, new: Order): Boolean = old == new
        }
    }
}