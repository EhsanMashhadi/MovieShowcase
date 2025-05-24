package software.ehsan.movieshowcase.core.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun getCurrentDateFormatted(): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val currentDate = Date()
    return dateFormat.format(currentDate)
}