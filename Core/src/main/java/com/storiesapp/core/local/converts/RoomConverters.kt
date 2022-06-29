package com.storiesapp.core.local.converts

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

class RoomConverters {
    /**
     * Converts a timestamp in [Long] format to [Calendar].
     *
     * @param timestamp timestamp to be converted
     *
     * @return [Calendar] from timestamp
     */
    @TypeConverter
    fun fromTimestamp(timestamp: Long?): Calendar? {
        if (timestamp == null) {
            return null
        }

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        return calendar
    }

    /**
     * Converts a [Calendar] to timestamp in [Long] format.
     *
     * @param calendar to be converted
     *
     * @return timestamp in [Long] format
     */
    @TypeConverter
    fun calendarToTimestamp(calendar: Calendar?): Long? {
        if (calendar == null) {
            return null
        }

        return calendar.timeInMillis
    }

    @TypeConverter
    fun fromString(jsonString: String?): List<String?>? =
            Gson().fromJson(jsonString, object : TypeToken<List<String?>?>() {}.type)

    @TypeConverter
    fun fromArrayList(list: List<String?>?): String? =
            Gson().toJson(list)

    /*@TypeConverter
    fun roadMapCityListToJson(value: List<RoadmapCity>?): String {
        return Gson().toJson(value)
    }*/
}