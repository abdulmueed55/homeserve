package com.example.homeserv.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.homeserv.R
import com.example.homeserv.data.BookingItem
import com.example.homeserv.data.BookingStatus
import com.google.android.material.button.MaterialButton

class BookingAdapter(
    private var bookings: List<BookingItem>,
    private val canComplete: Boolean,
    private val onCompleteClick: (BookingItem) -> Unit
) : RecyclerView.Adapter<BookingAdapter.BookingViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder =
        BookingViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_booking, parent, false))
    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) = holder.bind(bookings[position])
    override fun getItemCount(): Int = bookings.size
    fun updateData(newBookings: List<BookingItem>) { bookings = newBookings; notifyDataSetChanged() }

    inner class BookingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val bookingId: TextView = itemView.findViewById(R.id.tvBookingId)
        private val service: TextView = itemView.findViewById(R.id.tvBookingService)
        private val customer: TextView = itemView.findViewById(R.id.tvBookingCustomer)
        private val price: TextView = itemView.findViewById(R.id.tvBookingPrice)
        private val dateTime: TextView = itemView.findViewById(R.id.tvBookingDateTime)
        private val status: TextView = itemView.findViewById(R.id.tvBookingStatus)
        private val completeButton: MaterialButton = itemView.findViewById(R.id.btnMarkCompleted)
        fun bind(booking: BookingItem) {
            bookingId.text = itemView.context.getString(R.string.booking_id_format, booking.id)
            service.text = booking.serviceTitle
            customer.text = itemView.context.getString(R.string.customer_format, booking.customerName)
            price.text = itemView.context.getString(R.string.price_format, booking.price)
            dateTime.text = booking.dateTime
            status.text = booking.status
            val color = if (booking.status == BookingStatus.COMPLETED) R.color.status_completed else R.color.status_active
            status.backgroundTintList = ContextCompat.getColorStateList(itemView.context, color)
            completeButton.visibility = if (canComplete && booking.status == BookingStatus.ACTIVE) View.VISIBLE else View.GONE
            completeButton.setOnClickListener { onCompleteClick(booking) }
        }
    }
}
