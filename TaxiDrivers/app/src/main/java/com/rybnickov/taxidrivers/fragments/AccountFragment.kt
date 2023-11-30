package com.rybnickov.taxidrivers.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.rybnickov.taxidrivers.Converter
import com.rybnickov.taxidrivers.CurrentData
import com.rybnickov.taxidrivers.R
import com.rybnickov.taxidrivers.activities.AuthActivity
import com.rybnickov.taxidrivers.models.Driver
import com.rybnickov.taxidrivers.models.Order

class AccountFragment:Fragment(R.layout.account_fragment) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val firstnameTextView:TextView = view.findViewById(R.id.firstname_textview)
        val lastnameTextView:TextView = view.findViewById(R.id.lastname_textview)
        val middlenameTextView:TextView = view.findViewById(R.id.middlename_textview)
        val driverIDTextView:TextView = view.findViewById(R.id.driver_id_textview)
        val profilePhoto:ImageView = view.findViewById(R.id.profilePhotoView)
        val quitButton: Button= view.findViewById(R.id.quit_button)
        quitButton.setOnClickListener {
            CurrentData.order = Order()
            CurrentData.user = Driver()
            startActivity(Intent(this.context, AuthActivity::class.java))
        }

        if (CurrentData.user.photo!="")
            profilePhoto.setImageBitmap(Converter.convert(CurrentData.user.photo))
        firstnameTextView.text = "Имя: "+CurrentData.user.firstName
        lastnameTextView.text = "Фамилия: "+CurrentData.user.lastName
        middlenameTextView.text = "Отчество: "+CurrentData.user.middleName
        driverIDTextView.text = "Номер вод. прав: "+CurrentData.user.id.toString()
    }
}