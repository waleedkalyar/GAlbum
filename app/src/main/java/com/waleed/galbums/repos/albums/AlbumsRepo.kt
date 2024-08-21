package com.waleed.galbums.repos.albums

import com.waleed.galbums.models.Album
import com.waleed.galbums.models.GalleryItem
import kotlinx.coroutines.flow.Flow


interface AlbumsRepo {
    fun findImageAlbumsFlow(): Flow<List<Album>>
    fun findImagesByAlbumId(albumId: String) : Flow<List<GalleryItem>>

    fun findVideoAlbumsFlow(): Flow<List<Album>>
    fun findVideosByAlbumId(albumId: String) : Flow<List<GalleryItem>>

    fun findAllImageVideoAlbumsFlow(): Flow<List<Album>>
    fun findImageVideosByBucketIdFlow(albumId:String): Flow<List<GalleryItem>>
}