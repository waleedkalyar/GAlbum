package com.waleed.galbums.repos.albums

import com.waleed.galbums.datasources.gallery.GalleryDataSource
import com.waleed.galbums.models.Album
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AlbumsRepoImpl @Inject constructor(
 private val  gDataSource:GalleryDataSource
) : AlbumsRepo {
    override fun fetchAlbums(): Flow<List<Album>> {
        //TODO: add check for db or content provider currently form content providers...
       return gDataSource.findAlbumsFlow()
    }

}