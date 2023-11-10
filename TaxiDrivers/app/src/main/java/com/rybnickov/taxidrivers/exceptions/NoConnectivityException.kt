package com.rybnickov.taxidrivers.exceptions

import java.io.IOException


class NoConnectivityException : IOException() {
    override val message: String
        get() = "Нет соединения с интернетом"
}