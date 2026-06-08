package com.example.homeserv.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.homeserv.R
import com.example.homeserv.data.Offer
import com.google.android.material.button.MaterialButton

class OfferAdapter(
    private var offers: List<Offer>,
    private val canBook: Boolean,
    private val onBookClick: (Offer) -> Unit
) : RecyclerView.Adapter<OfferAdapter.OfferViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OfferViewHolder =
        OfferViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_offer, parent, false))
    override fun onBindViewHolder(holder: OfferViewHolder, position: Int) = holder.bind(offers[position])
    override fun getItemCount(): Int = offers.size
    fun updateData(newOffers: List<Offer>) { offers = newOffers; notifyDataSetChanged() }

    inner class OfferViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val serviceBadge: TextView = itemView.findViewById(R.id.tvServiceBadge)
        private val title: TextView = itemView.findViewById(R.id.tvOfferTitle)
        private val description: TextView = itemView.findViewById(R.id.tvOfferDescription)
        private val provider: TextView = itemView.findViewById(R.id.tvProviderName)
        private val price: TextView = itemView.findViewById(R.id.tvPrice)
        private val duration: TextView = itemView.findViewById(R.id.tvDuration)
        private val bookButton: MaterialButton = itemView.findViewById(R.id.btnBookNow)
        fun bind(offer: Offer) {
            serviceBadge.text = offer.serviceType
            title.text = offer.title
            description.text = offer.description
            provider.text = itemView.context.getString(R.string.provider_format, offer.providerName)
            price.text = itemView.context.getString(R.string.price_format, offer.price)
            duration.text = itemView.context.getString(R.string.duration_format, offer.duration)
            bookButton.visibility = if (canBook) View.VISIBLE else View.GONE
            bookButton.setOnClickListener { onBookClick(offer) }
        }
    }
}
