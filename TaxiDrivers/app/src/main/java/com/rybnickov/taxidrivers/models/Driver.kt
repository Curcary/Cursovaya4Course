package com.rybnickov.taxidrivers.models

import com.google.gson.annotations.SerializedName

class Driver {
    @SerializedName("driverid")
    var id:Int = -1
    @SerializedName("driverfirstName")
    var firstName: String = ""
    @SerializedName("driverlastName")
    var lastName: String = ""
    @SerializedName("drivermiddleName")
    var middleName: String = ""
    @SerializedName("driverphone")
    var phone: String = ""
    @SerializedName("driverphoto")
    var photo: String = ""
}