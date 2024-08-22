package com.waleed.galbums.ui.activities.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.waleed.galbums.models.Album
import com.waleed.galbums.models.GalleryItem
import com.waleed.galbums.repos.albums.AlbumsRepo
import com.waleed.galbums.utils.sealed.DataResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val albumsRepo: AlbumsRepo,
) : ViewModel() {

    private val _albumsData = MutableStateFlow<DataResult<List<Album>>>(DataResult.Loading())
    val albumsData = _albumsData.asStateFlow()

    fun intiAlbums() {
        viewModelScope.launch {
            _albumsData.value = DataResult.Loading()
            albumsRepo.findAllImageVideoAlbumsFlow().collectLatest {
                _albumsData.value = it
            }
        }
    }

}