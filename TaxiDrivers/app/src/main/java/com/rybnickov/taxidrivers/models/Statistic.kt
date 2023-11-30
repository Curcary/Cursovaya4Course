package com.rybnickov.taxidrivers.models

import com.google.gson.annotations.SerializedName

class Statistic {
    @SerializedName("endedorders")
    var ended_orders: Int = 0
    @SerializedName("sumcost")
    var earned:Float = 0f
}