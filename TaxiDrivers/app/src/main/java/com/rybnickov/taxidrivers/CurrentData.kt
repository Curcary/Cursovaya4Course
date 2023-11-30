package com.rybnickov.taxidrivers

import com.rybnickov.taxidrivers.models.Driver
import com.rybnickov.taxidrivers.models.Order

class CurrentData {
companion object {
    var user: Driver = Driver()
    var order:Order = Order()
}
}