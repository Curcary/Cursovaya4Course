package com.rybnickov.taxidrivers.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.ListView
import android.widget.Toast
import com.rybnickov.taxidrivers.R
import com.rybnickov.taxidrivers.adapters.OrdersAdapter
import com.rybnickov.taxidrivers.api.TaxiApiClient
import com.rybnickov.taxidrivers.models.Order
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException

class Orders_fragment : Fragment(R.layout.fragment_orders_fragment) {

    private val client by lazy {
        this@Orders_fragment.context?.let { TaxiApiClient.create(it) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setList(view)
    }
    fun setList(view: View ) {
        val context =this.context
        var orders: List<Order> = ArrayList()
        CoroutineScope(Dispatchers.IO).launch {
            client?.getActiveOrders(
            )
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { result ->
                        orders = result


                        val ordersListView: ListView =
                            view.findViewById(R.id.orders_listview)
                        val ordersAdapter =
                            context?.let { OrdersAdapter(it, R.layout.order_template, orders.asReversed()) }
                        ordersListView.adapter = ordersAdapter
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
                        Toast.makeText(this@Orders_fragment.context, errorMessage, Toast.LENGTH_LONG)
                            .show()
                    }
                )

        }
    }
}