package com.waleed.galbums.ui.fragments.detail

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.waleed.galbums.models.Album
import com.waleed.galbums.models.GalleryItem
import com.waleed.galbums.models.enums.AlbumCategory
import com.waleed.galbums.repos.albums.AlbumsRepo
import com.waleed.galbums.utils.sealed.DataResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumDetailViewModel @Inject constructor(
    private val albumsRepo: AlbumsRepo
) : ViewModel() {
    lateinit var selectedAlbum: Album


    private val _albumContent =
        MutableStateFlow<DataResult<List<GalleryItem>>>(DataResult.Loading())
    val albumContent = _albumContent.asStateFlow()


    fun fetchAlbumContentById() {
       // Log.d("AlbumDetailVM", "fetchAlbumContentById: id -> ${selectedAlbum.id}, cat -> ${selectedAlbum.category}")
        viewModelScope.launch {
            _albumContent.value = DataResult.Loading()
            when (selectedAlbum.category) {
                AlbumCategory.ALL_IMAGES -> {
                    albumsRepo.findImagesByAlbumId(selectedAlbum.id).collectLatest {
                        _albumContent.value = DataResult.Success(it)
                    }
                }
               AlbumCategory.ALL_VIDEOS -> {
                    albumsRepo.findVideosByAlbumId(selectedAlbum.id).collectLatest {
                        _albumContent.value = DataResult.Success(it)
                    }
                }
                else -> {
                    albumsRepo.findImageVideosByBucketIdFlow(selectedAlbum.id).collectLatest {
                        _albumContent.value = it
                    }
                }
            }

        }
    }
}