package com.rybnickov.taxidrivers.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.rybnickov.taxidrivers.CurrentData
import com.rybnickov.taxidrivers.R
import com.rybnickov.taxidrivers.adapters.OrdersAdapter
import com.rybnickov.taxidrivers.api.TaxiApiClient
import com.rybnickov.taxidrivers.fragments.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException

class FragmentActivity : AppCompatActivity() {


    private val client by lazy {
        TaxiApiClient.create(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment)
        hasActiveOrder()

    }

    fun hasActiveOrder() {
        val context = this
        CoroutineScope(Dispatchers.IO).launch {
            client?.hasActiveOrder(
                CurrentData.user.id
            )
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { result ->
                        if (result.id != -1) {
                            CurrentData.order = result
                            navigate(OrderFragment())
                        }
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
                        context.supportFragmentManager.beginTransaction()
                            .replace(
                                R.id.fragment_container_view,
                                OrdersFragment()
                            ).commit()
                    }
                )

        }
    }
    fun goToAccountClick(view: View){
        navigate(AccountFragment())
    }
    fun navigate(fragment:Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.fragment_container_view,
                fragment
            ).commit()
    }

    fun goToOrdersClick(view:View) {
    hasActiveOrder()
    }
    fun goToStatsClick(view:View) {
    navigate(StatsFragment())
    }
    fun goToMenuClick(view:View) {
    navigate(MenuFragment())
    }
}