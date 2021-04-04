package com.utils

import android.content.Context
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.util.*
import kotlin.collections.ArrayList

object CountryManager {
    fun getAllCountries(context: Context): ArrayList<String> {
        val tempList = ArrayList<String>()
        try {
            val inputStream: InputStream = context.assets.open("countriesToCities.json")
            val size: Int = inputStream.available()
            val byteArray = ByteArray(size)
            inputStream.read(byteArray)
            val jfile = String(byteArray)
            val jobject = JSONObject(jfile)
            val countriNames = jobject.names()
            if (countriNames != null) {
                for (n in 0 until countriNames.length()) {
                    tempList.add(countriNames.getString(n))

                }
            }


        } catch (e: IOException) {

        }
        return tempList

    }
    fun getAllCities(context: Context, country : String): ArrayList<String> {
        val tempList = ArrayList<String>()
        try {
            val inputStream: InputStream = context.assets.open("countriesToCities.json")
            val size: Int = inputStream.available()
            val byteArray = ByteArray(size)
            inputStream.read(byteArray)
            val jfile = String(byteArray)
            val jobject = JSONObject(jfile)
            val cities = jobject.getJSONArray(country)

                for (n in 0 until cities.length()) {
                    tempList.add(cities.getString(n))


            }


        } catch (e: IOException) {

        }
        return tempList

    }
    fun filterListData(list:ArrayList<String>,searchText:String?) : ArrayList<String> {
        val tempList = ArrayList<String>()
        if(searchText == null){
            tempList.add("No Result")
            return tempList
        }
        for(selection:String in list){
            if(selection.toLowerCase(Locale.ROOT).startsWith(searchText.toLowerCase(Locale.ROOT)))
                tempList.add(selection)
        }
        if(tempList.isEmpty())tempList.add("No Result")
        return tempList


    }
}