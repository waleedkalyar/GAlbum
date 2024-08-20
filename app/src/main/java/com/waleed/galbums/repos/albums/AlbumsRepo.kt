package com.waleed.galbums.repos.albums

import com.waleed.galbums.models.Album
import kotlinx.coroutines.flow.Flow


interface AlbumsRepo {

    fun fetchAlbums():Flow<List<Album>>
}