package com.waleed.galbums.utils.extensions

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.waleed.galbums.R
import java.text.DecimalFormat
import java.util.Locale
import kotlin.math.log10
import kotlin.math.pow

fun Int.toFileSize(): String {
    val size = this.toLong()
    if (size <= 0) return "0"
    val units = arrayOf("B", "kB", "MB", "GB", "TB")
    val digitGroups = (log10(size.toDouble()) / log10(1024.0)).toInt()
    return DecimalFormat("#,##0.#").format(size / 1024.0.pow(digitGroups.toDouble())) + " " + units[digitGroups]
}


fun <T> MutableList<T>.updateItemPosition(currentPosition: Int, newPosition: Int): MutableList<T> {
    if (currentPosition >= this.size || newPosition >= this.size) return this
    val replacementItem = this[currentPosition]
    val existingItem = this[newPosition]
    this.removeAt(currentPosition)
    this.removeAt(newPosition)

    this[newPosition] = replacementItem
    this[currentPosition] = existingItem

    return this
}

fun String.toFolderDrawable(context: Context): Drawable? {
    var res = 0
    res = if (this.equals("All Images", true)) {
        R.drawable.ic_photo_album
    } else if (this.equals("All Videos", true)) {
        R.drawable.ic_videos_album
    } else if (this.contains("Download", true)) {
        R.drawable.ic_downloads_album
    } else if (this.contains("Photos", true)) {
        R.drawable.ic_photo_album
    } else if (this.contains("Pictures", true)) {
        R.drawable.ic_photo_album
    } else if (this.contains("Videos", true)) {
        R.drawable.ic_photo_album
    } else R.drawable.ic_folder

    return ContextCompat.getDrawable(context, res)
}

@SuppressLint("DefaultLocale")
fun Long.toMinutesSeconds(): String {
    val totalSeconds = this / 1000
    val minutes = (totalSeconds / 60).toInt()
    val seconds = (totalSeconds % 60).toInt()
    return String.format("%02d:%02d", minutes, seconds, Locale.getDefault())
}



