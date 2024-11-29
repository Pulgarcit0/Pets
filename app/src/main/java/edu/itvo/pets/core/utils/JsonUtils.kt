package edu.itvo.pets.core.utils

import android.content.Context
import org.json.JSONArray

object JsonUtils {
    fun getPetTypes(context: Context): List<String> {
        val jsonString = context.assets.open("pettype.json").bufferedReader().use { it.readText() }
        val jsonArray = JSONArray(jsonString)
        val petTypes = mutableListOf<String>()
        for (i in 0 until jsonArray.length()) {
            petTypes.add(jsonArray.getString(i))
        }
        return petTypes
    }
}
