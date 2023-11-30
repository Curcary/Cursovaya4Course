package com.rybnickov.taxidrivers.models

import com.google.gson.annotations.SerializedName

class Driver {
    @SerializedName("driverid")
    var id:Int = -1
    @SerializedName("driverfirstname")
    var firstName: String = ""
    @SerializedName("driverlastname")
    var lastName: String = ""
    @SerializedName("drivermiddlename")
    var middleName: String = ""
    @SerializedName("driverphone")
    var phone: String = ""
    @SerializedName("driverphoto")
    var photo: String = ""
    @SerializedName("driveractivity")
    var activity:Boolean = false
}