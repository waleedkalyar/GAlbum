package com.waleed.galbums.repos.albums

import com.waleed.galbums.datasources.gallery.GalleryDataSource
import javax.inject.Inject

class AlbumsRepoImpl @Inject constructor(
    gDataSource:GalleryDataSource
) : AlbumsRepo {

}