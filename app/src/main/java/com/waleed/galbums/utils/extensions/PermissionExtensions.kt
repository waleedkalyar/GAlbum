package com.waleed.galbums.utils.extensions

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.Manifest.permission.READ_MEDIA_VIDEO
import android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
import android.app.Activity
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat


fun AppCompatActivity.requestMediaPermissions(onPermissionsGranted: (isGranted: Boolean) -> Unit) {

    val requestPermissions =
        this.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->
            onPermissionsGranted.invoke(results.all { it.value })
        }

    // Permission request logic
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        requestPermissions.launch(
            arrayOf(
                READ_MEDIA_IMAGES,
                READ_MEDIA_VIDEO,
                READ_MEDIA_VISUAL_USER_SELECTED
            )
        )
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        requestPermissions.launch(arrayOf(READ_MEDIA_IMAGES, READ_MEDIA_VIDEO))
    } else {
        requestPermissions.launch(arrayOf(READ_EXTERNAL_STORAGE))
    }
}

fun Activity.checkMediaPermissions(): Boolean {
    return when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
            // Android 14 (API level 34) or higher
            ContextCompat.checkSelfPermission(
                this,
                READ_MEDIA_VISUAL_USER_SELECTED
            ) == PERMISSION_GRANTED
        }

        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
            // Android 13 (API level 33) or higher
            ContextCompat.checkSelfPermission(this, READ_MEDIA_IMAGES) == PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, READ_MEDIA_VIDEO) == PERMISSION_GRANTED
        }

        else -> {
            // Android 10 (API level 29) & lower to Android 12 (API level 32)
            ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) == PERMISSION_GRANTED
        }
    }
}
