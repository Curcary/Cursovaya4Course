package com.rybnickov.taxidrivers.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import com.rybnickov.taxidrivers.R
import com.rybnickov.taxidrivers.api.TaxiApiClient
import com.rybnickov.taxidrivers.fragments.Orders_fragment
import com.rybnickov.taxidrivers.models.Order

class OrdersAdapter(context: Context, resource: Int, orders: List<Order>) :
    ArrayAdapter<Order>(context, resource, orders) {
    private var inflater: LayoutInflater
    private var layout = resource
    private var orders:List<Order>
    init {
        this.inflater = LayoutInflater.from(context)
        this.orders = orders
    }
    private val client by lazy {
        TaxiApiClient.create(context)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = inflater.inflate(layout, parent, false)
        val timecallview:TextView = view.findViewById(R.id.timecallview)
        val firstaddressview:TextView = view.findViewById(R.id.firstaddressview)
        val destinationaddressview:TextView = view.findViewById(R.id.destinationaddressview)
        val acceptbutton:ImageButton = view.findViewById(R.id.accept_order_button)
        val order:Order = orders[position]
        timecallview.text = order.date
        firstaddressview.text = order.firstaddress
        destinationaddressview.text= order.destination
        return view
    }
}