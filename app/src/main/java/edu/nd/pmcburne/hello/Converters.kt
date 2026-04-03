package edu.nd.pmcburne.hello

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromTagList(tags: List<String>): String {
        return tags.joinToString("||")
    }

    @TypeConverter
    fun toTagList(tagsString: String): List<String> {
        if (tagsString.isBlank()) return emptyList()
        return tagsString.split("||")
    }
}