package com.waleed.galbums.utils.extensions

import java.text.DecimalFormat
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