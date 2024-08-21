package com.waleed.galbums.models

import android.net.Uri
import com.waleed.galbums.models.enums.MediaType

data class GalleryItem(
    val id: String,
    val name: String,
    val size: Int = 0,
    val type: MediaType = MediaType.NONE,
    val duration: Long = 0,
    var uri: Uri? = null,
    var time:Long = 0,
)
