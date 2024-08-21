package com.waleed.galbums.utils.extensions

import android.content.Intent
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle


inline fun <reified T : java.io.Serializable> Bundle.toSerializable(key: String): T? = when {
    SDK_INT >= 33 -> getSerializable(key, T::class.java)
    else -> @Suppress("DEPRECATION") getSerializable(key) as T?
}

inline fun <reified T : java.io.Serializable> Intent.toSerializable(key: String): T? = when {
    SDK_INT >= 33 -> getSerializableExtra(key, T::class.java)
    else -> @Suppress("DEPRECATION") getSerializableExtra(key) as? T
}