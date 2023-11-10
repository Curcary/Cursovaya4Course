package com.rybnickov.taxidrivers.models

import com.google.gson.annotations.SerializedName

class Order {
    @SerializedName("orderid")
    var id:Int = -1
    @SerializedName("orderfirstaddress")
    var firstaddress: String = ""
    @SerializedName("orderdestinationaddress")
    var destination:String = ""
    @SerializedName("orderdate")
    var date:String = ""
    @SerializedName("statusname")
    var status:String =""
}