package com.rybnickov.taxidrivers

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import okio.ByteString
import java.io.ByteArrayInputStream


class Converter {
    companion object{
        fun convert(base64Str: String): Bitmap? {
            val decodedBytes: ByteArray = Base64.decode(
                base64Str.substring(base64Str.indexOf(",") + 1),
                Base64.DEFAULT
            )
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        }
    }
}