package com.abdallah.prayertimequran.data.dataProviders.azkarProvider

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException
import java.nio.charset.StandardCharsets


abstract class ReadFromJson <T>  {
    fun getAllAzkar(context: Context, path: String): java.util.ArrayList<T>? {
        return try {
            val azkarFile = context.assets.open(path)
            val size = azkarFile.available()
            val bytes = ByteArray(size)
            azkarFile.read(bytes)
            azkarFile.close()
            val azkarString = String(bytes, StandardCharsets.UTF_8)
            val gson = Gson()
            gson.fromJson<java.util.ArrayList<T>>(
                azkarString,
                object : TypeToken<List<T?>?>() {}.type
            )
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    abstract fun readSpecificData(
        context: Context,
        data: String,
        path: String
    ): java.util.ArrayList<Any>?
}