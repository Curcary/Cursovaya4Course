package com.rybnickov.taxidrivers.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.rybnickov.taxidrivers.CurrentData
import com.rybnickov.taxidrivers.R
import com.rybnickov.taxidrivers.api.TaxiApiClient
import com.rybnickov.taxidrivers.fragments.OrderFragment
import com.rybnickov.taxidrivers.models.Order
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException

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
        acceptbutton.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                client?.takeAnOrder(
                    order.id, CurrentData.user.id
                )
                    ?.subscribeOn(Schedulers.io())
                    ?.observeOn(AndroidSchedulers.mainThread())
                    ?.subscribe(
                        { _ ->
                            CurrentData.order = order
                            CurrentData.order.status="В процессе"
                            (context as AppCompatActivity).supportFragmentManager.beginTransaction()
                                .replace(R.id.fragment_container_view, OrderFragment()).commit()
                        },
                        { error ->
                            var errorMessage: String = ""
                            errorMessage = when (error) {
                                is HttpException -> {
                                    when (error.code()) {
                                        401 -> "Необходимо авторизоваться"
                                        403 -> "Это действие нельзя выполнить"
                                        else -> "Неизвестная ошибка"
                                    }
                                }
                                else -> {
                                    error.localizedMessage
                                }
                            }
                            println(errorMessage)
                            Toast.makeText(view.context, errorMessage, Toast.LENGTH_LONG)
                                .show()
                        }
                    )

            }
        }
        return view
    }
}