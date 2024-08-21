package com.waleed.galbums.repos.albums

import com.waleed.galbums.datasources.gallery.GalleryDataSource
import com.waleed.galbums.models.Album
import com.waleed.galbums.models.GalleryItem
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AlbumsRepoImpl @Inject constructor(
 private val  gDataSource:GalleryDataSource
) : AlbumsRepo {
    override fun findImageAlbumsFlow(): Flow<List<Album>> {
        return gDataSource.findImageAlbumsFlow()
    }

    override fun findImagesByAlbumId(albumId: String): Flow<List<GalleryItem>> {
      return gDataSource.findImagesByAlbumId(albumId)
    }

    override fun findVideoAlbumsFlow(): Flow<List<Album>> {
     return  gDataSource.findVideoAlbumsFlow()
    }

    override fun findVideosByAlbumId(albumId: String): Flow<List<GalleryItem>> {
      return gDataSource.findVideosByAlbumId(albumId)
    }

    override fun findAllImageVideoAlbumsFlow(): Flow<List<Album>> {
      return gDataSource.findAllImageVideoAlbumsFlow()
    }

    override fun findImageVideosByBucketIdFlow(albumId: String): Flow<List<GalleryItem>> {
      return gDataSource.findImageVideosByBucketIdFlow(albumId)
    }


}