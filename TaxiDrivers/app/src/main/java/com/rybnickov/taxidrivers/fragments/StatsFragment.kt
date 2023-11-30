package com.rybnickov.taxidrivers.fragments

import android.os.Bundle
import android.view.View
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
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

class StatsFragment:Fragment(R.layout.stats_fragment) {

    private val client by lazy {
        this@StatsFragment.context?.let { TaxiApiClient.create(it) }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val stat_switch:Switch = view.findViewById(R.id.stat_switch)
        checkSwitch(stat_switch,view)
        stat_switch.setOnClickListener {
            checkSwitch(stat_switch, view)
        }
    }
    fun checkSwitch(switch: Switch, view: View) {
        if (switch.isChecked) showTodays(view)
        else showMonthly(view)
    }

    fun showMonthly(view:View){
        CoroutineScope(Dispatchers.IO).launch {
            client?.getMonthlyStats(
                CurrentData.user.id
            )
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { result ->
                        setData(view, result.earned.toString(),
                            result.ended_orders.toString())
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
                        Toast.makeText(this@StatsFragment.context, errorMessage, Toast.LENGTH_LONG)
                            .show()
                    }
                )

        }
    }
    fun setData(view:View, earned:String, ended:String) {
        val earned_TextView:TextView = view.findViewById(R.id.earned_money_textview)
        val ended_TextView:TextView = view.findViewById(R.id.ended_orders_textview)
        earned_TextView.text = "Заработано " + earned+" рублей"
        ended_TextView.text = "Завершено " + ended+" заказов"
    }

    fun showTodays(view: View){

        CoroutineScope(Dispatchers.IO).launch {
            client?.getTodaysStats(
                CurrentData.user.id
            )
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { result ->
                        setData(view, result.earned.toString(),
                            result.ended_orders.toString())
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
                        Toast.makeText(this@StatsFragment.context, errorMessage, Toast.LENGTH_LONG)
                            .show()
                    }
                )

        }
    }
}