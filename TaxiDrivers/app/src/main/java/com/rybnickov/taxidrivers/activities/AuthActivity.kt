package com.rybnickov.taxidrivers.activities

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Switch
import android.widget.Toast
import com.rybnickov.taxidrivers.CurrentData
import com.rybnickov.taxidrivers.R
import com.rybnickov.taxidrivers.api.TaxiApiClient
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException

class AuthActivity : AppCompatActivity() {
    lateinit var phoneView: EditText
    lateinit var passwordView: EditText
    lateinit var rememberMeCheckBox: Switch
    lateinit var prefs: SharedPreferences

    private val client by lazy {
        TaxiApiClient.create(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        phoneView = findViewById(R.id.phone_EditText)
        passwordView = findViewById(R.id.password_EditText)
        rememberMeCheckBox = findViewById(R.id.remember_me_Switch)
        prefs = getSharedPreferences(
            AppCompatActivity.PERFORMANCE_HINT_SERVICE,
            AppCompatActivity.MODE_PRIVATE
        )

        val phone = prefs.getString("phone", "")
        val password = prefs.getString("password", "")
        if (phone != "") {
            passwordView.text.insert(0, password)
            phoneView.text.insert(0, phone)
        }
    }

    fun authClick(view: View) {
        val phone = phoneView.text.toString()
        val password = passwordView.text.toString()
        val context = this;
        CoroutineScope(Dispatchers.IO).launch {
            client?.login(
                phone, password
            )
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    { result ->
                        if (result.id != 0) {
                            if (!result.activity) {
                                Toast.makeText(
                                    context,
                                    "Доступ запрещён",
                                    Toast.LENGTH_LONG
                                ).show()
                                return@subscribe
                            }

                            CurrentData.user = result
                            if (rememberMeCheckBox.isChecked) {
                                val editor = prefs.edit()
                                editor.putString("phone", phone)
                                editor.putString("password", password)
                                editor.commit()
                            }
                            val intent: Intent = Intent(context, FragmentActivity::class.java)
                            startActivity(intent)

                        }
                        else Toast.makeText(
                            context,
                            "Неправильно введён номер телефона или пароль",
                            Toast.LENGTH_LONG
                        ).show()

                    },
                    { error ->
                        var errorMessage: String = ""
                        errorMessage = when (error) {
                            is HttpException -> {
                                when (error.code()) {
                                    401 -> "Необходимо авторизоваться"
                                    403 -> "Неправильный логин или пароль"
                                    else -> "Неизвестная ошибка"
                                }
                            }
                            else -> {
                                error.localizedMessage
                            }
                        }
                        println(errorMessage)
                        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                    }
                )
        }
    }
}
