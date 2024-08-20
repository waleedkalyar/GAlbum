package com.waleed.galbums.datasources.gallery

import com.waleed.galbums.models.Album
import kotlinx.coroutines.flow.Flow

interface GalleryDataSource {
    fun findAlbumsFlow(): Flow<List<Album>>
}