package com.waleed.galbums.ui.fragments.detail

import androidx.lifecycle.ViewModel
import com.waleed.galbums.models.Album
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AlbumDetailViewModel @Inject constructor() : ViewModel() {

    lateinit var selectedAlbum: Album
}