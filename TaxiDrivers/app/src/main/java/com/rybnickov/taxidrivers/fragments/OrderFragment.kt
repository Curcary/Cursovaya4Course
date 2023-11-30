package com.rybnickov.taxidrivers.fragments

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Chronometer
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.rybnickov.taxidrivers.CurrentData
import com.rybnickov.taxidrivers.R
import com.rybnickov.taxidrivers.api.TaxiApiClient
import com.rybnickov.taxidrivers.models.Order
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException

class OrderFragment: Fragment(R.layout.order_fragment) {

    private val client by lazy {
        this@OrderFragment.context?.let { TaxiApiClient.create(it) }
    }
    lateinit var firstaddressTextView: TextView
    lateinit var destinationaddressTextView: TextView
    lateinit var statusTextView: TextView
    lateinit var costTextView: TextView
    lateinit var actionbutton: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firstaddressTextView = view.findViewById(R.id.firstaddress_TextView)
        destinationaddressTextView = view.findViewById(R.id.destinationaddress_TextView)
        statusTextView = view.findViewById(R.id.status_TextView)
        costTextView = view.findViewById(R.id.cost_TextView)
        firstaddressTextView.text = CurrentData.order.firstaddress
        destinationaddressTextView.text = CurrentData.order.destination
        costTextView.text = CurrentData.order.cost.toString()
        setStatus(CurrentData.order.status)
        actionbutton = view.findViewById(R.id.action_button)
        val declinebutton: TextView = view.findViewById(R.id.decline_button)

        declinebutton.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(view.context)
            builder.setMessage("Вы действительно хотите отменить заказ?")
            builder.setPositiveButton("Да") { dialogInterface, i -> declineOrder() }
            builder.setNegativeButton("Нет", null)
            builder.create().show()
        }

        checkStatus(view.context)
    }

    private fun checkStatus(context: Context) {
        if (CurrentData.order.status == "В процессе") {
            actionbutton.setOnClickListener { confirmArrivement(context) }
        } else {
            actionbutton.setOnClickListener {
                actionbutton.text = "Завершить заказ"
                val builder: AlertDialog.Builder = AlertDialog.Builder(context)
                builder.setMessage("Вы действительно хотите завершить заказ?")
                builder.setPositiveButton("Да") { dialogInterface, i -> endOrder() }
                builder.setNegativeButton("Нет", null)
                builder.create().show()
            }
        }

    }
    private fun setStatus(status:String){
        statusTextView.text = "Статус: $status"
    }
    private fun declineOrder() {
        CoroutineScope(Dispatchers.IO).launch {
            client?.declineOrder(
                CurrentData.order.id
            )
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { _ ->
                        CurrentData.order = Order()
                        (context as AppCompatActivity).supportFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container_view, OrdersFragment()).commit()
                        Toast.makeText(this@OrderFragment.context, "Заказ отменён", Toast.LENGTH_LONG)
                            .show()
                    },
                    { error ->
                        var errorMessage = ""
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
                        Toast.makeText(this@OrderFragment.context, errorMessage, Toast.LENGTH_LONG)
                            .show()
                    }
                )

        }
    }

    private fun confirmArrivement(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            client?.confirmArrivement(
                CurrentData.order.id
            )
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { _ ->
                        CurrentData.order.status ="Ожидание"
                        setStatus(CurrentData.order.status)
                        checkStatus(context)
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
                        Toast.makeText(this@OrderFragment.context, errorMessage, Toast.LENGTH_LONG)
                            .show()
                    }
                )

        }
    }

    private fun endOrder() {
        CoroutineScope(Dispatchers.IO).launch {
            client?.endOrder(
                CurrentData.order.id
            )
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { _ ->
                        CurrentData.order = Order()
                        Toast.makeText(this@OrderFragment.context, "Заказ завершён", Toast.LENGTH_LONG)
                            .show()
                        (context as AppCompatActivity).supportFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container_view, OrdersFragment()).commit()
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
                        Toast.makeText(this@OrderFragment.context, errorMessage, Toast.LENGTH_LONG)
                            .show()
                    }
                )

        }
    }
}
